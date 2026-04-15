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

    public static final String RESULTS_FIELD_TAG = "resultfield";
    public static final String SEARCH_CRITERIA_TAG = "searchcriteria";
    public static final String FIELDS_REQUIRED_TAG = "fieldsrequired";
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




    public static ApiResponse callApi(ClientCredentials credentials, String endpoint, ListInfo listInfo, String resultField) {
        return callApi(credentials, endpoint, "GET", listInfo, null, resultField);
    }


    /**
     * Call the ServiceDesk API.
     *
     * @param credentials the client credentials to use for the API call.
     * @param endpoint    the endpoint to call.
     * @param method      the HTTP method to use (e.g., "GET", "PUT", "POST").
     * @param listInfo    the list information for the request (optional).
     * @param body        the request body (optional).
     * @param resultField the field in the response containing the results.
     * @return the API response.
     */
    public static ApiResponse callApi(ClientCredentials credentials, String endpoint, String method, ListInfo listInfo, String body, String resultField) {
        ApiResponse apiResponse = null;

        // Get the access token for our web service calls.
        String accessToken = SDP.getAccessToken(credentials);
        Log.debug(String.format("AssetModule token: %s", accessToken));

        if (StringUtil.isNotBlank(accessToken)) {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .header("Authorization", "Zoho-oauthtoken " + accessToken)
                    .header("Accept", "application/vnd.manageengine.sdp.v3+json");

            if ("PUT".equalsIgnoreCase(method)) {
                requestBuilder.uri(URI.create(SDP.getServiceUrl() + endpoint));
                if (body != null) {
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body));
                } else {
                    requestBuilder.PUT(HttpRequest.BodyPublishers.noBody());
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                requestBuilder.uri(URI.create(SDP.getServiceUrl() + endpoint));
                if (body != null) {
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
                } else {
                    requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
                }
            } else {
                requestBuilder.uri(generateUri(endpoint, listInfo));
                requestBuilder.GET();
            }

            HttpRequest request = requestBuilder.build();
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
                    Log.debug(String.format("Request:%n            %s%n   Body: %s%nResponse:%n    %s", request.toString(), body, status));
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
                    if (httpResponse.headers().firstValue("Location").isPresent()) {
                        apiResponse.setLink(httpResponse.headers().firstValue("Location").get());
                    }
                }

                if (Log.isLogging(Log.DEBUG_EVENTS) && status >= 400) {
                    Log.debug(String.format("Error Body: %s", httpResponse.body()));
                }

                if (status == 200 || status == 201) {
                    String responseBody = httpResponse.body();

                    Log.debug(String.format("Marshaling response body of '%s%s", responseBody.substring(0, Math.min(responseBody.length(), 500)), responseBody.length() <= 500 ? "'" : " ...'"));

                    // Parse the body into frames
                    List<DataFrame> frames = null;
                    apiResponse.parseStart();
                    try {
                        frames = JSONMarshaler.marshal(responseBody);
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

                        apiResponse.setResponseFrame((DataFrame) responseFrame.getObject(RESPONSE_STATUS_FIELD));
                        apiResponse.setListInfoFrame((DataFrame) responseFrame.getObject(LISTINFO_FIELD));

                        if (StringUtil.isNotBlank(resultField)) {
                            final DataFrame results = (DataFrame) responseFrame.getObject(resultField);
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
                                Log.debug("RESPONSE: NO RESPONSE DATA RETURNED for field: " + resultField);
                            }
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
     * Retrieve a workstation by its name.
     *
     * @param credentials the client credentials to use for the API call.
     * @param name        the name of the workstation to retrieve.
     * @return the Workstation object if found, null otherwise.
     */
    public static Workstation getWorkstationByName(ClientCredentials credentials, String name) {
        Workstation retval = null;
        SearchCriteria criteria = new SearchCriteria("name", "is", name);
        ListInfo listInfo = new ListInfo();
        listInfo.setSearchCriteria(criteria);

        ApiResponse response = callApi(credentials, "/workstations", listInfo, "workstations");

        if (response != null && response.isSuccessful() && response.getResultSize() > 0) {
            retval = new Workstation(response.getFrame(0));
        }

        return retval;
    }


    /**
     * Update the state of a workstation.
     *
     * @param workstation the workstation to update.
     * @param stateName   the name of the state to set (e.g., "Expired").
     * @param credentials the client credentials to use for the API call.
     * @return the updated workstation if successful, null otherwise.
     */
    public static Workstation updateWorkstationState(Workstation workstation, String stateName, ClientCredentials credentials) {
        Workstation retval = null;
        if (workstation != null && workstation.getId() != null && StringUtil.isNotBlank(stateName)) {
            DataFrame state = new DataFrame();
            state.add("name", stateName);
            DataFrame wsUpdate = new DataFrame();
            wsUpdate.add("state", state);
            DataFrame payload = new DataFrame();
            payload.add("workstation", wsUpdate);

            String body = "input_data=" + UriUtil.encodeString(JSONMarshaler.marshal(payload));
            ApiResponse response = callApi(credentials, "/workstation/" + workstation.getId(), "PUT", null, body, "workstation");

            if (response != null && response.isSuccessful() && response.getResultSize() > 0) {
                retval = new Workstation(response.getFrame(0));
            }
        }
        return retval;
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
