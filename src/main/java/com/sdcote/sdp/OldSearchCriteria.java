package com.sdcote.sdp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the 'search_criteria' tree.
 * Can represent a LEAF (field + condition + value) or a BRANCH (logical_operator + children).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OldSearchCriteria {

    // --- Leaf Node Fields ---
    private String field;
    private String condition;
    private Object value;       // For single value operations (is, contains)
    private List<Object> values; // For multi-value operations (is_in, not_in)

    // --- Branch Node Fields ---
    private String logicalOperator; // "and" or "or"
    private List<OldSearchCriteria> children;


    public OldSearchCriteria() {
    }

    /**
     * Convenience constructor for a simple condition.
     *
     * @param field     The API field name (e.g., "name", "site.name")
     * @param condition The operator (e.g., "is", "contains", "starts_with", "is_in")
     * @param value     The value to check against
     */
    public OldSearchCriteria(String field, String condition, Object value) {
        this.field = field;
        this.condition = condition;
        if (value instanceof List) {
            this.values = (List<Object>) value;
        } else {
            this.value = value;
        }
    }

    /**
     * Convenience constructor for a logical group (AND/OR).
     */
    public OldSearchCriteria(String logicalOperator) {
        this.logicalOperator = logicalOperator;
        this.children = new ArrayList<>();
    }


    public OldSearchCriteria addChild(OldSearchCriteria child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
        return this; // For chaining
    }


    @JsonProperty("field")
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @JsonProperty("condition")
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @JsonProperty("value")
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @JsonProperty("values")
    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @JsonProperty("logical_operator")
    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    @JsonProperty("children")
    public List<OldSearchCriteria> getChildren() {
        return children;
    }

    public void setChildren(List<OldSearchCriteria> children) {
        this.children = children;
    }
}