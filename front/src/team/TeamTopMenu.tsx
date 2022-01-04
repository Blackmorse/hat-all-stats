import React from 'react'
import TeamData from '../rest/models/leveldata/TeamData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import ExternalTeamLink from '../common/links/ExternalTeamLink'

interface Props {
    data?: TeamData
}

class TeamTopMenu extends TopMenu<TeamData, Props> {
    externalLink(): JSX.Element | undefined {
        return <ExternalTeamLink id={this.props.data?.teamId || 0} black={false} />
    }
    links(): [string, string?][] {
        return [
            ["/league/" + this.props.data?.leagueId, this.props.data?.leagueName],
            ["/league/" + this.props.data?.leagueId + "/divisionLevel/" + this.props.data?.divisionLevel, this.props.data?.divisionLevelName],
            ["/leagueUnit/" + this.props.data?.leagueUnitId, this.props.data?.leagueUnitName],
            ["/team/" + this.props.data?.teamId, this.props.data?.teamName]
        ]
    }

    selectBox(): JSX.Element | undefined {
        return undefined
    }
}

export default TeamTopMenu