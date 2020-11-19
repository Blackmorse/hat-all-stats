import ExternalLink from './ExternalLink';

class ExternalLeagueLink extends ExternalLink {
    path(): string {
        return '/World/Leagues/League.aspx?LeagueID=' + this.props.id
    }
}

export default ExternalLeagueLink