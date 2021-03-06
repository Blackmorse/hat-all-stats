package chpp.commonmodels

object MatchType extends Enumeration {
  val LEAGUE_MATCH = Value("1")
  val QUALIFICATION_MATCH = Value("2")
  val CUP_MATCH = Value("3")
  val FRIENDLY_NORMAL = Value("4")
  val FRIENDLY_CUP = Value("5")
  val HATTRICK_MASTERS = Value("7")
  val INTERNATIONAL_FRIENDLY_NORMAL = Value("8")
  val INTERNATIONAL_FRIENDLY_CUP = Value("9")
  val NATIONAL_TEAMS_NORMAL = Value("10")
  val NATIONAL_TEAMS_CUP = Value("11")
  val NATIONAL_TEAMS_FRIENDLY = Value("12")
  val TOURNAMENT_LEAGUE = Value("50")
  val TOURNAMENT_PLAYOFF = Value("51")
  val SINGLE_MATCH = Value("61")
  val LADDER_MATCH = Value("62")
  val PREPARATION_MATCH = Value("80")
  val YOUTH_LEAGUE = Value("100")
  val YOUTH_FRIENDLY = Value("101")
  val YOUTH_FRIENDLY_CUP = Value("103")
  val YOUTH_INTERNATIONAL_FRIENDLY = Value("105")
  val YOUTH_INTERNATIONAL_FRIENDLY_CUP = Value("106")
}
