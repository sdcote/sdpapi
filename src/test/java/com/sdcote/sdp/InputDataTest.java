package com.sdcote.sdp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputDataTest {

    @Test
    void testToString() {
        ListInfo listInfo = new ListInfo().setRowCount(3);
        InputData inputData = new InputData().setListInfo(listInfo);
        assertNotNull(inputData.toString());
        System.out.println(inputData.toString());

    }
}