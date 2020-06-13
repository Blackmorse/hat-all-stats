package com.blackmorse.hattrick.common;

import java.util.HashMap;
import java.util.Map;

public class CommonData {
    public static final Map<Integer, Long> higherLeagueMap = new HashMap<>();

    public static Map<Integer, String> arabToRomans = new HashMap<>();
    public static Map<String, Integer> romansToArab = new HashMap<>();
    public static Map<Integer, Integer> leagueLevelNumberTeams = new HashMap<>();

    static {
        leagueLevelNumberTeams.put(1, 1);
        leagueLevelNumberTeams.put(2, 4);
        leagueLevelNumberTeams.put(3, 16);
        leagueLevelNumberTeams.put(4, 64);
        leagueLevelNumberTeams.put(5, 256);
        leagueLevelNumberTeams.put(6, 1024);
        leagueLevelNumberTeams.put(7, 1024);
        leagueLevelNumberTeams.put(8, 2048);
        leagueLevelNumberTeams.put(9, 2048);
    }

    static {
        arabToRomans.put(1, "I");
        arabToRomans.put(2, "II");
        arabToRomans.put(3, "III");
        arabToRomans.put(4, "IV");
        arabToRomans.put(5, "V");
        arabToRomans.put(6, "VI");
        arabToRomans.put(7, "VII");
        arabToRomans.put(8, "VIII");
        arabToRomans.put(9, "IX");
        arabToRomans.put(10, "X");
        arabToRomans.put(11, "XI");
        arabToRomans.put(12, "XII");
        arabToRomans.put(13, "XIII");
        arabToRomans.put(14, "XIV");
        arabToRomans.put(15, "XV");
    }

    static {
        romansToArab.put("I", 1);
        romansToArab.put("II", 2);
        romansToArab.put("III", 3);
        romansToArab.put("IV", 4);
        romansToArab.put("V", 5);
        romansToArab.put("VI", 6);
        romansToArab.put("VII", 7);
        romansToArab.put("VIII", 8);
        romansToArab.put("IX", 9);
        romansToArab.put("X", 10);
        romansToArab.put("XI", 11);
        romansToArab.put("XII", 12);
        romansToArab.put("XIII", 13);
        romansToArab.put("XIV", 14);
        romansToArab.put("XV", 15);
    }

