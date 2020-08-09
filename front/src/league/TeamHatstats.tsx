import React from 'react';
import {getTeamRatings} from '../rest/Client';
import TeamRating from '../rest/models/TeamRating';
import ModelTable from '../common/ModelTable';


class TeamHatstats extends ModelTable<TeamRating> {
    fetchEntities = getTeamRatings
        
    columnHeaders(): JSX.Element {
        return <tr>
            <th className="position hint" popped-hint="table.position">table.position_abbr)</th>
            <th>table.team</th>
            <th className="value">table.league</th>
            <th className="value">table.hatstats</th>
            <th className="value">table.midfield</th>
            <th className="value">table.defense</th>
            <th className="value">table.attack</th>
        </tr>
    }

    columnValues(index: number, teamRating: TeamRating): JSX.Element {
        return <tr>
            <td>{index + 1}</td>
            <td><a className="table_link" href="/#">{teamRating.teamName}</a></td>
            <td className="value"><a className="table_link" href="/#">{teamRating.leagueUnitName}</a></td>
            <td className="value">{teamRating.hatStats}</td>
            <td className="value">{teamRating.midfield * 3}</td>
            <td className="value">{teamRating.defense}</td>
            <td className="value">{teamRating.attack}</td>
        </tr>
    }

}

export default TeamHatstats