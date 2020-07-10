package com.blackmorse.hattrick.promotions.model;

import com.blackmorse.hattrick.model.Team;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PromoteTeam {
    public Team team;
    public Integer season;

    public Integer position;
    public Integer points;
    public Integer diff;
    public Integer scored;
    public PromoteType promoteType;

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof PromoteTeam)) {
            return false;
        }
        PromoteTeam other = (PromoteTeam) obj;

        return this.position.equals(other.position) &&
                this.points.equals(other.points) &&
                this.diff.equals(other.diff) &&
                this.scored.equals(other.scored);
    }
}
