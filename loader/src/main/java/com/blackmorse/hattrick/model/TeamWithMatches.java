package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamWithMatches {
    private final Team team;
    private final List<Match> matches;
}
