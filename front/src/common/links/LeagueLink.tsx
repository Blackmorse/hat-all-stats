import HattidLink, { LinkProps } from './HattidLink';

interface Props extends LinkProps {
    id: number
}


class LeagueLink extends HattidLink<Props> {
    baseString(): string {
        return "/league/" + this.props.id
    }
}

export default LeagueLink