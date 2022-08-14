import React from 'react';
import TeamAgeInjury from "../../../rest/models/team/TeamAgeInjury";
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import '../../../i18n'
import { Translation } from 'react-i18next'
import { StatsTypeEnum } from "../../../rest/models/StatisticsParameters";
import { getTeamAgeInjuries } from '../../../rest/Client';
import ModelTableTh from '../../../common/elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { injuryFormatter, ageFormatter } from '../../Formatters'
import HattidTooltip from '../../elements/HattidTooltip';

abstract class TeamAgeInjuryTable<TableProps extends LevelDataProps>
    extends ClassicTableSection<TableProps, TeamAgeInjury> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'age', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamAgeInjuries

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
                <ModelTableTh title='table.age' sorting={{field: 'age', state: sortingState}} />
                <ModelTableTh title='table.total_injury_weeks' sorting={{field: 'injury', state: sortingState}} />
                <ModelTableTh title='table.total_injury_number' sorting={{field: 'injury_count', state: sortingState}} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, teamAgeInjury: TeamAgeInjury): JSX.Element {
        let teamSortingKey = teamAgeInjury.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{ageFormatter(teamAgeInjury.age)}</td>
            <td className="text-center">{injuryFormatter(teamAgeInjury.injury)}</td>
            <td className="text-center">{teamAgeInjury.injuryCount}</td>
        </tr>
    }
}

export default TeamAgeInjuryTable
