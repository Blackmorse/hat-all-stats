import React from 'react'
import MatchTopHatstats from '../../../../rest/models/match/MatchTopHatstats';
import { loddarStats } from '../../../Formatters';
import ExternalMatchLink from '../../../links/ExternalMatchLink';
import LeagueUnitLink from '../../../links/LeagueUnitLink';
import TeamLink from '../../../links/TeamLink';
import MatchRow, { TableRowProps } from './MatchRow'

class MatchSurprisingRow extends MatchRow<MatchTopHatstats, TableRowProps<MatchTopHatstats>> {
    columns(index: number, rowModel: MatchTopHatstats): JSX.Element[] {
        let matchHatstats = this.props.rowModel
        return [
            <td key={'td_match_index_' + rowModel.matchId}>{index + 1}</td>,
            <td key={'td_match_home_league_unit_' + rowModel.matchId} className="value"><LeagueUnitLink id={matchHatstats.homeTeam.leagueUnitId} text={matchHatstats.homeTeam.leagueUnitName} /></td>,
            <td key={'td_match_home_team_link_' + rowModel.matchId} className="value"><TeamLink id={matchHatstats.homeTeam.teamId} text={matchHatstats.homeTeam.teamName} 
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? matchHatstats.homeTeam.leagueId : undefined}/></td>,
            <td key={'td_match_home_loddar_' + rowModel.matchId} className="value">{loddarStats(matchHatstats.homeLoddarStats)}</td>,
            <td key={'td_match_home_hatstats_' + rowModel.matchId} className="value">{matchHatstats.homeHatstats}</td>,
            <td key={'td_match_result_' + rowModel.matchId} className="value">{matchHatstats.homeGoals} : {matchHatstats.awayGoals} <ExternalMatchLink id={matchHatstats.matchId} /></td>,
            <td key={'td_match_away_hatstats_' + rowModel.matchId} className="value">{matchHatstats.awayHatstats}</td>,
            <td key={'td_match_away_loddar_' + rowModel.matchId} className="value">{loddarStats(matchHatstats.awayLoddarStats)}</td>,
            <td key={'td_match_away_team_link_' + rowModel.matchId} className="value"><TeamLink id={matchHatstats.awayTeam.teamId} text={matchHatstats.awayTeam.teamName}
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? matchHatstats.awayTeam.leagueId : undefined}/></td>,
        ]
    }
}

export default MatchSurprisingRow