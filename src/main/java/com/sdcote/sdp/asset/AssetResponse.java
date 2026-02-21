package com.sdcote.sdp.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetResponse {

    private List<Asset> assets;
    private List<Map<String, Object>> responseStatus;
    private ResponseListInfo listInfo;

    @JsonProperty("assets")
    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @JsonProperty("response_status")
    public List<Map<String, Object>> getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(List<Map<String, Object>> responseStatus) {
        this.responseStatus = responseStatus;
    }

    @JsonProperty("list_info")
    public ResponseListInfo getListInfo() {
        return listInfo;
    }

    public void setListInfo(ResponseListInfo listInfo) {
        this.listInfo = listInfo;
    }

    // --- Inner Class for the Response List ListInfo ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseListInfo {
        private boolean hasMoreRows;
        private int rowCount;
        private int startIndex;

        @JsonProperty("has_more_rows")
        public boolean isHasMoreRows() {
            return hasMoreRows;
        }

        public void setHasMoreRows(boolean hasMoreRows) {
            this.hasMoreRows = hasMoreRows;
        }

        @JsonProperty("row_count")
        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        @JsonProperty("start_index")
        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }
    }
}