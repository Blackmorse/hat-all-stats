import ExternalLink from './ExternalLink'

class ExternalPlayerLink extends ExternalLink {
    path(): string {
        return '/Club/Players/Player.aspx?playerId=' + this.props.id
    }
}

export default ExternalPlayerLink