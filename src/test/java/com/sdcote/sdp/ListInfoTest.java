package com.sdcote.sdp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Here is a sample of an actual call:
 * https://servicedesk.navista.com/api/v3/assets?input_data={"list_info":{"row_count":"100","start_index":1,"fields_required":["name","user","department","used_by_asset","product","product_type","state","asset_tag","type","category","integration_mappings","has_attachments","lifecycle","loan"],"get_total_count":false,"filter_by":{"id":"0"},"sort_fields":[{"field":"product_type.name","order":"asc"}]}}
 */
class ListInfoTest {

    @Test
    void getRowCount() {
        ListInfo listInfo = new ListInfo();
        assertEquals(0, listInfo.getRowCount());
        listInfo.setRowCount(1);
        assertEquals(1, listInfo.getRowCount());
        listInfo = new ListInfo().setRowCount(3);
        assertEquals(3, listInfo.getRowCount());
        assertEquals("{\"row_count\":3}", listInfo.toString());
    }

}