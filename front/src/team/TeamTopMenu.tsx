import React from 'react'
import TeamData from '../rest/models/leveldata/TeamData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import ExternalTeamLink from '../common/links/ExternalTeamLink'

const TeamTopMenu = (props: {data?: TeamData}) => {
    let externalLink = <ExternalTeamLink id={props.data?.teamId || 0} black={false} /> 

    let links = [
            {
                href: "/league/" + props.data?.leagueId, 
                content: props.data?.leagueName
            },
            {
                href: "/league/" + props.data?.leagueId + "/divisionLevel/" + props.data?.divisionLevel, 
                content: props.data?.divisionLevelName
            },
            {
                href: "/leagueUnit/" + props.data?.leagueUnitId, 
                content: props.data?.leagueUnitName
            },
            {
                href: "/team/" + props.data?.teamId, 
                content: props.data?.teamName
            }
        ]

    return <TopMenu
        links={links}
        data={props.data}
        externalLink={externalLink}
        sectionLinks={[]}
        />
}

export default TeamTopMenu
