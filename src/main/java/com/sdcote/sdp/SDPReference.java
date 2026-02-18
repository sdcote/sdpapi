package com.sdcote.sdp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic class to map ServiceDesk Plus reference objects.
 * nearly all lookup fields (Site, State, Category, Users) return
 * an 'id' and a 'name'.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SDPReference {

    private String id;
    private String name;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return (name != null) ? name : id;
    }
}
