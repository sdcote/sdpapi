package com.sdcote.sdp;

import coyote.commons.ArrayUtil;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.DataFrameException;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.dataframe.marshal.MarshalException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * {@see https://www.manageengine.com/products/service-desk/sdpod-v3-api/getting-started/input-data.html}
 */
public class Info {
    private static final String ROW_COUNT_TAG = "row_count";             // number of rows to be returned(maximum row_count = 100)
    private static final String START_INDEX_TAG = "start_index";         // starting row index
    private static final String SORT_FIELD_TAG = "sort_field";           // "fieldName"
    private static final String SORT_ORDER_TAG = "sort_order";           // “asc/desc”,
    private static final String FIELDS_REQUIRED_TAG = "fields_required"; // [ "list of fields required" ]
    private static final String SEARCH_CRITERIA_TAG = "search_criteria"; //  Refer search criteria object given in the attributes of List Info(For performing advanced search)
    private static final String GET_TOTAL_COUNT_TAG = "get_total_count"; // boolean (by default it will be false)
    private static final String HAS_MORE_ROWS_TAG = "has_more_rows";     // boolean (will be returned with the response)
    private static final String TOTAL_COUNT_TAG = "total_count";         // count (will be returned with the response only)
    private DataFrame dataFrame = new DataFrame();


    /**
     * Constructor from a dataframe. This simply wraps the given frame.
     *
     * @param dataFrame the dataframe to wrap.
     */
    public Info(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }


    /**
     * Constructure from JSON. Wraps the dataframe represented by the JSON
     *
     * @param json the JSON object to wrap.
     * @throws IllegalArgumentException if there were problems parsing the JSON into a DataFrame
     */
    public Info(String json) throws IllegalArgumentException {
        try {
            List<DataFrame> frames = JSONMarshaler.marshal(json);
            dataFrame = frames.get(0);
        } catch (MarshalException e) {
            throw new IllegalArgumentException("JSON Marshaler error.", e);
        }
    }


    public int getRowCount() {
        try {
            return dataFrame.getAsInt(ROW_COUNT_TAG);
        } catch (DataFrameException e) {
            return 0;
        }
    }


    public Info setRowCount(int rowcount) {
        dataFrame.put(ROW_COUNT_TAG, rowcount);
        return this;
    }

    public int getStartIndex() {
        try {
            return dataFrame.getAsInt(START_INDEX_TAG);
        } catch (DataFrameException e) {
            return 0;
        }
    }

    public Info setStartIndex(int index) {
        dataFrame.put(START_INDEX_TAG, index);
        return this;
    }

    public String getSortField() {
        return dataFrame.getAsString(SORT_FIELD_TAG);
    }

    public Info setSortField(String fieldName) {
        dataFrame.put(SORT_FIELD_TAG, fieldName);
        return this;
    }

    public String getSortOrder() {
        return dataFrame.getAsString(SORT_ORDER_TAG);
    }

    public Info setSortOrder(String fieldName) {
        dataFrame.put(SORT_ORDER_TAG, fieldName);
        return this;
    }

    public String[] getFieldsRequired() {
        String[] retval = ArrayUtil.EMPTY_STRING_ARRAY;
        if (dataFrame.containsKey(FIELDS_REQUIRED_TAG)) {
            DataField field = dataFrame.getField(FIELDS_REQUIRED_TAG);
            if (field.isArray()) {
                DataFrame frame = (DataFrame) field.getObjectValue();
                List<String> array = new ArrayList<>();
                for (DataField df : frame.getFields()) {
                    array.add(df.getStringValue());
                }
                retval = array.toArray(new String[0]);
            } else {
                retval = new String[1];
                retval[0] = field.getStringValue();
            }
        }
        return retval;
    }

    public Info setFieldsRequired(String[] fieldNames) {
        dataFrame.put(FIELDS_REQUIRED_TAG, fieldNames);
        return this;
    }

    public Criteria getSearchCriteria() {
        Criteria retval = null;
        if (dataFrame.contains(SEARCH_CRITERIA_TAG)) {
            DataField field = dataFrame.getField(SEARCH_CRITERIA_TAG);
            if (field.isFrame()) {
                DataFrame frame = (DataFrame) field.getObjectValue();
                retval = new Criteria(frame);
            }
        }
        return retval;
    }

    public Info setSearchCriteria(Criteria criteria) {
        dataFrame.put(SEARCH_CRITERIA_TAG, criteria);
        return this;
    }

    @Override
    public String toString() {
        return JSONMarshaler.marshal(dataFrame);
    }

}
