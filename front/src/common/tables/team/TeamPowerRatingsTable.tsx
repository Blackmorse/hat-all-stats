import React from 'react';
import LevelData from '../../../rest/models/leveldata/LevelData';
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../../common/elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getTeamPowerRatings } from '../../../rest/Client';
import TeamPowerRating from '../../../rest/models/team/TeamPowerRating';
import HattidTooltip from '../../elements/HattidTooltip';

abstract class TeamPowerRatingsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends ClassicTableSection<Data, TableProps, TeamPowerRating> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'power_rating', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamPowerRatings

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='table.power_rating' sortingField='power_rating' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }
    
    row(index: number, className: string, teamPowerRating: TeamPowerRating): JSX.Element {
        let teamSortingKey = teamPowerRating.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{teamPowerRating.powerRating}</td>
        </tr>
    }
}

export default TeamPowerRatingsTable
