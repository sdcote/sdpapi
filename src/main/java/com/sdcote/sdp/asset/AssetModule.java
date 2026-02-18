package com.sdcote.sdp.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.SDP;
import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides simplified access to the Asset Module through web service calls.
 */
public class AssetModule {

    private static final String ENDPOINT = "/assets";

    /* This utility class should not be instantiated */
    private AssetModule() {
    }


    /**
     *
     * @param credentials The client credentials.
     * @return a list of Asset objects, or empty if errors occurred. Never returns null.
     */
    public static List<Asset> listAssets(ClientCredentials credentials) {
        List<Asset> retval = new ArrayList<>();

        // Get the access token for our web service calls.
        String accessToken = SDP.getAccessToken(credentials);
        Log.debug(String.format("AssetModule listAssets token: %s", accessToken));


        if (StringUtil.isNotBlank(accessToken)) {
            // Make the web service call
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // The API expects the token in the format: Zoho-oauthtoken <token>
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SDP.getServiceUrl() + ENDPOINT))
                    .header("Authorization", "Zoho-oauthtoken " + accessToken)
                    .header("Accept", "application/vnd.manageengine.sdp.v3+json")
                    .GET()
                    .build();

            try {
                SDP.throttle();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    try {
                        retval = parseAssets(response.body());
                    } catch (IOException e) {
                        Log.fatal("Could not parse Asset response.", e);
                        if (Log.isLogging(Log.DEBUG_EVENTS)) FileUtil.stringToFile(response.body(), "listAssets.json");
                    }
                } else {
                    Log.fatal("Call to Asset service resulted in an HTTP response code: " + response.statusCode());
                    if (Log.isLogging(Log.DEBUG_EVENTS)) Log.fatal("Failed response body: \n" + response.body());
                }

            } catch (Exception e) {
                Log.fatal("Web service call failed.", e);
            }
        } else {
            Log.fatal("Could not retrieve access token.");
        }

        return retval;
    }


    public static List<Asset> parseAssets(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // SDP often sends integers as strings for IDs; this helps coerce them if needed
        // mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);

        // Marshal the JSON into the wrapper object
        AssetResponse response = mapper.readValue(jsonResponse, AssetResponse.class);

        if (response.getAssets() == null) {
            throw new IOException("No assets found in response. Check 'response_status' for errors.");
        }

        return response.getAssets();
    }

}
