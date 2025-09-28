import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import ExternalLeagueUnitLink from '../common/links/ExternalLeagueUnitLink';
import { Form } from 'react-bootstrap';
import {useNavigate} from 'react-router';
import {nextLeagueUnit, previousLeagueUnit} from '../common/Utils';
import {getLeagueUnitIdByName} from '../rest/Client';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';

const LeagueUnitTopMenu = (props: {levelProps?: LeagueUnitLevelDataProps}) => {
    const navigate = useNavigate()

    const openLeagueUnit = (leagueUnitName: string) => {
        getLeagueUnitIdByName(props.levelProps!.leagueId(), leagueUnitName, id => {
            navigate('/leagueUnit/' + id)
            //workaround :( Can't refresh
            setTimeout(() => {window.location.reload()}, 100)
        } )
    }


    const prevLeagueUnitName = (props.levelProps === undefined) ? undefined : previousLeagueUnit(props.levelProps.leagueUnitName())
    const nextLeagueUnitName = (props.levelProps === undefined) ? undefined : nextLeagueUnit(props.levelProps.leagueUnitName())

    const prevLeagueUnitLink = (prevLeagueUnitName === undefined) ? undefined :
        <svg onClick={() => {openLeagueUnit(prevLeagueUnitName!)}} xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="neighboor_league_unit_link bi bi-arrow-left-square" viewBox="0 0 16 16">
          <path fillRule="evenodd" d="M15 2a1 1 0 0 0-1-1H2a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V2zM0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V2zm11.5 5.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z"></path>
    </svg>

    const nextLeagueUnitLink = (nextLeagueUnitName === undefined) ? undefined :
        <svg onClick={() => openLeagueUnit(nextLeagueUnitName!)} xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="neighboor_league_unit_link me-1 bi bi-arrow-right-square" viewBox="0 0 16 16">
          <path fillRule="evenodd" d="M15 2a1 1 0 0 0-1-1H2a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V2zM0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V2zm4.5 5.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5H4.5z"></path>
    </svg>

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
                beforeLink: prevLeagueUnitLink,
                afterLink: nextLeagueUnitLink,
                content: props.levelProps?.leagueUnitName()
            }
        ]

    const externalLink = <ExternalLeagueUnitLink id={props.levelProps?.leagueUnitId() || 0} black={false} />

    const selectBox = <Form>
            <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" onChange={e => navigate('/team/' + e.currentTarget.value)}>
                <option value={undefined}>Select...</option>
                {props.levelProps?.teams().map(([teamId, teamName]) => {
                    return <option key={'league_unit_top_option_' + teamId} value={teamId}>{teamName}</option>
                })}
            </Form.Select>
        </Form>

    return <TopMenu
            levelProps={props.levelProps}
            links={links}
            externalLink={externalLink}
            selectBox={selectBox}
            sectionLinks={[]}
        />
}

export default LeagueUnitTopMenu
