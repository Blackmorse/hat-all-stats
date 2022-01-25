import React from 'react'
import TopMenu from '../common/menu/TopMenu'
import WorldData from '../rest/models/leveldata/WorldData'
import { Form } from 'react-bootstrap'
import {useNavigate} from 'react-router'
import {useTranslation} from 'react-i18next'

const WorldTopMenu = (props: {data?: WorldData}) => {
    const t = useTranslation().t
    let navigate = useNavigate()

    let sectionLinks = [
        {href: '/about', text: t('menu.about')},
        {href: '/worldOverview', text: t('overview.world_overview')}
    ]

    let selectBox = <Form>
            <Form.Select  size="sm" className="my-1 pr-3 me-md-5" max-width="100" 
                    onChange={(e: React.FormEvent<HTMLSelectElement>) => navigate('/league/' + Number(e.currentTarget.value))}>
            <option value={undefined}>Select...</option>
            {props.data?.countries.map(countryInfo => {
                return <option value={countryInfo[0]} key={'league_select_' + countryInfo[0]}>
                    {countryInfo[1]}
                </option>
            })}
        </Form.Select>
    </Form> 


    return <TopMenu 
        selectBox={selectBox}
        data={props.data}
        links={[]}
        sectionLinks={sectionLinks}
    />
}

export default WorldTopMenu

