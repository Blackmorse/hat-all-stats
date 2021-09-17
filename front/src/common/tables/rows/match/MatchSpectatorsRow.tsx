import React from 'react'
import MatchSpectators from '../../../../rest/models/match/MatchSpectators'
import { commasSeparated } from '../../../Formatters'
import ExternalMatchLink from '../../../links/ExternalMatchLink'
import LeagueUnitLink from '../../../links/LeagueUnitLink'
import TeamLink from '../../../links/TeamLink'
import MatchRow, { TableRowProps } from './MatchRow'

class MatchSpectatorsRow extends MatchRow<MatchSpectators, TableRowProps<MatchSpectators>> {
    columns(index: number, matchSpectators: MatchSpectators): JSX.Element[] {
        return [
            <td key={'td_match_index_' + matchSpectators.matchId}>{index + 1}</td>,
            <td key={'td_match_league_unit_' + matchSpectators.matchId} className="value"><LeagueUnitLink id={matchSpectators.homeTeam.leagueUnitId} text={matchSpectators.homeTeam.leagueUnitName} /></td>,
            <td key={'td_match_home_team_' + matchSpectators.matchId} className="value"><TeamLink id={matchSpectators.homeTeam.teamId} text={matchSpectators.homeTeam.teamName} /></td>,
            <td key={'td_match_result_' + matchSpectators.matchId} className="value">{matchSpectators.homeGoals} : {matchSpectators.awayGoals} <ExternalMatchLink id={matchSpectators.matchId} /></td>,
            <td key={'td_match_away_team_' + matchSpectators.matchId} className="value"><TeamLink id={matchSpectators.awayTeam.teamId} text={matchSpectators.awayTeam.teamName} /></td>,
            <td key={'td_match_spectators_' + matchSpectators.matchId} className="value">{commasSeparated(matchSpectators.spectators)}</td>
        ]
    }
}

export default MatchSpectatorsRow