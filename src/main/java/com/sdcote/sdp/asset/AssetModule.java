package com.sdcote.sdp.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdcote.sdp.*;
import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
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
     * Retrieve only one page of data.
     *
     * @param credentials
     * @param oldListInfo
     * @return
     */
    public static List<Asset> retrieveAssets(ClientCredentials credentials, OldListInfo oldListInfo) {
        return getAssets(credentials, oldListInfo);
    }


    /**
     * Retrieve all data, regardless of rowcount.
     *
     * @param credentials
     * @param OldListInfo
     * @return
     */
    public static List<Asset> retrieveAllAssets(ClientCredentials credentials, OldListInfo OldListInfo) {
        List<Asset> retval = new ArrayList<>();
        OldListInfo myOldListInfo = new OldListInfo(OldListInfo); // make a copy that we can change

        while (true) {
            List<Asset> pageAssets = getAssets(credentials, myOldListInfo);
            if (pageAssets == null || pageAssets.isEmpty()) {
                break;
            }
            retval.addAll(pageAssets);

            // Check for End of Data
            if (pageAssets.size() < myOldListInfo.getRowCount()) {
                break; // Fewer records than requested means this is the last page
            }
            // Advance to Next Page
            myOldListInfo.incrementPage();
        }

        return retval;
    }


    /**
     *
     * @param credentials The client credentials.
     * @return a list of Asset objects, or empty if errors occurred. Never returns null.
     */
    private static List<Asset> getAssets(ClientCredentials credentials, OldListInfo oldListInfo) {
        List<Asset> retval = new ArrayList<>();

        // Get the access token for our web service calls.
        String accessToken = SDP.getAccessToken(credentials);
        Log.debug(String.format("AssetModule listAssets token: %s", accessToken));

        if (StringUtil.isNotBlank(accessToken)) {
            // Make the web service call
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(generateRequestUri(oldListInfo))
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


    /**
     * Generate a URI from the service url, the endpoint and the provided list information.
     *
     * @param oldListInfo The list information for the request.
     * @return a URI suitable for the HttpRequest.
     */
    private static URI generateRequestUri(OldListInfo oldListInfo) {
        StringBuilder b = new StringBuilder();
        b.append(SDP.getServiceUrl());
        b.append(ENDPOINT);

        if (oldListInfo != null) {
            b.append("?");
            b.append(oldListInfo.toQueryParam());
        }

        return URI.create(b.toString());
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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

}
