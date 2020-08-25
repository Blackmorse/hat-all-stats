import React from 'react';
import {getTeamRatings} from '../rest/Client';
import TeamRating from '../rest/models/TeamRating';
import ModelTable from '../common/ModelTable';
import ModelTableTh from '../common/ModelTableTh'
import '../i18n'
import { Translation } from 'react-i18next'
import { LeagueProps } from './League';



class TeamHatstats extends ModelTable<TeamRating> {

    constructor(props: LeagueProps) {
        super(props, 'menu.best_teams', [ 'hatstats' ])
    }

    fetchEntities = getTeamRatings
        
    columnHeaders(): JSX.Element {
        const sortingState = {
            callback: this.sortingChanged,
            currentSorting: this.state.statisticsParameters.sortingField
        }

        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.hatstats' sortingField='hatstats' sortingState={sortingState} />
                <ModelTableTh title='table.midfield' sortingField='midfield' sortingState={sortingState} />
                <ModelTableTh title='table.defense' sortingField='defense' sortingState={sortingState} />
                <ModelTableTh title='table.attack' sortingField='attack' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, teamRating: TeamRating): JSX.Element {
        return <tr key={"team_hatstats_row_" + index}>
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