import TeamData from '../rest/models/TeamData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';

interface Props {
    teamData?: TeamData
}

class TeamTopMenu extends TopMenu<Props> {
    links(): [string, string?][] {
        return [
            ["/league/" + this.props.teamData?.leagueId, this.props.teamData?.leagueName],
            ["/league/" + this.props.teamData?.leagueId + "/divisionLevel/" + this.props.teamData?.divisionLevel, this.props.teamData?.divisionLevelName],
            ["/leagueUnit/" + this.props.teamData?.leagueUnitId, this.props.teamData?.leagueUnitName],
            ["/team/" + this.props.teamData?.teamId, this.props.teamData?.teamName]
        ]
    }

    selectBox(): JSX.Element | undefined {
        return undefined
    }
}

export default TeamTopMenu