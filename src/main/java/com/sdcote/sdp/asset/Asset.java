package com.sdcote.sdp.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdcote.sdp.SDPDateTime;
import com.sdcote.sdp.SDPReference;

/**
 * Represents a single Asset entity from ServiceDesk Plus Cloud.
 * Uses @JsonIgnoreProperties to safely skip unmapped fields.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset {

    // --- Core Fields ---
    private String id;
    private String name;

    // --- Reference Fields (Lookups) ---
    private SDPReference state;
    private SDPReference product;
    private SDPReference site;
    private SDPReference category;

    // Note: 'type' usually refers to the Asset Type (e.g., 'Workstation')
    private SDPReference type;

    // Note: 'product_type' is often distinct from 'type' in SDP schema
    private SDPReference productType;

    // --- User References ---
    private SDPReference createdBy;
    private SDPReference lastUpdatedBy;

    // --- Time Fields ---
    private SDPDateTime createdTime;
    private SDPDateTime lastUpdatedTime;

    // --- Getters and Setters ---

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

    @JsonProperty("state")
    public SDPReference getState() {
        return state;
    }

    public void setState(SDPReference state) {
        this.state = state;
    }

    @JsonProperty("product")
    public SDPReference getProduct() {
        return product;
    }

    public void setProduct(SDPReference product) {
        this.product = product;
    }

    @JsonProperty("site")
    public SDPReference getSite() {
        return site;
    }

    public void setSite(SDPReference site) {
        this.site = site;
    }

    @JsonProperty("category")
    public SDPReference getCategory() {
        return category;
    }

    public void setCategory(SDPReference category) {
        this.category = category;
    }

    @JsonProperty("type")
    public SDPReference getType() {
        return type;
    }

    public void setType(SDPReference type) {
        this.type = type;
    }

    @JsonProperty("product_type")
    public SDPReference getProductType() {
        return productType;
    }

    public void setProductType(SDPReference productType) {
        this.productType = productType;
    }

    @JsonProperty("created_by")
    public SDPReference getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(SDPReference createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("last_updated_by")
    public SDPReference getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(SDPReference lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @JsonProperty("created_time")
    public SDPDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(SDPDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @JsonProperty("last_updated_time")
    public SDPDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(SDPDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    // --- Helper for Debugging ---
    @Override
    public String toString() {
        return String.format("Asset [ID=%s, Name=%s, Site=%s, State=%s]",
                id, name, (site != null ? site.getName() : "N/A"), (state != null ? state.getName() : "N/A"));
    }
}