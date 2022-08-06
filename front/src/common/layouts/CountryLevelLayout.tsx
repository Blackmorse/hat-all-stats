import { PagesEnum } from '../enums/PagesEnum'
import React from 'react'
import LeftMenu from '../../common/menu/LeftMenu'
import '../../i18n'
import './CountryLevelLayout.css'
import CurrentCountryInfoMenu from '../menu/CurrentCountryInfoMenu'
import LevelLayout, {BaseLevelLayoutProps} from './LevelLayout'
import CountryLevelDataProps from '../CountryLevelDataProps'

interface Props<LevelProps extends CountryLevelDataProps> extends BaseLevelLayoutProps<LevelProps> {}


const CountryLevelLayout = <LevelProps extends CountryLevelDataProps>(props: Props<LevelProps>) => {

    function topLeftMenu(levelProps: LevelProps, onPageChange: (page: PagesEnum) => void): JSX.Element {
        let promotionsMenu: JSX.Element = <></>
        let currentCountryInfoMenu = <></>
        currentCountryInfoMenu = <CurrentCountryInfoMenu levelDataProps={levelProps}/>
        if(levelProps.currentRound() >= 14) {
            promotionsMenu = <LeftMenu pages={[PagesEnum.PROMOTIONS]}
                callback={onPageChange} 
                title='menu.promotions' />
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
