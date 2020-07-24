package com.blackmorse.hattrick.clickhouse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
@Builder
public class MatchDetails {
    @AllArgsConstructor
    @Getter
    public enum IsHomeMatch {
        HOME(1, "home"),
        AWAY(0, "away");

        private int value;
        private String stringValue;
    }

    private final Integer season;
    private final Integer leagueId;
    private final Integer divisionLevel;
    private final Long leagueUnitId;
    private final String leagueUnitName;
    private final Long teamId;
    private final String teamName;
    private final Date date;
    private final Integer round;
    private final Long matchId;

    private final IsHomeMatch isHomeMatch;
    private final Integer goals;
    private final Integer enemyGoals;

    private final Integer soldTotal;

    private final String formation;
    private final Integer tacticType;
    private final Integer tacticSkill;
    private final Integer ratingMidfield;
    private final Integer ratingRightDef;
    private final Integer ratingLeftDef;
    private final Integer ratingMidDef;
    private final Integer ratingRightAtt;
    private final Integer ratingMidAtt;
    private final Integer ratingLeftAtt;
    private final Integer ratingIndirectSetPiecesDef;
    private final Integer ratingIndirectSetPiecesAtt;

    private final String oppositeFormation;
    private final Integer oppositeTacticType;
    private final Integer oppositeTacticSkill;
    private final Integer oppositeRatingMidfield;
    private final Integer oppositeRatingRightDef;
    private final Integer oppositeRatingLeftDef;
    private final Integer oppositeRatingMidDef;
    private final Integer oppositeRatingRightAtt;
    private final Integer oppositeRatingMidAtt;
    private final Integer oppositeRatingLeftAtt;
    private final Integer oppositeRatingIndirectSetPiecesDef;
    private final Integer oppositeRatingIndirectSetPiecesAtt;
}
