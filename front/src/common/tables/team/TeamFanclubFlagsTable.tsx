import React from 'react'
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import LevelData from '../../../rest/models/leveldata/LevelData';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getTeamFanclubFlags } from '../../../rest/Client';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import TeamFanclubFlags from '../../../rest/models/team/TeamFanclubFlags';
import HattidTooltip from '../../elements/HattidTooltip';

abstract class TeamFanclubFlagsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends ClassicTableSection<Data, TableProps, TeamFanclubFlags> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'fanclub_size', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamFanclubFlags

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
                <ModelTableTh title='table.fanclub_size' sortingField='fanclub_size' sortingState={sortingState} />
                <ModelTableTh title='table.home_flags' sortingField='home_flags' sortingState={sortingState} />
                <ModelTableTh title='table.away_flags' sortingField='away_flags' sortingState={sortingState} />
                <ModelTableTh title='table.all_flags' sortingField='all_flags' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, teamFanclubFlags: TeamFanclubFlags): JSX.Element {
        let teamSortingKey = teamFanclubFlags.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{teamFanclubFlags.fanclubSize}</td>
            <td className="text-center">{teamFanclubFlags.homeFlags}</td>
            <td className="text-center">{teamFanclubFlags.awayFlags}</td>
            <td className="text-center">{teamFanclubFlags.allFlags}</td>
        </tr>
    }
}

export default TeamFanclubFlagsTable
