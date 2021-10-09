import NearestMatch from '../match/NearestMatch'
import SingleMatch from '../match/SingleMatch';

export default interface MatchOpponentCombinedInfo {
    currentTeamPlayedMatches: Array<NearestMatch>
    currentTeamNextOpponents: Array<[number, string]>
    opponentPlayedMatches: Array<NearestMatch>,
    simulatedMatch?: SingleMatch
}