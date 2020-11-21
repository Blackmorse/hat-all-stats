import React from 'react'
import TableSection, { SortingState } from '../../sections/TableSection';
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import LevelData from '../../../rest/models/leveldata/LevelData';
import TeamRating from '../../../rest/models/team/TeamRating';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getTeamRatings } from '../../../rest/Client';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { ratingFormatter } from '../../Formatters'

abstract class TeamRatingsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends TableSection<Data, TableProps, TeamRating> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'rating', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
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
        return <>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="value">{ratingFormatter(teamRating.rating)}</td>
            <td className="value">{ratingFormatter(teamRating.ratingEndOfMatch)}</td>
        </>
    }
}

export default TeamRatingsTable