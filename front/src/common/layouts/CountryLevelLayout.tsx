import { PagesEnum } from '../enums/PagesEnum'
import LevelDataProps from '../LevelDataProps'
import React from 'react'
import CountryLevelData from '../../rest/models/leveldata/CountryLevelData'
import LeftMenu from '../../common/menu/LeftMenu'
import '../../i18n'
import './CountryLevelLayout.css'
import CurrentCountryInfoMenu from '../menu/CurrentCountryInfoMenu'
import LevelLayout, {BaseLevelLayoutProps} from './LevelLayout'

interface Props<Data extends CountryLevelData, TableProps extends LevelDataProps<Data>> extends BaseLevelLayoutProps<Data, TableProps> {}


const CountryLevelLayout = <Data extends CountryLevelData, TableProps extends LevelDataProps<Data>>(props: Props<Data, TableProps>) => {

    function topLeftMenu(levelData: Data, onPageChange: (page: PagesEnum) => void): JSX.Element {
        let promotionsMenu: JSX.Element = <></>
        let currentCountryInfoMenu = <></>
        if(levelData ) {
            let modelProps = props.makeModelProps(levelData)
            currentCountryInfoMenu = <CurrentCountryInfoMenu levelDataProps={modelProps}/>
            if(modelProps.currentRound() >= 14) {
                promotionsMenu = <LeftMenu pages={[PagesEnum.PROMOTIONS]}
                    callback={onPageChange} 
                    title='menu.promotions' />
            }
        } 
        return <>
                {currentCountryInfoMenu}
                {promotionsMenu}
            </>
    }

    return <LevelLayout
            topLeftMenu={topLeftMenu}
            {...props} 
        />
}

export default CountryLevelLayout
