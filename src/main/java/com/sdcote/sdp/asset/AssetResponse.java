package com.sdcote.sdp.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Root wrapper for the API response.
 * Handles the 'assets' list and the 'response_status' array.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetResponse {

    private List<Asset> assets;
    private List<Map<String, Object>> responseStatus;

    @JsonProperty("assets")
    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    /**
     * Maps response_status as a List to handle API array returns.
     */
    @JsonProperty("response_status")
    public List<Map<String, Object>> getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(List<Map<String, Object>> responseStatus) {
        this.responseStatus = responseStatus;
    }
}