import React from 'react'
import DivisionLevelData from '../rest/models/leveldata/DivisionLevelData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import { Form } from 'react-bootstrap';
import {useNavigate} from 'react-router';
import {getLeagueUnitIdByName} from '../rest/Client';

const DivisionLevelTopMenu = (props: {data?: DivisionLevelData}) => {
    let navigate = useNavigate()

    let links = [
        {
            href: "/league/" + props.data?.leagueId, 
            content: props.data?.leagueName
        },
        {
            href: "/league/" + props.data?.leagueId + "/divisionLevel/" + props.data?.divisionLevel, 
            content: props.data?.divisionLevelName
        }
      ]

    let onChanged = (e: React.FormEvent<HTMLSelectElement>) => { 
        getLeagueUnitIdByName(Number(props.data?.leagueId), props.data?.divisionLevelName + '.' + e.currentTarget.value, id => {
            navigate('/leagueUnit/' + id)
        })
        
    }

    let selectBox = <Form>
        <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" 
            onChange={onChanged}>
                <option value={undefined}>Select...</option>
                {Array.from(Array(props.data?.leagueUnitsNumber), (_, i) => i + 1).map(leagueUnit => {
                    return <option key={'division_leve_top_menu_' + leagueUnit} value={leagueUnit}>{leagueUnit}</option>
                })}
            </Form.Select>
          </Form>

    return <TopMenu
            data={props.data}
            selectBox={selectBox}
            links={links}
            sectionLinks={[]}
        />
}

export default DivisionLevelTopMenu
