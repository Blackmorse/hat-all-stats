package com.blackmorse.hattrick.model;

import com.blackmorse.hattrick.api.teamdetails.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TeamWithMatchAndTeamDetails {
    private final TeamWithMatch teamWithMatch;
    private final Team teamDetails;
}
