import React from 'react'
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TeamRating from '../../../rest/models/team/TeamRating';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getTeamRatings } from '../../../rest/Client';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { ratingFormatter } from '../../Formatters'
import HattidTooltip from '../../elements/HattidTooltip';

abstract class TeamRatingsTable<TableProps extends LevelDataProps>
    extends ClassicTableSection<TableProps, TeamRating> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'rating', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamRatings

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            t =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='table.rating' sorting={{field: 'rating', state: sortingState}} />
                <ModelTableTh title='table.rating_end_of_match' sorting={{field: 'rating_end_of_match', state: sortingState}} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, teamRating: TeamRating): JSX.Element {
        let teamSortingKey = teamRating.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{ratingFormatter(teamRating.rating)}</td>
            <td className="text-center">{ratingFormatter(teamRating.ratingEndOfMatch)}</td>
        </tr>
    }
}

export default TeamRatingsTable
