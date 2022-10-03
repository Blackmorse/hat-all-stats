import React from 'react'
import '../i18n'
import { Translation } from 'react-i18next'
import moment from 'moment'
import LeagueLink from '../common/links/LeagueLink'
import i18n from '../i18n'
import { Card, ProgressBar } from 'react-bootstrap'
import WorldLevelDataProps from './WorldLevelDataProps'

interface Props {
    worldLevelDataProps?: WorldLevelDataProps
}

const WorldLeftLoadingMenu = (props: Props) => {
    if(!props.worldLevelDataProps || !props.worldLevelDataProps.loadingInfo()) {
        return <></>
    }
    let worldData = props.worldLevelDataProps
    let percent = worldData.loadingInfo().proceedCountries / worldData.countries().length

    let currentProcess: JSX.Element
    if(worldData.loadingInfo().currentCountry) {
    currentProcess = <span><LeagueLink text={worldData.loadingInfo().currentCountry![1]} id={worldData.loadingInfo().currentCountry![0]} />{i18n.t('world.loading')}</span>
    } else if (worldData.loadingInfo().nextCountry) {
        currentProcess = <span className="h6">
                <LeagueLink className="left-menu-link link-dark p-0" text={worldData.loadingInfo().nextCountry![1]} id={worldData.loadingInfo().nextCountry![0]} />{i18n.t('world.scheduled')}<br/>
                {moment(worldData.loadingInfo().nextCountry![2]).format('DD.MM HH:mm:ss')} HT
            </span>
    }

    return <Translation>{
        t =>  
        <Card className="shadow">
            <Card.Header className='lead'>Progress</Card.Header>
            <Card.Body>
                {worldData.loadingInfo().proceedCountries} / {worldData.countries().length} {t('world.loaded')}
                <ProgressBar now={percent * 100}/> 
                {currentProcess}
            </Card.Body>
        </Card>


    }
    </Translation>
}

export default WorldLeftLoadingMenu
