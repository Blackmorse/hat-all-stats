package com.blackmorse.hattrick.model;

import com.blackmorse.hattrick.api.matchdetails.model.MatchDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamWithMatchDetails {
    private final TeamWithMatch teamWithMatch;
    private final MatchDetails matchDetails;
}
