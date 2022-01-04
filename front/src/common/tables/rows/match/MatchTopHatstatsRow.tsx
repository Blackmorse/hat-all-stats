import React from 'react'
import MatchTopHatstats from '../../../../rest/models/match/MatchTopHatstats';
import { loddarStats } from '../../../Formatters';
import ExternalMatchLink from '../../../links/ExternalMatchLink';
import LeagueUnitLink from '../../../links/LeagueUnitLink';
import TeamLink from '../../../links/TeamLink';
import MatchRow, { TableRowProps } from './MatchRow'

class MatchTopHatstatsRow extends MatchRow<MatchTopHatstats, TableRowProps<MatchTopHatstats>> {
    columns(index: number, matchHatstats: MatchTopHatstats): JSX.Element[] {
        return [
            <td key={'match_td_index_' + matchHatstats.matchId}>{index + 1}</td>,
            <td key={'match_td_league_unit_' + matchHatstats.matchId} className="text-center"><LeagueUnitLink id={matchHatstats.homeTeam.leagueUnitId} text={matchHatstats.homeTeam.leagueUnitName} /></td>,
            <td key={'match_td_home_team_' + matchHatstats.matchId} className="text-center"><TeamLink id={matchHatstats.homeTeam.teamId} text={matchHatstats.homeTeam.teamName}
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? matchHatstats.homeTeam.leagueId : undefined}/></td>,
            <td key={'match_td_home_loddar_' + matchHatstats.matchId} className="text-center">{loddarStats(matchHatstats.homeLoddarStats)}</td>,
            <td key={'match_td_home_hatstats_' + matchHatstats.matchId} className="text-center">{matchHatstats.homeHatstats}</td>,
            <td key={'match_td_result_' + matchHatstats.matchId} className="text-center">{matchHatstats.homeGoals} : {matchHatstats.awayGoals} <ExternalMatchLink id={matchHatstats.matchId} /></td>,
            <td key={'match_td_away_hatstats_' + matchHatstats.matchId} className="text-center">{matchHatstats.awayHatstats}</td>,
            <td key={'match_td_away_loddars_' + matchHatstats.matchId} className="text-center">{loddarStats(matchHatstats.awayLoddarStats)}</td>,
            <td key={'match_td_away_team_' + matchHatstats.matchId} className="text-center"><TeamLink id={matchHatstats.awayTeam.teamId} text={matchHatstats.awayTeam.teamName} 
                flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? matchHatstats.awayTeam.leagueId : undefined}/></td> 
        ]
    }
}

export default MatchTopHatstatsRow