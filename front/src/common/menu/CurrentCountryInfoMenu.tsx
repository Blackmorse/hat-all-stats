import React from 'react'
import './LeftMenu.css'
import CountryLevelData from '../../rest/models/leveldata/CountryLevelData'
import LevelDataProps from '../LevelDataProps'
import '../../i18n'
import { Translation } from 'react-i18next'
import moment from 'moment'
import i18n from '../../i18n'

interface Props<Data extends CountryLevelData> {
    levelDataProps: LevelDataProps<Data>
}

class CurrentCountryInfoMenu<Data extends CountryLevelData> extends React.Component<Props<Data>> {
    render() {
        let dateString: JSX.Element = <></>
        if(this.props.levelDataProps.levelData.loadingInfo.loadingInfo === "scheduled") {
            dateString = <>{i18n.t('league.next_round_scheduled')} <br/> {moment(this.props.levelDataProps.levelData.loadingInfo.date).format('DD.MM HH:mm:ss')} HT</>
        } else if(this.props.levelDataProps.levelData.loadingInfo.loadingInfo === "loading") {
        dateString = <>{i18n.t('league.next_round_loading')}</>
        }

        return <Translation>{
            (t, { i18n }) =>  <div className="left_side_inner">
                <div className="left_bar">
                    <header className="left_bar_header">
                        {this.props.levelDataProps.levelData.leagueName}
                    </header>
                    <section className="left_bar_links">
                        <span className="left_bar_season_info"> 
                            {t('filter.season')} {this.props.levelDataProps.offsettedSeason()}<br />
                            {t('filter.round')} {this.props.levelDataProps.currentRound()}
                        </span>
                        <span className="left_bar_season_info">
                            {dateString}
                        </span>
                    </section>
                </div>
            </div>
        }
        </Translation>
    }
}

export default CurrentCountryInfoMenu