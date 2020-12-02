import React from 'react'
import WorldData from '../rest/models/leveldata/WorldData'
import '../common/menu/LeftMenu.css'
import '../i18n'
import { Translation } from 'react-i18next'
import { Line } from 'rc-progress';
import moment from 'moment'
import LeagueLink from '../common/links/LeagueLink'
import i18n from '../i18n'

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
            currentProcess = <span><LeagueLink text={worldData.loadingInfo.nextCountry[1]} id={worldData.loadingInfo.nextCountry[0]} />{i18n.t('world.scheduled')}<br/>
                {moment(worldData.loadingInfo.nextCountry[2]).format('DD.MM HH:mm:ss')} HT</span>
        }

        return <Translation>{
            (t, { i18n }) =>  <div className="left_side_inner">
                <div className="left_bar">
                    <header className="left_bar_header">{t('world.countries')}</header>
                    <section className="left_bar_season_info">
                        <span>{worldData.loadingInfo.proceedCountries} / {worldData.countries.length} {t('world.loaded')}</span>
                        <Line percent={percent * 100} strokeWidth={8} strokeColor="green" />
                        {currentProcess}
                    </section>
                </div>                
            </div>
        }
        </Translation>
    }
}

export default WorldLeftLoadingMenu