    static {
        higherLeagueMap.put(1, 1L);
        higherLeagueMap.put(2, 512L);
        higherLeagueMap.put(3, 427L);
        higherLeagueMap.put(4, 724L);
        higherLeagueMap.put(5, 703L);
        higherLeagueMap.put(6, 682L);
        higherLeagueMap.put(7, 342L);
        higherLeagueMap.put(8, 597L);
        higherLeagueMap.put(9, 2110L);
        higherLeagueMap.put(11, 1769L);
        higherLeagueMap.put(12, 2280L);
        higherLeagueMap.put(14, 2195L);
        higherLeagueMap.put(15, 3208L);
        higherLeagueMap.put(16, 3229L);
        higherLeagueMap.put(17, 3314L);
        higherLeagueMap.put(18, 3335L);
        higherLeagueMap.put(19, 3377L);
        higherLeagueMap.put(20, 3488L);
        higherLeagueMap.put(21, 3573L);
        higherLeagueMap.put(22, 3594L);
        higherLeagueMap.put(23, 3615L);
        higherLeagueMap.put(24, 3620L);
        higherLeagueMap.put(25, 3705L);
        higherLeagueMap.put(26, 3166L);
        higherLeagueMap.put(27, 3161L);
        higherLeagueMap.put(28, 3013L);
        higherLeagueMap.put(29, 3008L);
        higherLeagueMap.put(30, 3140L);
        higherLeagueMap.put(31, 3119L);
        higherLeagueMap.put(32, 3098L);
        higherLeagueMap.put(33, 3398L);
        higherLeagueMap.put(34, 3356L);
        higherLeagueMap.put(35, 3187L);
        higherLeagueMap.put(36, 3403L);
        higherLeagueMap.put(37, 3854L);
        higherLeagueMap.put(38, 4200L);
        higherLeagueMap.put(39, 4205L);
        higherLeagueMap.put(44, 8714L);
        higherLeagueMap.put(45, 4213L);
        higherLeagueMap.put(46, 4206L);
        higherLeagueMap.put(47, 4211L);
        higherLeagueMap.put(50, 11345L);
        higherLeagueMap.put(51, 11324L);
        higherLeagueMap.put(52, 11303L);
        higherLeagueMap.put(53, 11450L);
        higherLeagueMap.put(54, 11408L);
        higherLeagueMap.put(55, 11429L);
        higherLeagueMap.put(56, 11366L);
        higherLeagueMap.put(57, 11471L);
        higherLeagueMap.put(58, 11387L);
        higherLeagueMap.put(59, 13508L);
        higherLeagueMap.put(60, 13531L);
        higherLeagueMap.put(61, 16623L);
        higherLeagueMap.put(62, 14234L);
        higherLeagueMap.put(63, 13680L);
        higherLeagueMap.put(64, 14213L);
        higherLeagueMap.put(66, 29747L);
        higherLeagueMap.put(67, 29768L);
        higherLeagueMap.put(68, 33138L);
        higherLeagueMap.put(69, 29726L);
        higherLeagueMap.put(70, 28425L);
        higherLeagueMap.put(71, 32093L);
        higherLeagueMap.put(72, 42133L);
        higherLeagueMap.put(73, 34841L);
        higherLeagueMap.put(74, 34840L);
        higherLeagueMap.put(75, 34872L);
        higherLeagueMap.put(76, 34871L);
        higherLeagueMap.put(77, 34870L);
        higherLeagueMap.put(79, 48896L);
        higherLeagueMap.put(80, 53781L);
        higherLeagueMap.put(81, 56879L);
        higherLeagueMap.put(83, 56880L);
        higherLeagueMap.put(84, 57433L);
        higherLeagueMap.put(85, 57518L);
        higherLeagueMap.put(88, 57539L);
        higherLeagueMap.put(89, 57560L);
        higherLeagueMap.put(91, 60146L);
        higherLeagueMap.put(93, 60150L);
        higherLeagueMap.put(94, 60148L);
        higherLeagueMap.put(95, 60149L);
        higherLeagueMap.put(96, 60151L);
        higherLeagueMap.put(97, 60147L);
        higherLeagueMap.put(98, 88340L);
        higherLeagueMap.put(99, 88257L);
        higherLeagueMap.put(100, 88256L);
        higherLeagueMap.put(101, 88258L);
        higherLeagueMap.put(102, 88341L);
        higherLeagueMap.put(103, 88259L);
        higherLeagueMap.put(104, 88382L);
        higherLeagueMap.put(105, 88385L);
        higherLeagueMap.put(106, 88390L);
        higherLeagueMap.put(107, 88447L);
        higherLeagueMap.put(110, 98772L);
        higherLeagueMap.put(111, 98793L);
        higherLeagueMap.put(112, 98814L);
        higherLeagueMap.put(113, 98835L);
        higherLeagueMap.put(117, 123048L);
        higherLeagueMap.put(118, 123069L);
        higherLeagueMap.put(119, 123090L);
        higherLeagueMap.put(120, 123111L);
        higherLeagueMap.put(121, 123132L);
        higherLeagueMap.put(122, 123133L);
        higherLeagueMap.put(123, 123188L);
        higherLeagueMap.put(124, 123209L);
        higherLeagueMap.put(125, 123210L);
        higherLeagueMap.put(126, 123211L);
        higherLeagueMap.put(127, 200087L);
        higherLeagueMap.put(128, 200092L);
        higherLeagueMap.put(129, 201137L);
        higherLeagueMap.put(130, 209686L);
        higherLeagueMap.put(131, 209708L);
        higherLeagueMap.put(132, 209729L);
        higherLeagueMap.put(133, 225688L);
        higherLeagueMap.put(134, 225713L);
        higherLeagueMap.put(135, 225734L);
        higherLeagueMap.put(136, 229917L);
        higherLeagueMap.put(137, 229916L);
        higherLeagueMap.put(138, 237126L);
        higherLeagueMap.put(139, 238747L);
        higherLeagueMap.put(140, 238748L);
        higherLeagueMap.put(141, 238789L);
        higherLeagueMap.put(142, 238790L);
        higherLeagueMap.put(143, 245936L);
        higherLeagueMap.put(144, 245935L);
        higherLeagueMap.put(145, 252316L);
        higherLeagueMap.put(146, 252313L);
        higherLeagueMap.put(147, 252358L);
        higherLeagueMap.put(148, 252357L);
        higherLeagueMap.put(149, 258094L);
        higherLeagueMap.put(151, 258136L);
        higherLeagueMap.put(152, 258052L);
        higherLeagueMap.put(153, 258115L);
        higherLeagueMap.put(154, 258073L);
        higherLeagueMap.put(155, 258477L);
        higherLeagueMap.put(156, 258498L);
        higherLeagueMap.put(157, 258519L);
        higherLeagueMap.put(158, 258540L);
        higherLeagueMap.put(159, 258561L);
        higherLeagueMap.put(160, 258582L);
        higherLeagueMap.put(1000, 256687L);
    }
}
