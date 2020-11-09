import HattidLink, { LinkProps } from './HattidLink';


interface Props extends LinkProps {
    id: number,
    callback?: () => void
}

class TeamLink extends HattidLink<Props> {
    baseString(): string {
        return "/team/" + this.props.id
    }
}

export default TeamLink