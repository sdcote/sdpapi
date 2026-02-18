package com.sdcote.sdp.asset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdcote.sdp.SDPReference;

/**
 * Represents a specific Product model (e.g., "HP Z440 Workstation").
 * Connects the physical asset to its abstract Product Type definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    // --- Core Identifiers ---
    private String id;
    private String name;
    private String manufacturer;
    private String partNo;
    private String glCode; // General Ledger Code

    // --- Classification ---
    // Reuses the complex ProductType class defined previously
    private ProductType productType;

    // Reuses the Category with description support from ProductType
    private ProductType.ProductCategory category;

    private SDPReference type; // e.g. "Asset"

    // --- Status & Config ---
    private boolean notInPo; // "not_in_po"
    private SDPReference software; // often null for hardware
    private String defaultImage;

    // ==========================================
    // Getters and Setters
    // ==========================================

    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("manufacturer")
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    @JsonProperty("part_no")
    public String getPartNo() { return partNo; }
    public void setPartNo(String partNo) { this.partNo = partNo; }

    @JsonProperty("gl_code")
    public String getGlCode() { return glCode; }
    public void setGlCode(String glCode) { this.glCode = glCode; }

    @JsonProperty("not_in_po")
    public boolean isNotInPo() { return notInPo; }
    public void setNotInPo(boolean notInPo) { this.notInPo = notInPo; }

    @JsonProperty("default_image")
    public String getDefaultImage() { return defaultImage; }
    public void setDefaultImage(String defaultImage) { this.defaultImage = defaultImage; }

    @JsonProperty("product_type")
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }

    @JsonProperty("category")
    public ProductType.ProductCategory getCategory() { return category; }
    public void setCategory(ProductType.ProductCategory category) { this.category = category; }

    @JsonProperty("type")
    public SDPReference getType() { return type; }
    public void setType(SDPReference type) { this.type = type; }

    @JsonProperty("software")
    public SDPReference getSoftware() { return software; }
    public void setSoftware(SDPReference software) { this.software = software; }

    @Override
    public String toString() {
        return "Product [name=" + name + ", manufacturer=" + manufacturer + "]";
    }
}