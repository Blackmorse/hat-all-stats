package com.blackmorse.hattrick.utils;

import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    @Test
    public void  test() {
        Date now = new Date();

        Date frist = new Date(now.getTime() - 1000L * 60 * 60 *24 *7 * 14);

        System.out.println(Utils.roundNumber(frist, now, 13));
    }
}