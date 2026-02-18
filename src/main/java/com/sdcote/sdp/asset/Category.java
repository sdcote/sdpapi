package com.sdcote.sdp.asset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdcote.sdp.SDPReference;

/**
 * Represents an Asset Category (e.g., "IT", "Non-IT").
 * Extends SDPReference to include the 'description' field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category extends SDPReference {

    private String description;

    @JsonProperty("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), description);
    }
}