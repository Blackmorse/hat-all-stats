import React from 'react';
import './TableSection.css'
import './StatisticsSection.css'
import StatisticsParameters, { SortingDirection, StatsTypeEnum, StatsType } from '../../rest/models/StatisticsParameters'
import RestTableData from '../../rest/models/RestTableData'
import PageNavigator from '../elements/PageNavigator'
import Cookies from 'js-cookie'
import PageSizeSelector from '../selectors/PageSizeSelector'
import StatsTypeSelector from '../selectors/StatsTypeSelector'
import SeasonSelector from '../selectors/SeasonSelector'
import LevelData from '../../rest/models/leveldata/LevelData';
import StatisticsSection from './StatisticsSection'
import LevelRequest from '../../rest/models/request/LevelRequest';
import LevelDataProps, { LevelDataPropsWrapper } from '../LevelDataProps'
import { LoadingEnum } from '../enums/LoadingEnum';

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

abstract class TableSection<Data extends LevelData, TableProps extends LevelDataProps<Data>, Model> 
        extends StatisticsSection<LevelDataPropsWrapper<Data, TableProps>, ModelTableState<Model>, RestTableData<Model>, StatisticsParameters> {
    private statsTypes: Array<StatsTypeEnum>
    private fistOpening: boolean = true

    constructor(props: LevelDataPropsWrapper<Data, TableProps>, 
            defaultSortingField: string, defaultStatsType: StatsType,
            statsTypes: Array<StatsTypeEnum>) {
        super(props, '')
        this.statsTypes = statsTypes
        
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

        this.state={
            loadingState: LoadingEnum.OK,
            dataRequest: {
                page: page,
                pageSize: pageSize,
                sortingField: sortingField,
                sortingDirection: SortingDirection.DESC,
                statsType: statsType,
                season: season
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
    }

    abstract fetchDataFunction(levelRequest: LevelRequest,
        statisticsParameters: StatisticsParameters,
        callback: (loadingEnum: LoadingEnum, restTableData?: RestTableData<Model>) => void): void

    createColumnHeaders(): JSX.Element {
        const sortingState = {
            callback: this.sortingChanged,
            currentSorting: this.state.dataRequest.sortingField,
            sortingDirection: this.state.dataRequest.sortingDirection
        }
        return this.columnHeaders(sortingState)
    }

    abstract columnHeaders(sortingState: SortingState): JSX.Element

    abstract columnValues(index: number, model: Model): JSX.Element

    executeDataRequest(dataRequest: StatisticsParameters, callback: (loadingState: LoadingEnum, result?: RestTableData<Model>) => void): void {
        const leveRequest = this.props.levelDataProps.createLevelRequest()
        this.fetchDataFunction(leveRequest, dataRequest, callback)
    }

    stateFromResult(result?: RestTableData<Model>): ModelTableState<Model> {
        return {
            entities: (result) ? result.entities : this.state.state.entities,
            isLastPage: (result) ? result.isLastPage : this.state.state.isLastPage,
            selectedRow: (result && this.fistOpening) ? this.state.state.selectedRow : undefined
        }
    }

    pageSelected(pageNumber: number) {
        let newStatisticsParameters = Object.assign({}, this.state.dataRequest)
        newStatisticsParameters.page = pageNumber

        this.fistOpening = false

        this.updateWithRequest(newStatisticsParameters)
    }

    pageSizeChanged(pageSize: number) {
        let newStatisticsParameters = Object.assign({}, this.state.dataRequest)
        newStatisticsParameters.pageSize = pageSize
        newStatisticsParameters.page = 0

        this.fistOpening = false

        Cookies.set('hattid_page_size', pageSize.toString(), { sameSite: "Lax", expires: 180 })

        this.updateWithRequest(newStatisticsParameters)
    }

    sortingChanged(sortingField: string) {
        let newStatisticsParameters = Object.assign({}, this.state.dataRequest)
        
        let newSortingDirection: SortingDirection
        if( this.state.dataRequest.sortingField === sortingField ) {
            if ( this.state.dataRequest.sortingDirection === SortingDirection.DESC ) {
                newSortingDirection = SortingDirection.ASC
            } else {
                newSortingDirection = SortingDirection.DESC
            }
        } else {
            newSortingDirection = SortingDirection.DESC
        }

        newStatisticsParameters.sortingField = sortingField
        newStatisticsParameters.sortingDirection = newSortingDirection

        this.fistOpening = false

        this.updateWithRequest(newStatisticsParameters)
    }

    statTypeChanged(statType: StatsType) {
        let newStatisticsParameters = Object.assign({}, this.state.dataRequest)
        newStatisticsParameters.statsType = statType

        this.fistOpening = false

        this.updateWithRequest(newStatisticsParameters)
    }

    seasonChanged(season: number) {
        let newStatisticsParameters = Object.assign({}, this.state.dataRequest)
        newStatisticsParameters.season = season

        this.fistOpening = false

        this.updateWithRequest(newStatisticsParameters)
    }

    renderSection(): JSX.Element {
        let navigatorProps = {
            pageNumber: this.state.dataRequest.page,
            isLastPage: this.state.state.isLastPage
        }
        let indexOffset = this.state.dataRequest.pageSize * this.state.dataRequest.page 
        return <>
                <div className="table_settings_div">
                    <SeasonSelector currentSeason={this.state.dataRequest.season}
                        seasonOffset={this.props.levelDataProps.levelData.seasonOffset}
                        seasons={this.props.levelDataProps.seasons()}
                        callback={this.seasonChanged}/>
                    <StatsTypeSelector  statsTypes={this.statsTypes}
                        rounds={this.props.levelDataProps.rounds(this.state.dataRequest.season)}
                        selectedStatType={this.state.dataRequest.statsType}
                        onChanged={this.statTypeChanged}
                        />

                    <PageSizeSelector 
                        selectedSize={this.state.dataRequest.pageSize}
                        linkAction={this.pageSizeChanged}/>
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

                <PageNavigator 
                    pageNumber={navigatorProps.pageNumber} 
                    isLastPage={navigatorProps.isLastPage} 
                    linkAction={(pageNumber) => this.pageSelected(pageNumber)}/> 
                </>
    }
}

export default TableSection;