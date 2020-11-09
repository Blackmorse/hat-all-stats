import HattidLink, { LinkProps } from './HattidLink';

interface Props extends LinkProps {
    leagueId: number,
    divisionLevel: number,
}

class DivisionLevelLink extends HattidLink<Props> {
    baseString(): string {
        return "/league/" + this.props.leagueId + "/divisionLevel/" + this.props.divisionLevel
    }
}

export default DivisionLevelLink