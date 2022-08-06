import './TableLink.css'
import HattidLink, { LinkProps } from './HattidLink';

interface Props extends LinkProps {
    id: number
}

class PlayerLink extends HattidLink<Props> {
    baseString(): string {
        return "/player/" + this.props.id
    }
}

export default PlayerLink
