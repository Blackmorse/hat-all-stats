package com.blackmorse.hattrick.clickhouse.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MatchDetails {
    private Integer season;
    private Integer leagueId;
    private Integer divisionLevel;
    private Long leagueUnitId;
    private String leagueUnitName;
    private Long teamId;
    private String teamName;
    private Date date;
    private Integer round;
    private Long matchId;

    private Integer goals;
    private Integer enemyGoals;
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
