import React from 'react';
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import '../../../i18n'
import { Translation } from 'react-i18next'
import TeamCards from '../../../rest/models/team/TeamCards';
import ModelTableTh from '../../../common/elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { yellowCards, redCards } from '../../Formatters'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getTeamCards } from '../../../rest/Client';
import HattidTooltip from '../../elements/HattidTooltip';


abstract class TeamCardsTable<TableProps extends LevelDataProps>
    extends ClassicTableSection<TableProps, TeamCards> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'yellow_cards', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamCards

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
                <ModelTableTh title='table.yellow_cards' sortingField='yellow_cards' sortingState={sortingState} />
                <ModelTableTh title='table.red_cards' sortingField='red_cards' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, teamCards: TeamCards): JSX.Element {
        let teamSortingKey = teamCards.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{yellowCards(teamCards.yellowCards)}</td>
            <td className="text-center">{redCards(teamCards.redCards)}</td>
        </tr>
    }
}

export default TeamCardsTable
