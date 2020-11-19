import ExternalLink from './ExternalLink'

class ExternalMatchLink extends ExternalLink {
    path(): string {
        return '/Club/Matches/Match.aspx?matchID=' + this.props.id
    }
}

export default ExternalMatchLink