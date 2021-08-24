package hattid

object CommonData {
  lazy val higherLeagueMap: Map[Int, CommonLeagueUnitInfo] = {
    Map(
      1 -> CommonLeagueUnitInfo(1, "Allsvenskan"),
      2 -> CommonLeagueUnitInfo(512, "English Premier"),
      3 -> CommonLeagueUnitInfo(427, "Bundesliga"),
      4 -> CommonLeagueUnitInfo(724, "Serie A"),
      5 -> CommonLeagueUnitInfo(703, "Championnat"),
      6 -> CommonLeagueUnitInfo(682, "Primera División"),
      7 -> CommonLeagueUnitInfo(342, "Primera División"),
      8 -> CommonLeagueUnitInfo(597, "Major League"),
      9 -> CommonLeagueUnitInfo(2110, "Tippeligaen"),
      11 -> CommonLeagueUnitInfo(1769, "Superligaen"),
      12 -> CommonLeagueUnitInfo(2280, "Mestaruussarja"),
      14 -> CommonLeagueUnitInfo(2195, "Eredivisie"),
      15 -> CommonLeagueUnitInfo(3208, "The Pacific Premier"),
      16 -> CommonLeagueUnitInfo(3229, "Campeonato Brasileiro"),
      17 -> CommonLeagueUnitInfo(3314, "All Canadian"),
      18 -> CommonLeagueUnitInfo(3335, "Campeonato Chileno"),
      19 -> CommonLeagueUnitInfo(3377, "Futbol Profesional Colombiano"),
      20 -> CommonLeagueUnitInfo(3488, "I-League"),
      21 -> CommonLeagueUnitInfo(3573, "Irish Premier"),
      22 -> CommonLeagueUnitInfo(3594, "J. League"),
      23 -> CommonLeagueUnitInfo(3615, "Primera División"),
      24 -> CommonLeagueUnitInfo(3620, "Ekstraklasa"),
      25 -> CommonLeagueUnitInfo(3705, "Super Liga"),
      26 -> CommonLeagueUnitInfo(3166, "Scottish Premier"),
      27 -> CommonLeagueUnitInfo(3161, "SA Premier League"),
      28 -> CommonLeagueUnitInfo(3013, "Campeonato Uruguayo"),
      29 -> CommonLeagueUnitInfo(3008, "Primera División"),
      30 -> CommonLeagueUnitInfo(3140, "K-League"),
      31 -> CommonLeagueUnitInfo(3119, "Thailand League"),
      32 -> CommonLeagueUnitInfo(3098, "Süper Lig"),
      33 -> CommonLeagueUnitInfo(3398, "Dawry El Momtaz"),
      34 -> CommonLeagueUnitInfo(3356, "Dragons Elite"),
      35 -> CommonLeagueUnitInfo(3187, "Premier Liga"),
      36 -> CommonLeagueUnitInfo(3403, "Primera"),
      37 -> CommonLeagueUnitInfo(3854, "Liga I"),
      38 -> CommonLeagueUnitInfo(4200, "Úrvalsdeild"),
      39 -> CommonLeagueUnitInfo(4205, "Bundesliga"),
      44 -> CommonLeagueUnitInfo(8714, "Eerste Klasse"),
      45 -> CommonLeagueUnitInfo(4213, "Liga Perdana"),
      46 -> CommonLeagueUnitInfo(4206, "Nationalliga A"),
      47 -> CommonLeagueUnitInfo(4211, "S-League"),
      50 -> CommonLeagueUnitInfo(11345, "A´ Ethniki"),
      51 -> CommonLeagueUnitInfo(11324, "NB I"),
      52 -> CommonLeagueUnitInfo(11303, "První liga"),
      53 -> CommonLeagueUnitInfo(11450, "Virsliga"),
      54 -> CommonLeagueUnitInfo(11408, "Ligina"),
      55 -> CommonLeagueUnitInfo(11429, "Philippines Football League"),
      56 -> CommonLeagueUnitInfo(11366, "Meistriliiga"),
      57 -> CommonLeagueUnitInfo(11471, "Super Liga"),
      58 -> CommonLeagueUnitInfo(11387, "1.HNL"),
      59 -> CommonLeagueUnitInfo(13508, "1st Division League"),
      60 -> CommonLeagueUnitInfo(13531, "Dragon League"),
      61 -> CommonLeagueUnitInfo(16623, "League of Wales"),
      62 -> CommonLeagueUnitInfo(14234, "Vissha Liga"),
      63 -> CommonLeagueUnitInfo(13680, "Ligat Ha´al"),
      64 -> CommonLeagueUnitInfo(14213, "Prva slovenska liga"),
      66 -> CommonLeagueUnitInfo(29747, "A Lyga"),
      67 -> CommonLeagueUnitInfo(29768, "1. liga"),
      68 -> CommonLeagueUnitInfo(33138, "Premier-Liga"),
      69 -> CommonLeagueUnitInfo(29726, "Premijer Liga"),
      70 -> CommonLeagueUnitInfo(28425, "V-League"),
      71 -> CommonLeagueUnitInfo(32093, "Pakistan Premier League"),
      72 -> CommonLeagueUnitInfo(42133, "División de Honor"),
      73 -> CommonLeagueUnitInfo(34841, "Campeonato Ecuatoriano"),
      74 -> CommonLeagueUnitInfo(34840, "Liga Profesional del Fútbol Boliviano"),
      75 -> CommonLeagueUnitInfo(34872, "Nigerian Premiership"),
      76 -> CommonLeagueUnitInfo(34871, "1. Deild"),
      77 -> CommonLeagueUnitInfo(34870, "Botola"),
      79 -> CommonLeagueUnitInfo(48896, "Al Dawry Al Momtaz"),
      80 -> CommonLeagueUnitInfo(53781, "Division Nationale"),
      81 -> CommonLeagueUnitInfo(56879, "Primera División"),
      83 -> CommonLeagueUnitInfo(56880, "Al Amiri League"),
      84 -> CommonLeagueUnitInfo(57433, "Nationaldivisioun"),
      85 -> CommonLeagueUnitInfo(57518, "League Bartar"),
      88 -> CommonLeagueUnitInfo(57539, "Liga Mayor de Futbol"),
      89 -> CommonLeagueUnitInfo(57560, "A Katigoria"),
      91 -> CommonLeagueUnitInfo(60146, "Vyshejshaja Liga"),
      93 -> CommonLeagueUnitInfo(60150, "Northern Irish Premier"),
      94 -> CommonLeagueUnitInfo(60148, "Jamaican Premier League"),
      95 -> CommonLeagueUnitInfo(60149, "Ligi Kuu Ya Kenya"),
      96 -> CommonLeagueUnitInfo(60151, "LPF"),
      97 -> CommonLeagueUnitInfo(60147, "Prva Makedonska Liga"),
      98 -> CommonLeagueUnitInfo(88340, "Superliga"),
      99 -> CommonLeagueUnitInfo(88257, "Liga Nacional"),
      100 -> CommonLeagueUnitInfo(88256, "Liga Mayor"),
      101 -> CommonLeagueUnitInfo(88258, "Kampjonat Premier"),
      102 -> CommonLeagueUnitInfo(88341, "Jogorku Liga"),
      103 -> CommonLeagueUnitInfo(88259, "Divizia Naţională"),
      104 -> CommonLeagueUnitInfo(88382, "Erovnuli Liga"),
      105 -> CommonLeagueUnitInfo(88385, "Primera Divisió"),
      106 -> CommonLeagueUnitInfo(88390, "Al-Dawry Al-Momtaz"),
      107 -> CommonLeagueUnitInfo(88447, "Liga del Quetzal"),
      110 -> CommonLeagueUnitInfo(98772, "T&T Pro League"),
      111 -> CommonLeagueUnitInfo(98793, "Primera División"),
      112 -> CommonLeagueUnitInfo(98814, "Premier Liga"),
      113 -> CommonLeagueUnitInfo(98835, "Hoofdklasse"),
      117 -> CommonLeagueUnitInfo(123048, "Fürstliche Liga"),
      118 -> CommonLeagueUnitInfo(123069, "Division Une"),
      119 -> CommonLeagueUnitInfo(123090, "Mongolia Premier League"),
      120 -> CommonLeagueUnitInfo(123111, "Al Dawry Al Aam Al Lubnani"),
      121 -> CommonLeagueUnitInfo(123132, "Teranga Ligue 1"),
      122 -> CommonLeagueUnitInfo(123133, "Bardzraguyn khumb"),
      123 -> CommonLeagueUnitInfo(123188, "Al Dawry Al-Momtaz"),
      124 -> CommonLeagueUnitInfo(123209, "Barbados Premier League"),
      125 -> CommonLeagueUnitInfo(123210, "Campeonato Nacional"),
      126 -> CommonLeagueUnitInfo(123211, "Ligue 1"),
      127 -> CommonLeagueUnitInfo(200087, "Kuwaiti Premier League"),
      128 -> CommonLeagueUnitInfo(200092, "Al-Dawry Al-Momtaz"),
      129 -> CommonLeagueUnitInfo(201137, "Güclülər Dəstəsi"),
      130 -> CommonLeagueUnitInfo(209686, "Girabola"),
      131 -> CommonLeagueUnitInfo(209708, "1. CFL"),
      132 -> CommonLeagueUnitInfo(209729, "Premier League"),
      133 -> CommonLeagueUnitInfo(225688, "Yemeni League"),
      134 -> CommonLeagueUnitInfo(225713, "Al-Dawry Al-Omani"),
      135 -> CommonLeagueUnitInfo(225734, "Moçambola"),
      136 -> CommonLeagueUnitInfo(229917, "B-League"),
      137 -> CommonLeagueUnitInfo(229916, "Premier League"),
      138 -> CommonLeagueUnitInfo(237126, "Cambodian Premier League"),
      139 -> CommonLeagueUnitInfo(238747, "Benin Premier League"),
      140 -> CommonLeagueUnitInfo(238748, "Syrian League"),
      141 -> CommonLeagueUnitInfo(238789, "Al Dawri Al Aaam"),
      142 -> CommonLeagueUnitInfo(238790, "Premier League"),
      143 -> CommonLeagueUnitInfo(245936, "Ugandan Super League"),
      144 -> CommonLeagueUnitInfo(245935, "Dhivehi League"),
      145 -> CommonLeagueUnitInfo(252316, "Oliy Liga"),
      146 -> CommonLeagueUnitInfo(252313, "Elite League"),
      147 -> CommonLeagueUnitInfo(252358, "Primera División Cubana"),
      148 -> CommonLeagueUnitInfo(252357, "I.1"),
      149 -> CommonLeagueUnitInfo(258094, "Campeonato Nacional de São Tomé e Príncipe"),
      151 -> CommonLeagueUnitInfo(258136, "I.1"),
      152 -> CommonLeagueUnitInfo(258052, "I.1"),
      153 -> CommonLeagueUnitInfo(258115, "Promé Divishon"),
      154 -> CommonLeagueUnitInfo(258073, "I.1"),
      155 -> CommonLeagueUnitInfo(258477, "I.1"),
      156 -> CommonLeagueUnitInfo(258498, "I.1"),
      157 -> CommonLeagueUnitInfo(258519, "I.1"),
      158 -> CommonLeagueUnitInfo(258540, "I.1"),
      159 -> CommonLeagueUnitInfo(258561, "I.1"),
      160 -> CommonLeagueUnitInfo(258582, "I.1"),
      161 -> CommonLeagueUnitInfo(258603, "I.1"),
      162 -> CommonLeagueUnitInfo(258624, "I.1"),
      163 -> CommonLeagueUnitInfo(258645, "I.1"),
      164 -> CommonLeagueUnitInfo(258666, "I.1"),
      165 -> CommonLeagueUnitInfo(258687, "I.1"),
      1000 -> CommonLeagueUnitInfo(256687, "I.1"),
      1001 -> CommonLeagueUnitInfo(258900, "I.1")
    )
  }

  lazy val leagueLevelNumberTeams: Map[Int, Int] = {
    Map(
      1 -> 1,
      2 -> 4,
      3 -> 16,
      4 -> 64,
      5 -> 256,
      6 -> 1024,
      7 -> 1024,
      8 -> 2048,
      9 -> 2048
    )
  }

  lazy val romansToArab: Map[String, Int] = {
    Map(
      "I" -> 1,
      "II" -> 2,
      "III" -> 3,
      "IV" -> 4,
      "V" -> 5,
      "VI" -> 6,
      "VII" -> 7,
      "VIII" -> 8,
      "IX" -> 9,
      "X" -> 10,
      "XI" -> 11,
      "XII" -> 12,
      "XIII" -> 13,
      "XIV" -> 14,
      "XV" -> 15
    )
  }

  lazy val arabToRomans: Map[Int, String] = {
    Map(
      1 -> "I",
      2 -> "II",
      3 -> "III",
      4 -> "IV",
      5 -> "V",
      6 -> "VI",
      7 -> "VII",
      8 -> "VIII",
      9 -> "IX",
      10 -> "X",
      11 -> "XI",
      12 -> "XII",
      13 -> "XIII",
      14 -> "XIV",
      15 -> "XV"
    )
  }
}
