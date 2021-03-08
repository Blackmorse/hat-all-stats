import LevelData from '../../../rest/models/leveldata/LevelData';
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import React from 'react';
import MatchTopHatstats from '../../../rest/models/match/MatchTopHatstats';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import TeamLink from '../../links/TeamLink'
import LeagueUnitLink from '../../links/LeagueUnitLink'
import { getSurprisingMatches } from '../../../rest/Client';
import ExternalMatchLink from '../../links/ExternalMatchLink';

abstract class MatchSurprisingTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends ClassicTableSection<Data, TableProps, MatchTopHatstats> {
    
    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'abs_hatstats_difference', {statType: StatsTypeEnum.ACCUMULATE},
            [StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getSurprisingMatches

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th className="value">{t('table.league')}</th>
                <th>{t('table.team')}</th>
                <ModelTableTh title='table.hatstats' sortingField='abs_hatstats_difference' sortingState={sortingState} />
                <ModelTableTh title='overview.goals' sortingField='abs_goals_difference' sortingState={sortingState} />
                <ModelTableTh title='table.hatstats' sortingField='abs_hatstats_difference' sortingState={sortingState} />
                <th>{t('table.team')}</th>
            </tr>
            }
        </Translation>
    }

    columnValues(index: number, matchHatstats: MatchTopHatstats): JSX.Element {
        return <>
            <td>{index + 1}</td>
            <td className="value"><LeagueUnitLink id={matchHatstats.homeTeam.leagueUnitId} text={matchHatstats.homeTeam.leagueUnitName} /></td>
            <td className="value"><TeamLink id={matchHatstats.homeTeam.teamId} text={matchHatstats.homeTeam.teamName} 
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? matchHatstats.homeTeam.leagueId : undefined}/></td>
            <td className="value">{matchHatstats.homeHatstats}</td>
            <td className="value">{matchHatstats.homeGoals} : {matchHatstats.awayGoals} <ExternalMatchLink id={matchHatstats.matchId} /></td>
            <td className="value">{matchHatstats.awayHatstats}</td>
            <td className="value"><TeamLink id={matchHatstats.awayTeam.teamId} text={matchHatstats.awayTeam.teamName} 
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? matchHatstats.awayTeam.leagueId : undefined}/></td>           
        </>
    }
}

export default MatchSurprisingTable