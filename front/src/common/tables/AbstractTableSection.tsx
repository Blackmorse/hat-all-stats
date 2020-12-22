import React from 'react';
import './TableSection.css'
import '../sections//StatisticsSection.css'
import StatisticsParameters, { SortingDirection, StatsTypeEnum, StatsType } from '../../rest/models/StatisticsParameters'
import RestTableData from '../../rest/models/RestTableData'
import PageNavigator from '../elements/PageNavigator'
import Cookies from 'js-cookie'
import PageSizeSelector from '../selectors/PageSizeSelector'
import StatsTypeSelector from '../selectors/StatsTypeSelector'
import SeasonSelector from '../selectors/SeasonSelector'
import PlayedAllMatchesSelector from '../selectors/PlayedAllMatchesSelector'
import PositionSelector from '../selectors/PositionSelector'
import NationalitySelector from '../selectors/NationalitySelector'
import AgeSelector from '../selectors/AgeSelector'
import LevelData from '../../rest/models/leveldata/LevelData';
import StatisticsSection from '../sections/StatisticsSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../LevelDataProps'
import { LoadingEnum } from '../enums/LoadingEnum';
import { SelectorsEnum } from './SelectorsEnum'
import PlayersParameters from '../../rest/models/PlayersParameters'

interface ModelTableState<T> {
    entities?: Array<T>,
    isLastPage: boolean,
    selectedRow?: number
}

export interface SortingState {
    callback: (sortingField: string) => void,
    currentSorting: string,
    sortingDirection: SortingDirection
}

export interface DataRequest {
    statisticsParameters: StatisticsParameters,
    playedAllMatches: boolean,
    playersParameters: PlayersParameters
}


