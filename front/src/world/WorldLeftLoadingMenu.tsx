import React, { Fragment } from 'react'
import WorldData from '../rest/models/leveldata/WorldData'
import '../common/menu/LeftMenu.css'
import '../i18n'
import { Translation } from 'react-i18next'
import { Line } from 'rc-progress';
import moment from 'moment'
import LeagueLink from '../common/links/LeagueLink'
import i18n from '../i18n'
import { Card, Container, ProgressBar } from 'react-bootstrap'

interface Props {
    worldData?: WorldData
}

class WorldLeftLoadingMenu extends React.Component<Props, {}> {
    render() {
        if(!this.props.worldData || !this.props.worldData.loadingInfo) {
            return <></>
        }
        let worldData = this.props.worldData
        let percent = worldData.loadingInfo.proceedCountries / worldData.countries.length

        let currentProcess: JSX.Element
        if(worldData.loadingInfo.currentCountry) {
        currentProcess = <span><LeagueLink text={worldData.loadingInfo.currentCountry[1]} id={worldData.loadingInfo.currentCountry[0]} />{i18n.t('world.loading')}</span>
        } else if (worldData.loadingInfo.nextCountry) {
            currentProcess = <span className="h6">
                    <LeagueLink className="link-dark p-0" text={worldData.loadingInfo.nextCountry[1]} id={worldData.loadingInfo.nextCountry[0]} />{i18n.t('world.scheduled')}<br/>
                    {moment(worldData.loadingInfo.nextCountry[2]).format('DD.MM HH:mm:ss')} HT
                </span>
        }

        return <Translation>{
            (t, { i18n }) =>  
            <Card className="shadow">
                {/* TODO bootstrap translate progress */}
                <Card.Header className='lead'>Progress</Card.Header>
                <Card.Body>
                    {worldData.loadingInfo.proceedCountries} / {worldData.countries.length} {t('world.loaded')}
                    <ProgressBar now={percent * 100}/> 
                    {currentProcess}
                </Card.Body>
            </Card>


        }
        </Translation>
    }
}

export default WorldLeftLoadingMenu