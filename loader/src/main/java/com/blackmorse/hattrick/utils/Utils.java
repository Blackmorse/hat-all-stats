package com.blackmorse.hattrick.utils;

import java.util.Date;

public final class Utils {
    private Utils() {}

    public static Integer roundNumber(Date matchDate, Date lastLeagueMatchDate, Integer currentRound) {
        long daysBefore = (lastLeagueMatchDate.getTime() - matchDate.getTime()) / (1000 * 60 * 60 * 24 * 7);
        return (int)(currentRound - daysBefore);
    }
}
