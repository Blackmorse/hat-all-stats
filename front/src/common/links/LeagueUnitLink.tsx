import './TableLink.css'
import HattidLink, { LinkProps } from './HattidLink';

interface Props extends LinkProps {
    id: number
}

class LeagueUnitLink extends HattidLink<Props> {
    baseString(): string {
        return "/leagueUnit/" + this.props.id
    }
}

export default LeagueUnitLink