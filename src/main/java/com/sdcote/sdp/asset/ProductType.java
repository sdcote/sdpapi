package com.sdcote.sdp.asset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdcote.sdp.SDPReference;

/**
 * Represents the comprehensive 'product_type' definition in ServiceDesk Plus.
 * Contains nested configuration for extensions, inheritance, and UI visibility.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductType {

    // --- Core Identifiers ---
    private String id;
    private String name;
    private String displayName;
    private String displayNamePlural;
    private String apiPluralName;
    private String description;
    private String image;

    // --- Classification ---
    private String entityCategory; // e.g., "ASSET"
    private boolean mandatory;

    // --- Nested References ---
    private SDPReference type;     // Reusing standard reference (id, name)
    private ProductCategory category; // Custom inner class (has description/display_name)
    private ProductExtension extension;
    private InheritedProduct inherits;

    // --- Visibility Flags ---
    private boolean parentProductVisibility;
    private boolean siblingProductVisibility;
    private boolean childProductVisibility;

    // ==========================================
    // Nested Static Classes for Inner Objects
    // ==========================================

    /**
     * Represents the 'extension' object detailing API names and icons.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductExtension {
        private String id;
        private String name;
        private String displayName;
        private String displayNamePlural;
        private String apiPluralName;
        private String category;
        private String iconName;

        @JsonProperty("id")
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        @JsonProperty("name")
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @JsonProperty("display_name")
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        @JsonProperty("display_name_plural")
        public String getDisplayNamePlural() { return displayNamePlural; }
        public void setDisplayNamePlural(String displayNamePlural) { this.displayNamePlural = displayNamePlural; }

        @JsonProperty("api_plural_name")
        public String getApiPluralName() { return apiPluralName; }
        public void setApiPluralName(String apiPluralName) { this.apiPluralName = apiPluralName; }

        @JsonProperty("category")
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        @JsonProperty("icon_name")
        public String getIconName() { return iconName; }
        public void setIconName(String iconName) { this.iconName = iconName; }
    }

    /**
     * Represents the 'category' object specific to Product Types.
     * Contains description and display_name which standard SDPReference lacks.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductCategory {
        private String id;
        private String name;
        private String displayName;
        private String description;

        @JsonProperty("id")
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        @JsonProperty("name")
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @JsonProperty("display_name")
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        @JsonProperty("description")
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * Represents the 'inherits' object.
     * Note: The nested 'extension' here is often just an ID wrapper.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InheritedProduct {
        private String id;
        private String name;
        private String displayName;
        private String apiPluralName;
        private ProductExtension extension; // Reusing the Extension class

        @JsonProperty("id")
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        @JsonProperty("name")
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @JsonProperty("display_name")
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        @JsonProperty("api_plural_name")
        public String getApiPluralName() { return apiPluralName; }
        public void setApiPluralName(String apiPluralName) { this.apiPluralName = apiPluralName; }

        @JsonProperty("extension")
        public ProductExtension getExtension() { return extension; }
        public void setExtension(ProductExtension extension) { this.extension = extension; }
    }

    // ==========================================
    // Main Class Getters and Setters
    // ==========================================

    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("display_name")
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    @JsonProperty("display_name_plural")
    public String getDisplayNamePlural() { return displayNamePlural; }
    public void setDisplayNamePlural(String displayNamePlural) { this.displayNamePlural = displayNamePlural; }

    @JsonProperty("api_plural_name")
    public String getApiPluralName() { return apiPluralName; }
    public void setApiPluralName(String apiPluralName) { this.apiPluralName = apiPluralName; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @JsonProperty("image")
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @JsonProperty("entity_category")
    public String getEntityCategory() { return entityCategory; }
    public void setEntityCategory(String entityCategory) { this.entityCategory = entityCategory; }

    @JsonProperty("mandatory")
    public boolean isMandatory() { return mandatory; }
    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }

    @JsonProperty("type")
    public SDPReference getType() { return type; }
    public void setType(SDPReference type) { this.type = type; }

    @JsonProperty("category")
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    @JsonProperty("extension")
    public ProductExtension getExtension() { return extension; }
    public void setExtension(ProductExtension extension) { this.extension = extension; }

    @JsonProperty("inherits")
    public InheritedProduct getInherits() { return inherits; }
    public void setInherits(InheritedProduct inherits) { this.inherits = inherits; }

    @JsonProperty("parent_product_visibility")
    public boolean isParentProductVisibility() { return parentProductVisibility; }
    public void setParentProductVisibility(boolean parentProductVisibility) { this.parentProductVisibility = parentProductVisibility; }

    @JsonProperty("sibling_product_visibility")
    public boolean isSiblingProductVisibility() { return siblingProductVisibility; }
    public void setSiblingProductVisibility(boolean siblingProductVisibility) { this.siblingProductVisibility = siblingProductVisibility; }

    @JsonProperty("child_product_visibility")
    public boolean isChildProductVisibility() { return childProductVisibility; }
    public void setChildProductVisibility(boolean childProductVisibility) { this.childProductVisibility = childProductVisibility; }

    @Override
    public String toString() {
        return "ProductType [name=" + name + ", id=" + id + "]";
    }
}