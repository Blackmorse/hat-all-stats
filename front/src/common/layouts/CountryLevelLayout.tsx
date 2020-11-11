import Layout from './Layout'
import { PagesEnum } from '../enums/PagesEnum'
import LevelDataProps from '../LevelDataProps'
import React from 'react'
import LevelData from '../../rest/models/leveldata/LevelData'
import { Translation } from 'react-i18next'
import LeftMenu from '../../common/menu/LeftMenu'
import '../../i18n'
import './CountryLevelLayout.css'
import Mappings from '../enums/Mappings'

export interface CountryLevelLayoutState<Data extends LevelData> {
    leaguePage: PagesEnum,
    levelData?: Data
}

abstract class CountryLevelLayout<Props, Data extends LevelData, TableProps extends LevelDataProps<Data>> extends Layout<Props, CountryLevelLayoutState<Data>> {
    pagesMap = new Map<PagesEnum, (props: TableProps) => JSX.Element>()

    constructor(props: Props,
        pagesMap: Map<PagesEnum, (props:  TableProps) => JSX.Element>) {
        super(props)
        this.pagesMap = pagesMap

        let params = new URLSearchParams(window.location.search);        
        let pageString = params.get(Mappings.PAGE)
        if (pageString === null) {
            this.state = {leaguePage: Array.from(pagesMap)[0][0]}
        } else {
            let page = Mappings.queryParamToPageMap.getValue(pageString)
            if (page) {
                this.state = {leaguePage: page}
            } else {
                this.state = {leaguePage: Array.from(pagesMap)[0][0]}
            }
        }       
    }

    abstract makeModelProps(levelData: Data): TableProps

    abstract fetchLevelData(props: Props, callback: (data: Data) => void): void

    componentDidMount() {
        const oldState = this.state
        this.fetchLevelData(this.props, data => {
            this.setState({
                leaguePage: oldState.leaguePage,
                levelData: data
            })
        })
    }

    leftMenu(): JSX.Element {
        let promotionsMenu: JSX.Element
        if(this.state.levelData && this.makeModelProps(this.state.levelData).currentRound() >= 14) {
            promotionsMenu = <LeftMenu pages={[PagesEnum.PROMOTIONS]}
                callback={leaguePage => {this.setState({leaguePage: leaguePage})}} 
                title='menu.promotions' />
        } else {
            promotionsMenu = <></>
        }

        return <>
                {promotionsMenu}
                <LeftMenu pages={Array.from(this.pagesMap.keys()).filter(p => p !== PagesEnum.PROMOTIONS)} 
                    callback={leaguePage =>{this.setState({leaguePage: leaguePage})}}
                    title='menu.statistics'/>
            </>
    }

    content() {
        let res: JSX.Element
        let jsxFunction = this.pagesMap.get(this.state.leaguePage)
        if (this.state.levelData && jsxFunction) {
            res = jsxFunction(this.makeModelProps(this.state.levelData))
        } else {
            res = <></>
        }
        return <Translation>{
            (t, { i18n }) => <>
                <header className="content_header">{t(this.state.leaguePage)}</header>
                <div className="content_body">
                    {res}
                </div>
            </>
            }
            </Translation>
    }
}

export default CountryLevelLayout