package com.sdcote.sdp.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdcote.sdp.SDPDateTime;
import com.sdcote.sdp.SDPReference;
import com.sdcote.sdp.SDPUser;

/**
 * Represents a single Asset entity from ServiceDesk Plus Cloud.
 * Uses @JsonIgnoreProperties to safely skip unmapped fields.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset {

    private String loan;
    private String acknowledgement;
    private Double totalCost;
    private boolean retainUserSite;
    private Double currentCost;
    private Double purchaseCost;
    private String barcode;
    private Double operationalCost;
    private String recentScanSource;
    private String purchaseOrder;
    private String location;
    private boolean isLoaned;
    private boolean isLoanable;


    // --- Core Fields ---
    private String id;
    private String name;

    // --- Reference Fields (Lookups) ---
    private SDPReference state;
    private SDPReference site;
    private Category category;

    private SDPReference type;

    private Product product;
    private ProductType productType;

    // --- User References ---
    private SDPUser createdBy;
    private SDPUser lastUpdatedBy;

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
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
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
    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @JsonProperty("created_by")
    public SDPUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(SDPUser createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("last_updated_by")
    public SDPUser getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(SDPUser lastUpdatedBy) {
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


    @JsonProperty("loan")
    public String getLoan() {
        return loan;
    }

    public void setLoan(String loan) {
        this.loan = loan;
    }

    @JsonProperty("acknowledgement")
    public String getAcknowledgement() {
        return acknowledgement;
    }

    public void setAcknowledgement(String acknowledgement) {
        this.acknowledgement = acknowledgement;
    }

    @JsonProperty("total_cost")
    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    @JsonProperty("retain_user_site")
    public boolean isRetainUserSite() {
        return retainUserSite;
    }

    public void setRetainUserSite(boolean retainUserSite) {
        this.retainUserSite = retainUserSite;
    }

    @JsonProperty("current_cost")
    public Double getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(Double currentCost) {
        this.currentCost = currentCost;
    }

    @JsonProperty("purchase_cost")
    public Double getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(Double purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    @JsonProperty("barcode")
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @JsonProperty("operational_cost")
    public Double getOperationalCost() {
        return operationalCost;
    }

    public void setOperationalCost(Double operationalCost) {
        this.operationalCost = operationalCost;
    }

    @JsonProperty("recent_scan_source")
    public String getRecentScanSource() {
        return recentScanSource;
    }

    public void setRecentScanSource(String recentScanSource) {
        this.recentScanSource = recentScanSource;
    }

    @JsonProperty("purchase_order")
    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("is_loaned")
    public boolean isLoaned() {
        return isLoaned;
    }

    public void setLoaned(boolean loaned) {
        isLoaned = loaned;
    }

    @JsonProperty("is_loanable")
    public boolean isLoanable() {
        return isLoanable;
    }

    public void setLoanable(boolean loanable) {
        isLoanable = loanable;
    }


    @Override
    public String toString() {
        return String.format("Asset [ID=%s, Name=%s, Site=%s, State=%s]",
                id, name, (site != null ? site.getName() : "N/A"), (state != null ? state.getName() : "N/A"));
    }
}