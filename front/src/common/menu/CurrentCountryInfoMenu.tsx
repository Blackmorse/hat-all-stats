import React from 'react'
import './LeftMenu.css'
import CountryLevelData from '../../rest/models/leveldata/CountryLevelData'
import LevelDataProps from '../LevelDataProps'
import '../../i18n'
import { Translation } from 'react-i18next'

interface Props<Data extends CountryLevelData> {
    levelDataProps: LevelDataProps<Data>
}

class CurrentCountryInfoMenu<Data extends CountryLevelData> extends React.Component<Props<Data>> {
    render() {
        return <Translation>{
            (t, { i18n }) =>  <div className="left_side_inner">
                <div className="left_bar">
                    <header className="left_bar_header">
                        {this.props.levelDataProps.levelData.leagueName}
                    </header>
                    <section className="left_bar_links">
                        <span className="left_bar_links"> 
                            {t('filter.season')} {this.props.levelDataProps.offsettedSeason()}<br />
                            {t('filter.round')} {this.props.levelDataProps.currentRound()}
                        </span>
                    </section>
                </div>
            </div>
        }
        </Translation>
    }
}

export default CurrentCountryInfoMenu