import React from 'react'
import ExternalPlayerLink from '../common/links/ExternalPlayerLink'
import TopMenu from '../common/menu/TopMenu'
import PlayerData from '../rest/models/leveldata/PlayerData'

const PlayerTopMenu = (props: {data?: PlayerData}) => {
    let externalLink = <ExternalPlayerLink id={props.data?.playerId || 0} black={false} />

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
        },
        {
            href: "/player/" + props.data?.playerId,
            content: props.data?.firstName + ' ' + props.data?.lastName
        }
    ]

    return <TopMenu 
        links={links}
        data={props.data}
        externalLink={externalLink}
        sectionLinks={[]}
    />
}

export default PlayerTopMenu