abstract class AbstractTableSection<Data extends LevelData, TableProps extends LevelDataProps<Data>, Model> 
        extends StatisticsSection<LevelDataPropsWrapper<Data, TableProps>, ModelTableState<Model>, RestTableData<Model>, DataRequest> {
    private statsTypes: Array<StatsTypeEnum>
    private selectors: Array<SelectorsEnum>
    private fistOpening: boolean = true

    constructor(props: LevelDataPropsWrapper<Data, TableProps>, 
            defaultSortingField: string, defaultStatsType: StatsType,
            statsTypes: Array<StatsTypeEnum>,
            selectors: Array<SelectorsEnum>) {
        super(props, '')
        this.statsTypes = statsTypes
        this.selectors = selectors
        
        let queryParams = props.queryParams
        let sortingField = queryParams.sortingField
        if (!sortingField) {
            sortingField = defaultSortingField
        }

        let pageSizeString = Cookies.get('hattid_page_size')
        let pageSize = (pageSizeString == null) ? 16 : Number(pageSizeString)

        let selectedRow = queryParams.selectedRow
        let page = (selectedRow) ? Math.floor(Number(selectedRow)/ pageSize) : 0

        let statsType = defaultStatsType
        let round = queryParams.round
        if (round) {
            statsType = {statType: StatsTypeEnum.ROUND, roundNumber: round}
        }

        let seasonQp = queryParams.season
        let season = (seasonQp) ? seasonQp : this.props.levelDataProps.currentSeason()

        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {
                statisticsParameters: {
                    page: page,
                    pageSize: pageSize,
                    sortingField: sortingField,
                    sortingDirection: SortingDirection.DESC,
                    statsType: statsType,
                    season: season
                },
                playedAllMatches: true,
                playersParameters: {
                }
            },
            state: {
                isLastPage: true,
                selectedRow: (selectedRow === null) ? undefined: selectedRow
            }
        }

        this.pageSizeChanged=this.pageSizeChanged.bind(this);
        this.sortingChanged=this.sortingChanged.bind(this);
        this.statTypeChanged=this.statTypeChanged.bind(this);
        this.seasonChanged=this.seasonChanged.bind(this);
        this.playedAllMatchesChanged=this.playedAllMatchesChanged.bind(this);
        this.roleChanged=this.roleChanged.bind(this)
        this.nationalityChanged=this.nationalityChanged.bind(this)
        this.minMaxAgeChanged=this.minMaxAgeChanged.bind(this)
    }

    createColumnHeaders(): JSX.Element {
        const sortingState = {
            callback: this.sortingChanged,
            currentSorting: this.state.dataRequest.statisticsParameters.sortingField,
            sortingDirection: this.state.dataRequest.statisticsParameters.sortingDirection
        }
        return this.columnHeaders(sortingState)
    }

    abstract columnHeaders(sortingState: SortingState): JSX.Element

    abstract columnValues(index: number, model: Model): JSX.Element

    stateFromResult(result?: RestTableData<Model>): ModelTableState<Model> {
        return {
            entities: (result) ? result.entities : this.state.state.entities,
            isLastPage: (result) ? result.isLastPage : this.state.state.isLastPage,
            selectedRow: (result && this.fistOpening) ? this.state.state.selectedRow : undefined
        }
    }

    pageSelected(pageNumber: number) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newStatisticsParameters = Object.assign({}, this.state.dataRequest.statisticsParameters)
        newStatisticsParameters.page = pageNumber

        newDataRequest.statisticsParameters = newStatisticsParameters

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    pageSizeChanged(pageSize: number) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newStatisticsParameters = Object.assign({}, this.state.dataRequest.statisticsParameters)
        newStatisticsParameters.pageSize = pageSize
        newStatisticsParameters.page = 0

        newDataRequest.statisticsParameters = newStatisticsParameters

        this.fistOpening = false

        Cookies.set('hattid_page_size', pageSize.toString(), { sameSite: "Lax", expires: 180 })

        this.updateWithRequest(newDataRequest)
    }

    sortingChanged(sortingField: string) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newStatisticsParameters = Object.assign({}, this.state.dataRequest.statisticsParameters)
        
        let newSortingDirection: SortingDirection
        if( this.state.dataRequest.statisticsParameters.sortingField === sortingField ) {
            if ( this.state.dataRequest.statisticsParameters.sortingDirection === SortingDirection.DESC ) {
                newSortingDirection = SortingDirection.ASC
            } else {
                newSortingDirection = SortingDirection.DESC
            }
        } else {
            newSortingDirection = SortingDirection.DESC
        }

        newStatisticsParameters.sortingField = sortingField
        newStatisticsParameters.sortingDirection = newSortingDirection

        newDataRequest.statisticsParameters = newStatisticsParameters

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    statTypeChanged(statType: StatsType) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newStatisticsParameters = Object.assign({}, this.state.dataRequest.statisticsParameters)
        newStatisticsParameters.statsType = statType

        newDataRequest.statisticsParameters = newStatisticsParameters

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    seasonChanged(season: number) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newStatisticsParameters = Object.assign({}, this.state.dataRequest.statisticsParameters)
        newStatisticsParameters.season = season

        newDataRequest.statisticsParameters = newStatisticsParameters

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    playedAllMatchesChanged(playedAllMatches: boolean) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        newDataRequest.playedAllMatches = playedAllMatches

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    roleChanged(role?: string) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newPlayersParameters = Object.assign({}, this.state.dataRequest.playersParameters)
        newPlayersParameters.role = role

        newDataRequest.playersParameters = newPlayersParameters

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    nationalityChanged(nationality?: number) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newPlayersParameters = Object.assign({}, this.state.dataRequest.playersParameters)
        newPlayersParameters.nationality = nationality

        newDataRequest.playersParameters = newPlayersParameters

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    minMaxAgeChanged(minMax: [number?, number?]) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        let newPlayersParameters = Object.assign({}, this.state.dataRequest.playersParameters)
        newPlayersParameters.minAge = minMax[0]
        newPlayersParameters.maxAge = minMax[1]

        newDataRequest.playersParameters = newPlayersParameters

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    renderSection(): JSX.Element {
        let seasonSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.SEASON_SELECTOR) !== -1) {
            seasonSelector = <SeasonSelector currentSeason={this.state.dataRequest.statisticsParameters.season}
                seasonOffset={this.props.levelDataProps.levelData.seasonOffset}
                seasons={this.props.levelDataProps.seasons()}
                callback={this.seasonChanged}/>
        }

        let statsTypeSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.STATS_TYPE_SELECTOR) !== -1) {
            statsTypeSelector = <StatsTypeSelector  statsTypes={this.statsTypes}
                rounds={this.props.levelDataProps.rounds(this.state.dataRequest.statisticsParameters.season)}
                selectedStatType={this.state.dataRequest.statisticsParameters.statsType}
                onChanged={this.statTypeChanged}
                />
        }

        let pageSizeSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.PAGE_SIZE_SELECTOR) !== -1) {
            pageSizeSelector = <PageSizeSelector 
                selectedSize={this.state.dataRequest.statisticsParameters.pageSize}
                linkAction={this.pageSizeChanged}/>
        }

        let navigatorProps = {
            pageNumber: this.state.dataRequest.statisticsParameters.page,
            isLastPage: this.state.state.isLastPage
        }
        
        let pageSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.PAGE_SELECTOR) !== -1) {
            pageSelector = <PageNavigator 
                pageNumber={navigatorProps.pageNumber} 
                isLastPage={navigatorProps.isLastPage} 
                linkAction={(pageNumber) => this.pageSelected(pageNumber)}/> 
        }

        let playedAllMatchesSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.PLAYED_ALL_MATCHES_SELECTOR) !== -1) {
            playedAllMatchesSelector = <PlayedAllMatchesSelector 
                value={this.state.dataRequest.playedAllMatches}
                callback={this.playedAllMatchesChanged}
                />
        }

        let playerPositionsSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.PLAYER_ROLES) !== -1) {
            playerPositionsSelector = <PositionSelector 
                value={this.state.dataRequest.playersParameters.role}
                callback={this.roleChanged}/>
        }

        let nationalitySelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.NATIONALITIES_SELECTOR) !== -1) {
            nationalitySelector = <NationalitySelector 
                value={this.state.dataRequest.playersParameters.nationality}
                countryMap={this.props.levelDataProps.countriesMap()}
                callback={this.nationalityChanged} />
        }

        let ageSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.AGE_SELECTOR) !== -1) {
            ageSelector = <AgeSelector callback={this.minMaxAgeChanged}/>
        }

        let indexOffset = this.state.dataRequest.statisticsParameters.pageSize * this.state.dataRequest.statisticsParameters.page 
        return <>
                <div className="table_settings_div">
                    {seasonSelector}
                    {statsTypeSelector}
                    {playedAllMatchesSelector}
                    {pageSizeSelector}
                </div>
                <div className="players_settings_div">
                    {playerPositionsSelector}
                    {nationalitySelector}
                    {ageSelector}
                </div>
                <table className="statistics_table">
                    <thead>
                        {this.createColumnHeaders()}
                    </thead>
                    <tbody>
                        {this.state.state.entities?.map((entity, index) => 
                            <tr key={this.constructor.name + '_' + index} 
                                className={((this.state.state.selectedRow !== undefined) && this.state.state.selectedRow === indexOffset + index) ? "selected_row" : ""}>
                                {this.columnValues(indexOffset + index, entity)}
                            </tr>
                            )}
                    </tbody>
                </table>

                {pageSelector}
                </>
    }
}

export default AbstractTableSection;