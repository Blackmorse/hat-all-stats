import ExternalLink from './ExternalLink'

class ExternalTeamLink extends ExternalLink {
    path(): string {
        return '/Club/?TeamID=' + this.props.id    
    }
}

export default ExternalTeamLink