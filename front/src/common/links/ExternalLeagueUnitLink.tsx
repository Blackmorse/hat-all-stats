import ExternalLink from './ExternalLink'

class ExternalLeagueUnitLink extends ExternalLink {
    path(): string {
        return '/World/Series/Default.aspx?LeagueLevelUnitID=' + this.props.id
    }
}

export default ExternalLeagueUnitLink