package com.blackmorse.hattrick.common;

import java.util.HashMap;
import java.util.Map;

public class CommonData {
    public static final Map<Integer, CommonLeagueUnitInfo> higherLeagueMap = new HashMap<>();

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

    public static class CommonLeagueUnitInfo {
        private final Long leagueUnitId;
        private final String leagueUnitName;

        public CommonLeagueUnitInfo(Long leagueUnitId, String leagueUnitName) {
            this.leagueUnitId = leagueUnitId;
            this.leagueUnitName = leagueUnitName;
        }

        public Long getLeagueUnitId() {
            return leagueUnitId;
        }

        public String getLeagueUnitName() {
            return leagueUnitName;
        }
    }

    static {
        higherLeagueMap.put(1, new CommonLeagueUnitInfo(1L, "Allsvenskan"));
        higherLeagueMap.put(2, new CommonLeagueUnitInfo(512L, "English Premier"));
        higherLeagueMap.put(3, new CommonLeagueUnitInfo(427L, "Bundesliga"));
        higherLeagueMap.put(4, new CommonLeagueUnitInfo(724L, "Serie A"));
        higherLeagueMap.put(5, new CommonLeagueUnitInfo(703L, "Championnat"));
        higherLeagueMap.put(6, new CommonLeagueUnitInfo(682L, "Primera División"));
        higherLeagueMap.put(7, new CommonLeagueUnitInfo(342L, "Primera División"));
        higherLeagueMap.put(8, new CommonLeagueUnitInfo(597L, "Major League"));
        higherLeagueMap.put(9, new CommonLeagueUnitInfo(2110L, "Tippeligaen"));
        higherLeagueMap.put(11, new CommonLeagueUnitInfo(1769L, "Superligaen"));
        higherLeagueMap.put(12, new CommonLeagueUnitInfo(2280L, "Mestaruussarja"));
        higherLeagueMap.put(14, new CommonLeagueUnitInfo(2195L, "Eredivisie"));
        higherLeagueMap.put(15, new CommonLeagueUnitInfo(3208L, "The Pacific Premier"));
        higherLeagueMap.put(16, new CommonLeagueUnitInfo(3229L, "Campeonato Brasileiro"));
        higherLeagueMap.put(17, new CommonLeagueUnitInfo(3314L, "All Canadian"));
        higherLeagueMap.put(18, new CommonLeagueUnitInfo(3335L, "Campeonato Chileno"));
        higherLeagueMap.put(19, new CommonLeagueUnitInfo(3377L, "Futbol Profesional Colombiano"));
        higherLeagueMap.put(20, new CommonLeagueUnitInfo(3488L, "I-League"));
        higherLeagueMap.put(21, new CommonLeagueUnitInfo(3573L, "Irish Premier"));
        higherLeagueMap.put(22, new CommonLeagueUnitInfo(3594L, "J. League"));
        higherLeagueMap.put(23, new CommonLeagueUnitInfo(3615L, "Primera División"));
        higherLeagueMap.put(24, new CommonLeagueUnitInfo(3620L, "Ekstraklasa"));
        higherLeagueMap.put(25, new CommonLeagueUnitInfo(3705L, "Super Liga"));
        higherLeagueMap.put(26, new CommonLeagueUnitInfo(3166L, "Scottish Premier"));
        higherLeagueMap.put(27, new CommonLeagueUnitInfo(3161L, "SA Premier League"));
        higherLeagueMap.put(28, new CommonLeagueUnitInfo(3013L, "Campeonato Uruguayo"));
        higherLeagueMap.put(29, new CommonLeagueUnitInfo(3008L, "Primera División"));
        higherLeagueMap.put(30, new CommonLeagueUnitInfo(3140L, "K-League"));
        higherLeagueMap.put(31, new CommonLeagueUnitInfo(3119L, "Thailand League"));
        higherLeagueMap.put(32, new CommonLeagueUnitInfo(3098L, "Süper Lig"));
        higherLeagueMap.put(33, new CommonLeagueUnitInfo(3398L, "Dawry El Momtaz"));
        higherLeagueMap.put(34, new CommonLeagueUnitInfo(3356L, "Dragons Elite"));
        higherLeagueMap.put(35, new CommonLeagueUnitInfo(3187L, "Premier Liga"));
        higherLeagueMap.put(36, new CommonLeagueUnitInfo(3403L, "Primera"));
        higherLeagueMap.put(37, new CommonLeagueUnitInfo(3854L, "Liga I"));
        higherLeagueMap.put(38, new CommonLeagueUnitInfo(4200L, "Úrvalsdeild"));
        higherLeagueMap.put(39, new CommonLeagueUnitInfo(4205L, "Bundesliga"));
        higherLeagueMap.put(44, new CommonLeagueUnitInfo(8714L, "Eerste Klasse"));
        higherLeagueMap.put(45, new CommonLeagueUnitInfo(4213L, "Liga Perdana"));
        higherLeagueMap.put(46, new CommonLeagueUnitInfo(4206L, "Nationalliga A"));
        higherLeagueMap.put(47, new CommonLeagueUnitInfo(4211L, "S-League"));
        higherLeagueMap.put(50, new CommonLeagueUnitInfo(11345L, "A´ Ethniki"));
        higherLeagueMap.put(51, new CommonLeagueUnitInfo(11324L, "NB I"));
        higherLeagueMap.put(52, new CommonLeagueUnitInfo(11303L, "První liga"));
        higherLeagueMap.put(53, new CommonLeagueUnitInfo(11450L, "Virsliga"));
        higherLeagueMap.put(54, new CommonLeagueUnitInfo(11408L, "Ligina"));
        higherLeagueMap.put(55, new CommonLeagueUnitInfo(11429L, "Philippines Football League"));
        higherLeagueMap.put(56, new CommonLeagueUnitInfo(11366L, "Meistriliiga"));
        higherLeagueMap.put(57, new CommonLeagueUnitInfo(11471L, "Super Liga"));
        higherLeagueMap.put(58, new CommonLeagueUnitInfo(11387L, "1.HNL"));
        higherLeagueMap.put(59, new CommonLeagueUnitInfo(13508L, "1st Division League"));
        higherLeagueMap.put(60, new CommonLeagueUnitInfo(13531L, "Dragon League"));
        higherLeagueMap.put(61, new CommonLeagueUnitInfo(16623L, "League of Wales"));
        higherLeagueMap.put(62, new CommonLeagueUnitInfo(14234L, "Vissha Liga"));
        higherLeagueMap.put(63, new CommonLeagueUnitInfo(13680L, "Ligat Ha´al"));
        higherLeagueMap.put(64, new CommonLeagueUnitInfo(14213L, "Prva slovenska liga"));
        higherLeagueMap.put(66, new CommonLeagueUnitInfo(29747L, "A Lyga"));
        higherLeagueMap.put(67, new CommonLeagueUnitInfo(29768L, "1. liga"));
        higherLeagueMap.put(68, new CommonLeagueUnitInfo(33138L, "Premier-Liga"));
        higherLeagueMap.put(69, new CommonLeagueUnitInfo(29726L, "Premijer Liga"));
        higherLeagueMap.put(70, new CommonLeagueUnitInfo(28425L, "V-League"));
        higherLeagueMap.put(71, new CommonLeagueUnitInfo(32093L, "Pakistan Premier League"));
        higherLeagueMap.put(72, new CommonLeagueUnitInfo(42133L, "División de Honor"));
        higherLeagueMap.put(73, new CommonLeagueUnitInfo(34841L, "Campeonato Ecuatoriano"));
        higherLeagueMap.put(74, new CommonLeagueUnitInfo(34840L, "Liga Profesional del Fútbol Boliviano"));
        higherLeagueMap.put(75, new CommonLeagueUnitInfo(34872L, "Nigerian Premiership"));
        higherLeagueMap.put(76, new CommonLeagueUnitInfo(34871L, "1. Deild"));
        higherLeagueMap.put(77, new CommonLeagueUnitInfo(34870L, "Botola"));
        higherLeagueMap.put(79, new CommonLeagueUnitInfo(48896L, "Al Dawry Al Momtaz"));
        higherLeagueMap.put(80, new CommonLeagueUnitInfo(53781L, "Division Nationale"));
        higherLeagueMap.put(81, new CommonLeagueUnitInfo(56879L, "Primera División"));
        higherLeagueMap.put(83, new CommonLeagueUnitInfo(56880L, "Al Amiri League"));
        higherLeagueMap.put(84, new CommonLeagueUnitInfo(57433L, "Nationaldivisioun"));
        higherLeagueMap.put(85, new CommonLeagueUnitInfo(57518L, "League Bartar"));
        higherLeagueMap.put(88, new CommonLeagueUnitInfo(57539L, "Liga Mayor de Futbol"));
        higherLeagueMap.put(89, new CommonLeagueUnitInfo(57560L, "A Katigoria"));
        higherLeagueMap.put(91, new CommonLeagueUnitInfo(60146L, "Vyshejshaja Liga"));
        higherLeagueMap.put(93, new CommonLeagueUnitInfo(60150L, "Northern Irish Premier"));
        higherLeagueMap.put(94, new CommonLeagueUnitInfo(60148L, "Jamaican Premier League"));
        higherLeagueMap.put(95, new CommonLeagueUnitInfo(60149L, "Ligi Kuu Ya Kenya"));
        higherLeagueMap.put(96, new CommonLeagueUnitInfo(60151L, "LPF"));
        higherLeagueMap.put(97, new CommonLeagueUnitInfo(60147L, "Prva Makedonska Liga"));
        higherLeagueMap.put(98, new CommonLeagueUnitInfo(88340L, "Superliga"));
        higherLeagueMap.put(99, new CommonLeagueUnitInfo(88257L, "Liga Nacional"));
        higherLeagueMap.put(100, new CommonLeagueUnitInfo(88256L, "Liga Mayor"));
        higherLeagueMap.put(101, new CommonLeagueUnitInfo(88258L, "Kampjonat Premier"));
        higherLeagueMap.put(102, new CommonLeagueUnitInfo(88341L, "Jogorku Liga"));
        higherLeagueMap.put(103, new CommonLeagueUnitInfo(88259L, "Divizia Naţională"));
        higherLeagueMap.put(104, new CommonLeagueUnitInfo(88382L, "Erovnuli Liga"));
        higherLeagueMap.put(105, new CommonLeagueUnitInfo(88385L, "Primera Divisió"));
        higherLeagueMap.put(106, new CommonLeagueUnitInfo(88390L, "Al-Dawry Al-Momtaz"));
        higherLeagueMap.put(107, new CommonLeagueUnitInfo(88447L, "Liga del Quetzal"));
        higherLeagueMap.put(110, new CommonLeagueUnitInfo(98772L, "T&T Pro League"));
        higherLeagueMap.put(111, new CommonLeagueUnitInfo(98793L, "Primera División"));
        higherLeagueMap.put(112, new CommonLeagueUnitInfo(98814L, "Premier Liga"));
        higherLeagueMap.put(113, new CommonLeagueUnitInfo(98835L, "Hoofdklasse"));
        higherLeagueMap.put(117, new CommonLeagueUnitInfo(123048L, "Fürstliche Liga"));
        higherLeagueMap.put(118, new CommonLeagueUnitInfo(123069L, "Division Une"));
        higherLeagueMap.put(119, new CommonLeagueUnitInfo(123090L, "Mongolia Premier League"));
        higherLeagueMap.put(120, new CommonLeagueUnitInfo(123111L, "Al Dawry Al Aam Al Lubnani"));
        higherLeagueMap.put(121, new CommonLeagueUnitInfo(123132L, "Teranga Ligue 1"));
        higherLeagueMap.put(122, new CommonLeagueUnitInfo(123133L, "Bardzraguyn khumb"));
        higherLeagueMap.put(123, new CommonLeagueUnitInfo(123188L, "Al Dawry Al-Momtaz"));
        higherLeagueMap.put(124, new CommonLeagueUnitInfo(123209L, "Barbados Premier League"));
        higherLeagueMap.put(125, new CommonLeagueUnitInfo(123210L, "Campeonato Nacional"));
        higherLeagueMap.put(126, new CommonLeagueUnitInfo(123211L, "Ligue 1"));
        higherLeagueMap.put(127, new CommonLeagueUnitInfo(200087L, "Kuwaiti Premier League"));
        higherLeagueMap.put(128, new CommonLeagueUnitInfo(200092L, "Al-Dawry Al-Momtaz"));
        higherLeagueMap.put(129, new CommonLeagueUnitInfo(201137L, "Güclülər Dəstəsi"));
        higherLeagueMap.put(130, new CommonLeagueUnitInfo(209686L, "Girabola"));
        higherLeagueMap.put(131, new CommonLeagueUnitInfo(209708L, "1. CFL"));
        higherLeagueMap.put(132, new CommonLeagueUnitInfo(209729L, "Premier League"));
        higherLeagueMap.put(133, new CommonLeagueUnitInfo(225688L, "Yemeni League"));
        higherLeagueMap.put(134, new CommonLeagueUnitInfo(225713L, "Al-Dawry Al-Omani"));
        higherLeagueMap.put(135, new CommonLeagueUnitInfo(225734L, "Moçambola"));
        higherLeagueMap.put(136, new CommonLeagueUnitInfo(229917L, "B-League"));
        higherLeagueMap.put(137, new CommonLeagueUnitInfo(229916L, "Premier League"));
        higherLeagueMap.put(138, new CommonLeagueUnitInfo(237126L, "Cambodian Premier League"));
        higherLeagueMap.put(139, new CommonLeagueUnitInfo(238747L, "Benin Premier League"));
        higherLeagueMap.put(140, new CommonLeagueUnitInfo(238748L, "Syrian League"));
        higherLeagueMap.put(141, new CommonLeagueUnitInfo(238789L, "Al Dawri Al Aaam"));
        higherLeagueMap.put(142, new CommonLeagueUnitInfo(238790L, "Premier League"));
        higherLeagueMap.put(143, new CommonLeagueUnitInfo(245936L, "Ugandan Super League"));
        higherLeagueMap.put(144, new CommonLeagueUnitInfo(245935L, "Dhivehi League"));
        higherLeagueMap.put(145, new CommonLeagueUnitInfo(252316L, "Oliy Liga"));
        higherLeagueMap.put(146, new CommonLeagueUnitInfo(252313L, "Elite League"));
        higherLeagueMap.put(147, new CommonLeagueUnitInfo(252358L, "Primera División Cubana"));
        higherLeagueMap.put(148, new CommonLeagueUnitInfo(252357L, "I.1"));
        higherLeagueMap.put(149, new CommonLeagueUnitInfo(258094L, "Campeonato Nacional de São Tomé e Príncipe"));
        higherLeagueMap.put(151, new CommonLeagueUnitInfo(258136L, "I.1"));
        higherLeagueMap.put(152, new CommonLeagueUnitInfo(258052L, "I.1"));
        higherLeagueMap.put(153, new CommonLeagueUnitInfo(258115L, "Promé Divishon"));
        higherLeagueMap.put(154, new CommonLeagueUnitInfo(258073L, "I.1"));
        higherLeagueMap.put(155, new CommonLeagueUnitInfo(258477L, "I.1"));
        higherLeagueMap.put(156, new CommonLeagueUnitInfo(258498L, "I.1"));
        higherLeagueMap.put(157, new CommonLeagueUnitInfo(258519L, "I.1"));
        higherLeagueMap.put(158, new CommonLeagueUnitInfo(258540L, "I.1"));
        higherLeagueMap.put(159, new CommonLeagueUnitInfo(258561L, "I.1"));
        higherLeagueMap.put(160, new CommonLeagueUnitInfo(258582L, "I.1"));
        higherLeagueMap.put(1000, new CommonLeagueUnitInfo(256687L, "I.1"));
        
        
    }
}
