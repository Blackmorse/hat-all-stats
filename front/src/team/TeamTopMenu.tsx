import '../common/menu/TopMenu.css'
import ExternalTeamLink from '../common/links/ExternalTeamLink'
import TeamLevelDataProps from './TeamLevelDataProps';
import { TopMenuMUI } from '../common/menu/TopMenuMUI';
import ExternalLeagueLink from '../common/links/ExternalLeagueLink';

const TeamTopMenu = (props: {levelProps?: TeamLevelDataProps}) => {
    const externaTeamlLink = <ExternalTeamLink id={props.levelProps?.teamId() || 0} black={false} /> 
    const externalLeagueLink = <ExternalLeagueLink id={props.levelProps?.leagueId() || 1000} black={false} />
    const links = [
            {
                href: "/league/" + props.levelProps?.leagueId(), 
                content: props.levelProps?.leagueName(),
                afterLink: externalLeagueLink
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
                content: props.levelProps?.teamName(),
                afterLink: externaTeamlLink
            }
        ]

    return <TopMenuMUI
        links={links}
        levelProps={props.levelProps}
        externalLink={externaTeamlLink}
        sectionLinks={[]}
        />
}

export default TeamTopMenu
