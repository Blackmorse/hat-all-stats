import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import ExternalTeamLink from '../common/links/ExternalTeamLink'
import TeamLevelDataProps from './TeamLevelDataProps';

const TeamTopMenu = (props: {levelProps?: TeamLevelDataProps}) => {
    const externalLink = <ExternalTeamLink id={props.levelProps?.teamId() || 0} black={false} /> 

    const links = [
            {
                href: "/league/" + props.levelProps?.leagueId(), 
                content: props.levelProps?.leagueName()
            },
            {
                href: "/league/" + props.levelProps?.leagueId() + "/divisionLevel/" + props.levelProps?.divisionLevel(), 
                content: props.levelProps?.divisionLevelName()
            },
            {
                href: "/leagueUnit/" + props.levelProps?.leagueUnitId(),
                content: props.levelProps?.leagueUnitName()
            },
            {
                href: "/team/" + props.levelProps?.teamId(), 
                content: props.levelProps?.teamName()
            }
        ]

    return <TopMenu
        links={links}
        levelProps={props.levelProps}
        externalLink={externalLink}
        sectionLinks={[]}
        />
}

export default TeamTopMenu
