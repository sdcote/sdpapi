package com.sdcote.sdp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Handles SDP date/time objects which contain both a formatted
 * display string and a raw millisecond timestamp.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SDPDateTime {

    private String displayValue;
    private Long value; // Raw timestamp in milliseconds

    @JsonProperty("display_value")
    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonProperty("value")
    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return displayValue + " (" + value + ")";
    }
}