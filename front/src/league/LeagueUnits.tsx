import React from 'react';
import LeagueUnitRating from '../rest/models/LeagueUnitRating'
import ModelTable from '../common/ModelTable'
import { getLeagueUnits } from '../rest/Client'


class LeagueUnits extends ModelTable<LeagueUnitRating> {

    columnHeaders(): JSX.Element {
        return <tr>
            <th className="position hint" popped-hint="table.position">table.position_abbr</th>
            <th className="value">table.league</th>
            <th className="value">table.hatstats</th>
            <th className="value">table.midfield</th>
            <th className="value">table.defense</th>
            <th className="value">table.attack</th>
        </tr>
    }

    columnValues(index: number, leagueUnitRating: LeagueUnitRating): JSX.Element {
        return <tr>
            <td>{index + 1}</td>
            <td className="value"><a className="table_link" href="/#">{leagueUnitRating.leagueUnitName}</a></td>
            <td className="value">{leagueUnitRating.hatStats}</td>
            <td className="value">{leagueUnitRating.midfield * 3}</td>
            <td className="value">{leagueUnitRating.defense}</td>
            <td className="value">{leagueUnitRating.attack}</td>
        </tr>
    }

    fetchEntities(leagueId: number, callback: (entities: Array<LeagueUnitRating>) => void): void {
        getLeagueUnits(leagueId, callback)
    }
}

export default LeagueUnits;