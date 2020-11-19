import LevelData from '../../../rest/models/leveldata/LevelData';
import TableSection, { SortingState } from '../../sections/TableSection';
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import React from 'react';
import MatchTopHatstats from '../../../rest/models/match/MatchTopHatstats';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import TeamLink from '../../links/TeamLink'
import LeagueUnitLink from '../../links/LeagueUnitLink'
import { getMatchesTopHatstats } from '../../../rest/Client';
import ExternalMatchLink from '../../links/ExternalMatchLink';

abstract class MatchTopHatstatsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends TableSection<Data, TableProps, MatchTopHatstats> {
    
    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'sum_hatstats', {statType: StatsTypeEnum.ACCUMULATE},
            [StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getMatchesTopHatstats

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th className="value">{t('table.league')}</th>
                <th>{t('table.team')}</th>
                <ModelTableTh title='table.hatstats' sortingField='sum_hatstats' sortingState={sortingState} />
                <th></th>
                <ModelTableTh title='table.hatstats' sortingField='sum_hatstats' sortingState={sortingState} />
                <th>{t('table.team')}</th>
            </tr>
            }
        </Translation>
    }

    columnValues(index: number, matchHatstats: MatchTopHatstats): JSX.Element {
        return <tr key={"top_matches_row" + index}>
            <td>{index + 1}</td>
            <td className="value"><LeagueUnitLink id={matchHatstats.homeTeam.leagueUnitId} text={matchHatstats.homeTeam.leagueUnitName} /></td>
            <td className="value"><TeamLink id={matchHatstats.homeTeam.teamId} text={matchHatstats.homeTeam.teamName} /></td>
            <td className="value">{matchHatstats.homeHatstats}</td>
            <td className="value">{matchHatstats.homeGoals} : {matchHatstats.awayGoals} <ExternalMatchLink id={matchHatstats.matchId} /></td>
            <td className="value">{matchHatstats.awayHatstats}</td>
            <td className="value"><TeamLink id={matchHatstats.awayTeam.teamId} text={matchHatstats.awayTeam.teamName} /></td>           
        </tr>
    }
}

export default MatchTopHatstatsTable