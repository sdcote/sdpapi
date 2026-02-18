package com.sdcote.sdp;

import coyote.commons.log.Log;
import coyote.commons.vault.Vault;
import coyote.commons.vault.VaultBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static helper methods for working with ServiceDesk Plus.
 */
public class SDP {
    private static final long TOKEN_EXPIRY_WINDOW = 10_000L;
    private static final int MAX_CALLS = 30;
    private static final long WINDOW_MS = 60_000; // 1 Minute

    // Circular buffer to store the timestamps of the last 'MAX_CALLS' requests.
    // Initialized to 0, representing "far in the past".
    private static final long[] callTimestamps = new long[MAX_CALLS];
    // Lock object for synchronization
    private static final Object lock = new Object();
    // Pointer to the oldest timestamp slot in the buffer
    private static int headIndex = 0;
    private static String OAUTH_URL = "https://accounts.zoho.com/oauth/v2";
    private static String SERVICE_URL = "https://sdpondemand.manageengine.com/api/v3";

    private static OAuthAccessTokenTracker refreshTokenTracker = new OAuthAccessTokenTracker(OAUTH_URL, TOKEN_EXPIRY_WINDOW);

    /**
     * @return the secrets vault for this project
     */
    public static Vault getVault() {
        Vault retval = null;

        VaultBuilder builder = new VaultBuilder()
                .setProvider("Local") // case-sensitive name of the provider to load
                .setMethod("file")
                .setProperty("filename", "sdp.vault")
                .setProperty("password", System.getProperty("vault.password"));
        try {
            retval = builder.build();
        } catch (Exception e) {
            Log.error("Could not build the vault. Did you forget the `vault.password` system property?", e);
        }

        return retval;
    }


    /**
     * Blocks the calling thread if the rate limit has been exceeded.
     *
     * <p>All web service calls chack this method to ensure that this API does
     * not trigger any flogs at the ServiceDesk data center. Their API is
     * designed for web applications, not high speed automation components, to
     * this API must operate at the rate of a standard, browser-based user.</p>
     *
     * <p>Calling this method causes the thread of execution to block if too
     * many calls have already been made to the ServiceDesk API. This returns
     * immediately if the rate limit allows.</p>
     *
     * <p>This class implements a thread-safe, sliding window rate limiter
     * using a circular buffer (ring buffer). It ensures that no more than 30
     * calls are made within any rolling 60-minute window (implied 60 seconds
     * based on "per minute").</p>
     *
     * <p>It uses a "reservation" strategy:<ol>
     * <li>Threads acquire a lock to check the timestamp of the 30th previous call.</li>
     * <li>If that call was less than 60 seconds ago, the thread calculates the exact wait time needed.</li>
     * <li>The thread reserves the slot by updating the timestamp to the future time when it will execute.</li>
     * <li>The lock is released, and the thread sleeps for the calculated duration (if any) before returning control to the caller.</li>
     * </ol></p>
     */
    public static void throttle() {
        long waitDuration = 0;

        synchronized (lock) {
            long now = System.currentTimeMillis();

            // Look at the timestamp of the oldest call in our window (the one leaving the window soonest)
            long oldestCallTime = callTimestamps[headIndex];

            // Calculate when that slot will become available again
            long availabilityTime = oldestCallTime + WINDOW_MS;

            if (now < availabilityTime) {
                // We are moving too fast. We must wait until the slot opens.
                waitDuration = availabilityTime - now;

                // Reserve this slot for the future time when we wake up.
                // This prevents other threads from "stealing" the slot while we sleep.
                callTimestamps[headIndex] = availabilityTime;
            } else {
                // Slot is ready. Mark it with the current time.
                callTimestamps[headIndex] = now;
            }

            // Move the pointer to the next oldest slot (circular increment)
            headIndex = (headIndex + 1) % MAX_CALLS;
        }

        // Sleep outside the synchronized block to allow other threads to reserve their own slots concurrently.
        if (waitDuration > 0) {
            try {
                Thread.sleep(waitDuration);
            } catch (InterruptedException e) {
                // Restore interrupt status if interrupted during sleep
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * @return the URL of the OAuth token service
     */
    public static String getTokenUrl() {
        return OAUTH_URL;
    }


    /**
     * Set the URL of the Token Service. (Default is the US data center).
     *
     * <p>Because ServiceDesk can run in multiple data centers, the OAuth Token
     * Service may be in a different location.</p>
     *
     * @param tokenUrl The URL to use.
     */
    public static void setTokenUrl(String tokenUrl) {
        OAUTH_URL = tokenUrl;
        refreshTokenTracker = new OAuthAccessTokenTracker(OAUTH_URL, TOKEN_EXPIRY_WINDOW);
    }


    /**
     * @return the root URL of the API service endpoint.
     */
    public static String getServiceUrl() {
        return SERVICE_URL;
    }


    /**
     * Set the root URL of the API endpoint. (Default is the US data center).
     *
     * <p>Because ServiceDesk can run in multiple data centers, the API service
     * endpoint may be in a different location.</p>
     *
     * @param serviceUrl The root of all service requests without the trailing
     *                   backslash (e.g., "https://sdpondemand.manageengine.com/api/v3")
     */
    public static void setServiceUrl(String serviceUrl) {
        SERVICE_URL = serviceUrl;
    }


    public static void registerClient(ClientCredentials clientCredentials) {
        refreshTokenTracker.registerClient(clientCredentials);
    }


    public static String getAccessToken(ClientCredentials clientCredentials) {
        String retval = null;
        try {
            retval = refreshTokenTracker.getAccessToken(clientCredentials);
        } catch (Exception e) {
            Log.error("Failed to retrieve access token", e);
        }
        return retval;
    }


    /**
     * Helper method to recursively deep copy a Map<String, Object>.
     */
    public static Map<String, Object> deepCopyMap(Map<String, Object> original) {
        Map<String, Object> copy = new HashMap<>();

        for (Map.Entry<String, Object> entry : original.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Map) {
                // Recursively copy nested maps
                copy.put(entry.getKey(), deepCopyMap((Map<String, Object>) value));
            } else if (value instanceof List) {
                // Copy nested lists
                copy.put(entry.getKey(), new ArrayList<>((List<?>) value));
            } else {
                // For Strings, Primitives (Integer, Long, etc.), these are
                // effectively immutable, so sharing the reference is safe.
                copy.put(entry.getKey(), value);
            }
        }
        return copy;
    }
}
