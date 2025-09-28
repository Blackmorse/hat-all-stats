import ExternalPlayerLink from '../common/links/ExternalPlayerLink'
import TopMenu from '../common/menu/TopMenu'
import PlayerLevelDataProps from './PlayerLevelDataProps'

const PlayerTopMenu = (props: {levelProps?: PlayerLevelDataProps}) => {
    const externalLink = <ExternalPlayerLink id={props.levelProps?.playerId() || 0} black={false} />

    const links = [
        {
            href: "/league/" + props.levelProps?.leagueId(),
            content: props.levelProps?.leagueName()
        },
        {
            href: "/league/" + props.levelProps?.leagueId() + "/divisionLevel/" + props.levelProps?.divisionLevel,
            content: props.levelProps?.divisionLevelName()
        },
        {
            href: "/leagueUnit/" + props.levelProps?.leagueUnitId(),
            content: props.levelProps?.leagueUnitName()
        },
        {
            href: "/team/" + props.levelProps?.teamId(),
            content: props.levelProps?.teamName()
        },
        {
            href: "/player/" + props.levelProps?.playerId(),
            content: (props.levelProps === undefined) ? '' : props.levelProps.firstName() + ' ' + props.levelProps.lastName()
        }
    ]

    return <TopMenu 
        links={links}
        levelProps={props.levelProps}
        externalLink={externalLink}
        sectionLinks={[]}
    />
}

export default PlayerTopMenu
