import { PagesEnum } from '../enums/PagesEnum'
import LevelDataProps from '../LevelDataProps'
import React from 'react'
import CountryLevelData from '../../rest/models/leveldata/CountryLevelData'
import LeftMenu from '../../common/menu/LeftMenu'
import '../../i18n'
import './CountryLevelLayout.css'
import QueryParams from '../QueryParams'
import CurrentCountryInfoMenu from '../menu/CurrentCountryInfoMenu'
import LevelLayout from './LevelLayout'

export interface CountryLevelLayoutState<Data extends CountryLevelData> {
    leaguePage: PagesEnum,
    levelData?: Data,
    queryParams: QueryParams,
    isError: boolean
}

abstract class CountryLevelLayout<Props, Data extends CountryLevelData, TableProps extends LevelDataProps<Data>> extends LevelLayout<Props, Data, TableProps> {
    leftMenu(): JSX.Element {
        let promotionsMenu: JSX.Element = <></>
        let currentCountryInfoMenu = <></>
        if(this.state.levelData ) {
            let modelProps = this.makeModelProps(this.state.levelData)
            currentCountryInfoMenu = <CurrentCountryInfoMenu levelDataProps={modelProps}/>
            if(modelProps.currentRound() >= 14) {
                promotionsMenu = <LeftMenu pages={[PagesEnum.PROMOTIONS]}
                    callback={leaguePage => {this.setState({leaguePage: leaguePage})}} 
                    title='menu.promotions' />
            }
        } 
        return <>
                {currentCountryInfoMenu}
                {promotionsMenu}
                <LeftMenu pages={Array.from(this.pagesMap.keys()).filter(p => (p !== PagesEnum.PROMOTIONS && p !== PagesEnum.TEAM_SEARCH))} 
                    callback={leaguePage =>{this.setState({leaguePage: leaguePage})}}
                    title='menu.statistics'/>
                <LeftMenu pages={[PagesEnum.TEAM_SEARCH]} 
                    callback={leaguePage =>{this.setState({leaguePage: leaguePage})}}
                    title='menu.team_search' />
            </>
    }
}

export default CountryLevelLayout