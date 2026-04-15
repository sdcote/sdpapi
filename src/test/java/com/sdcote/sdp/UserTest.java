package com.sdcote.sdp;

import coyote.commons.dataframe.DataFrame;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserAccessors() {
        User user = new User();
        user.setName("John Doe");
        user.setEmailId("john@example.com");
        user.setTechnician(true);
        
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmailId());
        assertTrue(user.isTechnician());
    }

    @Test
    void testUserFromJson() {
        String json = "{\"name\":\"System\",\"id\":\"208564000000006619\",\"is_technician\":false}";
        User user = new User(json);
        assertEquals("System", user.getName());
        assertEquals("208564000000006619", user.getId());
        assertFalse(user.isTechnician());
    }
}
