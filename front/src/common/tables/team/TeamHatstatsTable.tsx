import React from 'react';
import TeamHatstats from '../../../rest/models/team/TeamHatstats';
import ClassicTableSection from '../ClassicTableSection';
import { SortingState } from '../AbstractTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import ModelTableTh from '../../elements/SortingTableTh'
import '../../../i18n'
import { Translation } from 'react-i18next'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import LevelData from '../../../rest/models/leveldata/LevelData';
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { getTeamHatstats } from '../../../rest/Client';
import { loddarStats } from '../../Formatters'
import Section from '../../sections/Section';
import HattidTooltip from '../../elements/HattidTooltip';

class TeamHatstatsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
        extends ClassicTableSection<Data, TableProps, TeamHatstats> {
    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'hatstats', {statType: StatsTypeEnum.AVG},
            [StatsTypeEnum.AVG, StatsTypeEnum.MAX, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamHatstats
    
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
                <ModelTableTh title='table.hatstats' sortingField='hatstats' sortingState={sortingState} />
                <ModelTableTh title='table.midfield' sortingField='midfield' sortingState={sortingState} />
                <ModelTableTh title='table.defense' sortingField='defense' sortingState={sortingState} />
                <ModelTableTh title='table.attack' sortingField='attack' sortingState={sortingState} />
                <ModelTableTh title='table.loddar_stats' sortingField='loddar_stats' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, teamHatstats: TeamHatstats): JSX.Element {
        let teamSortingKey = teamHatstats.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} 
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? teamSortingKey.leagueId : undefined} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{teamHatstats.hatStats}</td>
            <td className="text-center">{teamHatstats.midfield * 3}</td>
            <td className="text-center">{teamHatstats.defense}</td>
            <td className="text-center">{teamHatstats.attack}</td>
            <td className="text-center">{loddarStats(teamHatstats.loddarStats)}</td>
        </tr>
    }

}

export const TeamHatstatsTableSection = Section(TeamHatstatsTable)
export default TeamHatstatsTable
