import LevelData from '../../../rest/models/leveldata/LevelData';
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps' 
import React from 'react';
import MatchSpectators from '../../../rest/models/match/MatchSpectators';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import TeamLink from '../../links/TeamLink'
import LeagueUnitLink from '../../links/LeagueUnitLink'
import { getMatchSpectators } from '../../../rest/Client';
import { commasSeparated } from '../../Formatters'
import ExternalMatchLink from '../../links/ExternalMatchLink';

abstract class MatchSpectatorsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends ClassicTableSection<Data, TableProps, MatchSpectators> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'sold_total', {statType: StatsTypeEnum.ACCUMULATE},
            [StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getMatchSpectators

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th className="value">{t('table.league')}</th>
                <th>{t('table.team')}</th>
                <th className="value"></th>
                <th>{t('table.team')}</th>
                <ModelTableTh title='matches.spectatos' sortingField='sold_total' sortingState={sortingState} />
            </tr>
            }
        </Translation>
    }

    columnValues(index: number, matchSpectators: MatchSpectators): JSX.Element {
        return <>
            <td>{index + 1}</td>
            <td className="value"><LeagueUnitLink id={matchSpectators.homeTeam.leagueUnitId} text={matchSpectators.homeTeam.leagueUnitName} /></td>
            <td className="value"><TeamLink id={matchSpectators.homeTeam.teamId} text={matchSpectators.homeTeam.teamName} /></td>
            <td className="value">{matchSpectators.homeGoals} : {matchSpectators.awayGoals} <ExternalMatchLink id={matchSpectators.matchId} /></td>
            <td className="value"><TeamLink id={matchSpectators.awayTeam.teamId} text={matchSpectators.awayTeam.teamName} /></td>           
            <td className="value">{commasSeparated(matchSpectators.spectators)}</td>
        </>
    }
}

export default MatchSpectatorsTable