package com.blackmorse.hattrick.clickhouse;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MatchDetails {
    private Integer leagueId;
    private Integer divisionLevel;
    private Integer leagueUnitId;
    private Long teamId;
    private String teamName;
    private Date date;
    private Integer round;
    private Long matchId;

    private String formation;
    private Integer tacticType;
    private Integer tacticSkill;
    private Integer ratingMidfield;
    private Integer ratingRightDef;
    private Integer ratingLeftDef;
    private Integer ratingMidDef;
    private Integer ratingRightAtt;
    private Integer ratingMidAtt;
    private Integer ratingLeftAtt;
    private Integer ratingIndirectSetPiecesDef;
    private Integer ratingIndirectSetPiecesAtt;
}
