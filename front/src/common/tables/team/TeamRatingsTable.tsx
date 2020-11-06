import React from 'react'
import ModelTable, { ModelTablePropsWrapper, SortingState, ModelTableProps } from '../../ModelTable';
import LevelData from '../../../rest/models/leveldata/LevelData';
import TeamRating from '../../../rest/models/team/TeamRating';
import { StatsTypeEnum } from '../../../rest/StatisticsParameters';
import { getTeamRatings } from '../../../rest/Client';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../ModelTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { ratingFormatter } from '../../Formatters'

abstract class TeamRatingsTable<Data extends LevelData, TableProps extends ModelTableProps<Data>>
    extends ModelTable<Data, TableProps, TeamRating> {

    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'rating', {statType: StatsTypeEnum.ROUND, roundNumber: props.modelTableProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamRatings

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.rating' sortingField='rating' sortingState={sortingState} />
                <ModelTableTh title='table.rating_end_of_match' sortingField='rating_end_of_match' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, teamRating: TeamRating): JSX.Element {
        let teamSortingKey = teamRating.teamSortingKey
        return <tr key={"team_ratings_row_" + index}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} name={teamSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={teamSortingKey.leagueUnitId} name={teamSortingKey.leagueUnitName}/></td>
            <td className="value">{ratingFormatter(teamRating.rating)}</td>
            <td className="value">{ratingFormatter(teamRating.ratingEndOfMatch)}</td>
        </tr>
    }
}

export default TeamRatingsTable