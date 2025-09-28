import React from 'react'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import { Form } from 'react-bootstrap';
import {useNavigate} from 'react-router';
import {getLeagueUnitIdByName} from '../rest/Client';
import DivisionLevelDataProps from './DivisionLevelDataProps';

const DivisionLevelTopMenu = (props: {levelProps?: DivisionLevelDataProps}) => {
    const navigate = useNavigate()

    const links = [
        {
            href: "/league/" + props.levelProps?.leagueId(),
            content: props.levelProps?.leagueName()
        },
        {
            href: "/league/" + props.levelProps?.leagueId() + "/divisionLevel/" + props.levelProps?.divisionLevel(), 
            content: props.levelProps?.divisionLevelName()
        }
      ]

    const onChanged = (e: React.FormEvent<HTMLSelectElement>) => { 
        getLeagueUnitIdByName(Number(props.levelProps?.leagueId()), props.levelProps?.divisionLevelName() + '.' + e.currentTarget.value, id => {
            navigate('/leagueUnit/' + id)
        })
        
    }

    const selectBox = <Form>
        <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" 
            onChange={onChanged}>
                <option value={undefined}>Select...</option>
                {Array.from(Array(props.levelProps?.leagueUnitsNumber()), (_, i) => i + 1).map(leagueUnit => {
                    return <option key={'division_leve_top_menu_' + leagueUnit} value={leagueUnit}>{leagueUnit}</option>
                })}
            </Form.Select>
          </Form>

    return <TopMenu
            levelProps={props.levelProps}
            selectBox={selectBox}
            links={links}
            sectionLinks={[]}
        />
}

export default DivisionLevelTopMenu
