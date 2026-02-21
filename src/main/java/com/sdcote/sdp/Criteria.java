package com.sdcote.sdp;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.dataframe.marshal.MarshalException;

import java.util.List;

/**
 *
 * {@see https://www.manageengine.com/products/service-desk/sdpod-v3-api/getting-started/search-criteria.html}
 */
public class Criteria {
    private static final String FIELD_TAG = "field";
    private static final String CONDITION_TAG = "condition";
    private static final String VALUE_TAG = "value";
    private static final String OPERATOR_TAG = "operator";
    private static final String CHILDREN_TAG = "children";
    private DataFrame dataFrame = new DataFrame();

    /**
     * Constructor from a dataframe. This simply wraps the given frame.
     *
     * @param dataFrame the dataframe to wrap.
     */
    public Criteria(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    /**
     * Constructure from JSON. Wraps the dataframe represented by the JSON
     *
     * @param json the JSON object to wrap.
     * @throws IllegalArgumentException if there were problems parsing the JSON into a DataFrame
     */
    public Criteria(String json) throws IllegalArgumentException {
        try {
            List<DataFrame> frames = JSONMarshaler.marshal(json);
            dataFrame = frames.get(0);
        } catch (MarshalException e) {
            throw new IllegalArgumentException("JSON Marshaler error.", e);
        }
    }

    public Criteria(String field, String condition, String value) {
        setField(field);
        setCondition(condition);
        setValue(value);
    }


    public String getField() {
        return dataFrame.getAsString(FIELD_TAG);
    }

    public Criteria setField(String fieldName) {
        dataFrame.put(FIELD_TAG, fieldName);
        return this;
    }

    public String getCondition() {
        return dataFrame.getAsString(CONDITION_TAG);
    }

    public Criteria setCondition(String condition) {
        dataFrame.put(CONDITION_TAG, condition);
        return this;
    }

    public String getValue() {
        return dataFrame.getAsString(VALUE_TAG);
    }

    public Criteria setValue(String value) {
        dataFrame.put(VALUE_TAG, value);
        return this;
    }

    public String getLogicalOperator() {
        return dataFrame.getAsString(OPERATOR_TAG);
    }

    public Criteria setLogicalOperator(String operator) {
        dataFrame.put(OPERATOR_TAG, operator);
        return this;
    }

    public void addChild(Criteria child) {
        if (!dataFrame.contains(CHILDREN_TAG)) {
            DataFrame array = new DataFrame();
            array.add(child);
            dataFrame.put(CHILDREN_TAG, array);
        } else {
            DataFrame children = (DataFrame) dataFrame.getObject(CHILDREN_TAG);
            children.add(child);
            dataFrame.put(CHILDREN_TAG, children);
        }
    }

    @Override
    public String toString() {
        return JSONMarshaler.marshal(dataFrame);
    }

}
