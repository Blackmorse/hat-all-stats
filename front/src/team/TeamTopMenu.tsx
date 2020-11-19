import React from 'react'
import TeamData from '../rest/models/leveldata/TeamData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import ExternalTeamLink from '../common/links/ExternalTeamLink'

interface Props {
    teamData?: TeamData
}

class TeamTopMenu extends TopMenu<Props> {
    externalLink(): JSX.Element | undefined {
        return <ExternalTeamLink id={this.props.teamData?.teamId || 0} black={false} />
    }
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