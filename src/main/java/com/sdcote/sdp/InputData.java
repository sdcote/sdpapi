package com.sdcote.sdp;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;

public class InputData {
    private static final String ENTITY_TAG = "entity";
    private static final String LIST_INFO_TAG = "list_info";

    private final DataFrame dataFrame = new DataFrame();

    /**
     * Place the desired Entity in this Input Data.
     *
     * @param entity the Entity to place in the input data.
     * @return a reference to this object for call chaining.
     */
    public InputData setEntity(DataFrame entity) {
        dataFrame.put(ENTITY_TAG, entity);
        return this;
    }


    /**
     * Place the desired ListInfo in this Input Data.
     *
     * @param listInfo the ListInfo to place in the input data.
     * @return a reference to this object for call chaining.
     */
    public InputData setListInfo(ListInfo listInfo) {
        dataFrame.put(LIST_INFO_TAG, listInfo.getDataFrame());
        return this;
    }


    @Override
    public String toString() {
        return JSONMarshaler.marshal(dataFrame);
    }

}
