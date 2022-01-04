import React from 'react'
import LevelData from '../../../rest/models/leveldata/LevelData'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import OldestTeam from '../../../rest/models/team/OldestTeam'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import ClassicTableSection from '../ClassicTableSection'
import { getOldestTeams } from '../../../rest/Client'
import { SortingState } from '../AbstractTableSection'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../../common/elements/SortingTableTh'
import TeamLink from '../../links/TeamLink'
import LeagueUnitLink from '../../links/LeagueUnitLink'
import { dateFormatter } from '../../Formatters'

abstract class OldestTeamsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
    extends ClassicTableSection<Data, TableProps, OldestTeam> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'founded_date', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getOldestTeams

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation> 
            { (t, { i18n }) =>
            <tr>
                <th className="hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='team.date_of_foundation' sortingField='founded_date' sortingState={sortingState}/>
            </tr>
            }
        </Translation>
    }

    row(index: number, className: string, team: OldestTeam): JSX.Element {
        let teamSortingKey = team.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} 
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? teamSortingKey.leagueId : undefined}/></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{dateFormatter(team.foundedDate)}</td>
        </tr>
    }
}

export default OldestTeamsTable