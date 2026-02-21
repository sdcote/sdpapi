package com.sdcote.sdp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to generate the 'list_info' query parameter for ServiceDesk Plus V3 API.
 * Handles pagination (row_count, start_index), sorting, and search filters.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OldListInfo implements Cloneable {

    public static final String ASCENDING = "asc";
    public static final String DECENDING = "desc";

    private int rowCount = 50; // Default to 50
    private int startIndex = 1; // Default to 1 (1-based index)
    private String sortField;
    private String sortOrder; // "asc" or "desc"
    private Map<String, Object> searchFields;
    private OldSearchCriteria oldSearchCriteria;
    private List<String> fieldsRequired;

    public OldListInfo() {
    }

    public OldListInfo(int rowCount, int startIndex) {
        this.rowCount = rowCount;
        this.startIndex = startIndex;
    }


    /**
     * Copy Constructor: Performs a deep copy of the searchFields map.
     *
     * @param other The original OldListInfo instance to copy from.
     */
    public OldListInfo(OldListInfo other) {
        if (other == null) {
            throw new IllegalArgumentException("Source OldListInfo cannot be null");
        }

        // Copy primitives and immutable Strings
        this.rowCount = other.rowCount;
        this.startIndex = other.startIndex;
        this.sortField = other.sortField;
        this.sortOrder = other.sortOrder;
        this.oldSearchCriteria = other.oldSearchCriteria;
        this.fieldsRequired = other.fieldsRequired;

        // Perform deep copy of the Map
        if (other.searchFields != null) {
            this.searchFields = SDP.deepCopyMap(other.searchFields);
        } else {
            this.searchFields = new HashMap<>();
        }
    }

    /**
     * Serializes this object to a JSON string and URL-encodes it for use in a
     * GET request query parameter.
     *
     * @return encoded string (e.g., "%7B%22row_count%22%3A100...%7D")
     */
    public String toQueryParam() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1. Generate the inner JSON
            String innerJson = mapper.writeValueAsString(this);

            // 2. Wrap it in the required root key "list_info"
            String wrappedJson = "{\"list_info\":" + innerJson + "}";

            // 3. URL Encode the entire JSON string
            String encodedJson = URLEncoder.encode(wrappedJson, StandardCharsets.UTF_8);

            // 4. Return the full query parameter key-value pair
            return "input_data=" + encodedJson;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OldListInfo to JSON", e);
        }
    }

    /**
     * Increment the start index to the next page of data.
     */
    public void incrementPage() {
        startIndex += rowCount;
    }

    @JsonProperty("row_count")
    public int getRowCount() {
        return rowCount;
    }

    /** How many rows are to be retrieved at a time (default=50)*/
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

    @JsonProperty("sort_field")
    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    @JsonProperty("sort_order")
    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @JsonProperty("search_fields")
    public Map<String, Object> getSearchFields() {
        return searchFields;
    }
    public void setSearchFields(Map<String, Object> searchFields) {
        this.searchFields = searchFields;
    }

    public void putSearchField(String key, String value) {
        if (this.searchFields == null) this.searchFields = new HashMap<>();
        this.searchFields.put(key, value);
    }

    @JsonProperty("search_criteria")
    public OldSearchCriteria getSearchCriteria() { return oldSearchCriteria; }
    public void setSearchCriteria(OldSearchCriteria oldSearchCriteria) { this.oldSearchCriteria = oldSearchCriteria; }


    /**
     * Helper to easily set required fields.
     * usage: listInfo.setFieldsRequired("id", "name", "site");
     */
    public void setFieldsRequired(String... fields) {
        this.fieldsRequired = Arrays.asList(fields);
    }

    @JsonProperty("fields_required")
    public List<String> getFieldsRequired() { return fieldsRequired; }
    public void setFieldsRequired(List<String> fieldsRequired) { this.fieldsRequired = fieldsRequired; }
}