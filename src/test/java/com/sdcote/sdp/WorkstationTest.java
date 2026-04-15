package com.sdcote.sdp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorkstationTest {

    @Test
    void testWorkstationFromJson() {
        String json = "{\"name\":\"ATL-CTX-SF03.ion.llc\",\"id\":\"208564000018058167\",\"is_server\":false,\"last_updated_by\":{\"name\":\"System\",\"id\":\"208564000000006619\"},\"computer_system\":{\"service_tag\":\"VMware-42 28 86 85 c4 f9 67 f1-15 64 d3 0c a1 11 4c e2\",\"model\":\"VMware7,1\"}}";
        Workstation ws = new Workstation(json);
        
        assertEquals("ATL-CTX-SF03.ion.llc", ws.getName());
        assertEquals("208564000018058167", ws.getId());
        assertFalse(ws.isServer());
        
        User lastUpdatedBy = ws.getLastUpdatedBy();
        assertNotNull(lastUpdatedBy);
        assertEquals("System", lastUpdatedBy.getName());
        assertEquals("208564000000006619", lastUpdatedBy.getId());
        
        assertEquals("VMware-42 28 86 85 c4 f9 67 f1-15 64 d3 0c a1 11 4c e2", ws.getServiceTag());
        assertEquals("VMware7,1", ws.getModel());
        assertNull(ws.getStateName());
    }

    @Test
    void testGetStateName() {
        // Case 1: State and name exist
        String jsonWithState = "{\"state\":{\"name\":\"In Store\"}}";
        Workstation ws1 = new Workstation(jsonWithState);
        assertEquals("In Store", ws1.getStateName());

        // Case 2: No state dataframe
        Workstation ws2 = new Workstation("{}");
        assertNull(ws2.getStateName());

        // Case 3: State exists but no name attribute
        String jsonStateNoName = "{\"state\":{\"id\":\"123\"}}";
        Workstation ws3 = new Workstation(jsonStateNoName);
        assertNull(ws3.getStateName());
    }

    @Test
    void testWorkstationAccessors() {
        Workstation ws = new Workstation();
        ws.setName("TestWS");
        ws.setBarcode("12345");
        ws.setServer(true);
        
        assertEquals("TestWS", ws.getName());
        assertEquals("12345", ws.getBarcode());
        assertTrue(ws.isServer());
    }
}
