package com.sdcote.sdp;

import coyote.commons.log.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SDPTest {
    static {
        //Log.initDevelopmentLogging();
    }
    @Test
    void throttle() {

        long now = System.currentTimeMillis();
        Log.info("throttle starting");
        for (int i = 0; i < 5; i++) {
            SDP.throttle();
            Log.debug("step");
        }
        Log.info("throttle finished");
        long elapsed = System.currentTimeMillis() - now;
        Log.info("throttle elapsed: " + elapsed);
        assertTrue(elapsed> 10_000);
    }
}