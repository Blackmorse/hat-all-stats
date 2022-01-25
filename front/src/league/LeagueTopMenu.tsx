import React from 'react';
import LeagueData from '../rest/models/leveldata/LeagueData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu'
import { toArabian } from "../common/Utils"
import ExternalLeagueLink from '../common/links/ExternalLeagueLink';
import { Form } from 'react-bootstrap';
import {useNavigate} from 'react-router';

const LeagueTopMenu = (props: {data?: LeagueData}) => {
    let navigate = useNavigate()

    let links = [
        {
            href: "/league/" + props.data?.leagueId, 
            content: props.data?.leagueName
        }
    ]

    let externalLink = <ExternalLeagueLink id={props.data?.leagueId || 1000} black={false} />

    let selectBox = <Form>
        <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" 
             onChange={e => navigate('/league/' + props.data?.leagueId + '/divisionLevel/' + toArabian(e.currentTarget.value))}>
            <option value={undefined}>Select...</option>
            {props.data?.divisionLevels.map(divisionLevel => {
               return <option key={'division_level_select_' + divisionLevel} value={divisionLevel}>{divisionLevel}</option>}
           )}
        </Form.Select>
      </Form>

    return <TopMenu
            data={props.data}
            selectBox={selectBox}
            externalLink={externalLink}
            links={links}
            sectionLinks={[]}
        />
}

export default LeagueTopMenu
