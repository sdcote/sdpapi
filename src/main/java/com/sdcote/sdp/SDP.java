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

    private static String OAUTH_URL = "https://accounts.zoho.com/oauth/v2";
    private static String SERVICE_URL = "https://sdpondemand.manageengine.com/api/v3";
    private static OAuthAccessTokenTracker refreshTokenTracker = new OAuthAccessTokenTracker(OAUTH_URL, TOKEN_EXPIRY_WINDOW);

    private static final int MAX_CALLS_PER_MINUTE = 20;
    private static final long INTERVAL_MS = 60000L / MAX_CALLS_PER_MINUTE; // 2000ms
    private static long lastCallTime = 0;


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
     * Blocks the current thread to ensure a strict, even interval between API calls.
     * Must be called immediately before executing the HTTP request.
     */
    public static synchronized void throttle()  {
        long now = System.currentTimeMillis();
        long timeSinceLastCall = now - lastCallTime;

        if (timeSinceLastCall < INTERVAL_MS) {
            long sleepTime = INTERVAL_MS - timeSinceLastCall;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException("Throttle interupted",e);
            }
        }

        // Record the time after the sleep completes
        lastCallTime = System.currentTimeMillis();
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
