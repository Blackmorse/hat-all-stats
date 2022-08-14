import React from 'react'
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
import HattidTooltip from '../../elements/HattidTooltip'

abstract class OldestTeamsTable<TableProps extends LevelDataProps> 
    extends ClassicTableSection<TableProps, OldestTeam> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'founded_date', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getOldestTeams

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation> 
            { t =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='team.date_of_foundation' sorting={{field: 'founded_date', state: sortingState}}/>
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
