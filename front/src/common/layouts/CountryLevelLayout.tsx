import Layout from './Layout'
import { PagesEnum } from '../enums/PagesEnum'
import LevelDataProps from '../LevelDataProps'
import React from 'react'
import CountryLevelData from '../../rest/models/leveldata/CountryLevelData'
import { Translation } from 'react-i18next'
import LeftMenu from '../../common/menu/LeftMenu'
import '../../i18n'
import './CountryLevelLayout.css'
import Mappings from '../enums/Mappings'
import QueryParams from '../QueryParams'
import TeamSearchPage from '../pages/TeamSearchPage'
import CurrentCountryInfoMenu from '../menu/CurrentCountryInfoMenu'

export interface CountryLevelLayoutState<Data extends CountryLevelData> {
    leaguePage: PagesEnum,
    levelData?: Data,
    queryParams: QueryParams
}

abstract class CountryLevelLayout<Props, Data extends CountryLevelData, TableProps extends LevelDataProps<Data>> extends Layout<Props, CountryLevelLayoutState<Data>> {
    private firstTime: boolean = true
    
    pagesMap = new Map<PagesEnum, (props: TableProps, queryParams: QueryParams) => JSX.Element>()

    parseQueryParams: (paramss: URLSearchParams) =>  QueryParams = function(paramss: URLSearchParams) {

        let params = new URLSearchParams(window.location.search);

        let sortingFieldParams = params.get('sortingField')
        let sortingField: string | undefined = undefined
        if (sortingFieldParams !== null) {
            sortingField = sortingFieldParams
        }

        let selectedRowParams = params.get('row')
        let selectedRow: number | undefined = undefined
        if (selectedRowParams !== null) {
            selectedRow = Number(selectedRowParams)
        }
       
        let roundParams = params.get('round')
        let round: number | undefined = undefined
        if(roundParams !== null) {
            round = Number(roundParams)
        }

        let seasonParams = params.get('season')
        let season: number | undefined = undefined
        if(seasonParams !== null) {
            season = Number(seasonParams)
        }

        return {
            sortingField: sortingField,
            selectedRow: selectedRow,
            round: round,
            season: season
        }
    }

    constructor(props: Props,
        pagesMap: Map<PagesEnum, (props: TableProps, queryParams: QueryParams) => JSX.Element>) {
        super(props)
        pagesMap.set(PagesEnum.TEAM_SEARCH, (props, queryParams) => <TeamSearchPage />)
        this.pagesMap = pagesMap

        let params = new URLSearchParams(window.location.search);    
        let queryParams = this.parseQueryParams(params)
        
        let pageString = params.get('page')

        if (pageString === null) {
            this.state = {leaguePage: Array.from(pagesMap)[0][0], queryParams: queryParams}
        } else {
            let page = Mappings.queryParamToPageMap.getValue(pageString)
            if (page) {
                this.state = {leaguePage: page, queryParams: queryParams}
            } else {
                this.state = {leaguePage: Array.from(pagesMap)[0][0], queryParams: queryParams}
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

    content() {
        let res: JSX.Element
        let jsxFunction = this.pagesMap.get(this.state.leaguePage)
        if (this.state.levelData && jsxFunction) {
            let queryParams = (this.firstTime) ? this.state.queryParams : {}
            // let queryParams = this.state.queryParams
            console.log(queryParams)
            res = jsxFunction(this.makeModelProps(this.state.levelData), queryParams)
            this.firstTime = false
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