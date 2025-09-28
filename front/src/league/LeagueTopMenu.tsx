import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu'
import { toArabian } from "../common/Utils"
import ExternalLeagueLink from '../common/links/ExternalLeagueLink';
import { Form } from 'react-bootstrap';
import {useNavigate} from 'react-router';
import LeagueLevelDataProps from './LeagueLevelDataProps';

const LeagueTopMenu = (props: {levelProps?: LeagueLevelDataProps}) => {
    const navigate = useNavigate()

    const links = [
        {
            href: "/league/" + props.levelProps?.leagueId(),
            content: props.levelProps?.leagueName()
        }
    ]

    const externalLink = <ExternalLeagueLink id={props.levelProps?.leagueId() || 1000} black={false} />

    const selectBox = <Form>
        <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" 
             onChange={e => navigate('/league/' + props.levelProps?.leagueId() + '/divisionLevel/' + toArabian(e.currentTarget.value))}>
            <option value={undefined}>Select...</option>
            {props.levelProps?.divisionLevels().map(divisionLevel => {
               return <option key={'division_level_select_' + divisionLevel} value={divisionLevel}>{divisionLevel}</option>}
           )}
        </Form.Select>
      </Form>

    return <TopMenu
            levelProps={props.levelProps}
            selectBox={selectBox}
            externalLink={externalLink}
            links={links}
            sectionLinks={[]}
        />
}

export default LeagueTopMenu
