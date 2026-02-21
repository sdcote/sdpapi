package com.sdcote.sdp;

import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.log.Log;
import coyote.commons.vault.Vault;
import coyote.commons.vault.VaultBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static helper methods for working with ServiceDesk Plus.
 */
public class SDP {

    private static final String RESPONSE_STATUS_FIELD = "response_status";
    private static final String LISTINFO_FIELD = "list_info";

    /** The number of seconds before the access token expiration we want the token refresh to occur.*/
    private static final long TOKEN_EXPIRY_WINDOW = 60L;

    private static String OAUTH_URL = "https://accounts.zoho.com/oauth/v2";
    private static String SERVICE_URL = "https://sdpondemand.manageengine.com/api/v3";
    private static OAuthAccessTokenTracker refreshTokenTracker = new OAuthAccessTokenTracker(OAUTH_URL, TOKEN_EXPIRY_WINDOW);

    // Throttling attributes
    private static final int MAX_CALLS_PER_MINUTE = 10;
    private static final long INTERVAL_MS = 60000L / MAX_CALLS_PER_MINUTE;
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


    /**
     * Request an OAuth access token for the given client.
     *
     * @param clientCredentials THe credentials representing the client (client ID, secret and refresh token)
     *
     * @return an OAuth access token for making requests to the API.
     */
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




    public static ApiResponse callApi(ClientCredentials credentials,String endpoint, ListInfo listInfo,String resultField) {
        ApiResponse apiResponse = null;

        // Get the access token for our web service calls.
        String accessToken = SDP.getAccessToken(credentials);
        Log.debug(String.format("AssetModule listAssets token: %s", accessToken));

        if (StringUtil.isNotBlank(accessToken)) {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(generateUri(endpoint,listInfo))
                    .header("Authorization", "Zoho-oauthtoken " + accessToken)
                    .header("Accept", "application/vnd.manageengine.sdp.v3+json")
                    .GET()
                    .build();

            apiResponse = new ApiResponse(request);

            try {
                SDP.throttle();
                apiResponse.transactionStart();
                apiResponse.requestStart();
                HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                apiResponse.requestEnd();

                final int status = httpResponse.statusCode();
                apiResponse.setStatusCode(status);

                // Debug messages
                if (Log.isLogging(Log.DEBUG_EVENTS)) {
                    Log.debug(String.format("Request:%n   %s%nResponse:%n    %s", request.toString(), status));
                    if ((status >= 200) && (status < 300)) {
                        Log.debug(String.format("Success - %s", status));
                    } else if ((status >= 300) && (status < 400)) {
                        Log.debug(String.format("Unexpected Response - %s", status));
                    } else if ((status >= 400) && (status < 500)) {
                        Log.debug(String.format("Access error - %s", status));
                    } else if (status >= 500) {
                        Log.debug(String.format("Server error - %s", status));
                    }
                }

                // Status of a 301 or a 302, look for a Location: header in the response and use that URL
                if (status >= 300 && status < 400) {
                    apiResponse.setLink(httpResponse.headers().firstValue("Location").toString());
                }

                if (status == 200) {
                    String body = httpResponse.body();

                    Log.debug(String.format("Marshaling response body of '%s%s", body.substring(0, Math.min(body.length(), 500)), body.length() <= 500 ? "'" : " ...'"));

                    // Parse the body into frames
                    List<DataFrame> frames = null;
                    apiResponse.parseStart();
                    try {
                        frames = JSONMarshaler.marshal(body);
                    } catch (Exception e) {
                        Log.fatal("Marshaling error.", e);
                    } finally {
                        apiResponse.parseEnd();
                    }

                    // Because it is possible for responses to have multiple set of data,
                    // make sure just to retrieve the first full frame of data
                    if (frames != null && !frames.isEmpty()) {
                        if (frames.size() > 1) {
                            Log.error("The response contained more than one object - only using first response object");
                        }
                        final DataFrame responseFrame = frames.get(0);

                        final DataFrame results = (DataFrame) responseFrame.getObject(resultField);
                        apiResponse.setResponseFrame( (DataFrame) responseFrame.getObject(RESPONSE_STATUS_FIELD));
                        apiResponse.setListInfoFrame( (DataFrame) responseFrame.getObject(LISTINFO_FIELD));

                        if (results != null) {
                            // Multiple results come as an array, single results are their own frame
                            if (results.isArray()) {
                                for (final DataField field : results.getFields()) {
                                    if (field.isFrame()) {
                                        apiResponse.add((DataFrame) field.getObjectValue());
                                    } else {
                                        Log.warn(String.format("Malformed response: array of records contained a %s field: %s ", field.getTypeName(), field));
                                    }
                                }
                            } else {
                                // This is a single result, add it to the return value
                                apiResponse.add(results);
                            }

                        } else {
                            Log.debug("RESPONSE: NO RESPONSE DATA RETURNED");
                        }

                    } else {
                        Log.debug("There were no valid frames in the response body");
                    }

                } else {
                    Log.fatal("Call to Asset service resulted in an HTTP response code: " + status);
                    if (Log.isLogging(Log.DEBUG_EVENTS)) Log.fatal("Failed response body: \n" + httpResponse.body());
                }

            } catch (Exception e) {
                Log.fatal("Web service call failed.", e);
            } finally {
                apiResponse.transactionEnd();
            }
        } else {
            Log.fatal("Could not retrieve access token.");
        }

        return apiResponse;
    }



    /**
     * Generate a URI from the service url, the endpoint and the provided list information.
     *
     * @param endpoint The service endpoint
     * @param listInfo The list information for the request.
     * @return a URI suitable for the HttpRequest.
     */
    private static URI generateUri(String endpoint, ListInfo listInfo) {
        StringBuilder b = new StringBuilder();
        b.append(SDP.getServiceUrl());
        b.append(endpoint);
        InputData inputData = new InputData();
        inputData.setListInfo(listInfo);

        if (listInfo != null) {
            b.append("?input_data=");
            b.append(UriUtil.encodeString(inputData.toString()));
        }

        return URI.create(b.toString());
    }


}
