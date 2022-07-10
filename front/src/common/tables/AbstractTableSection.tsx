import React, { Fragment } from 'react';
import './TableSection.css'
import StatisticsParameters, { SortingDirection, StatsTypeEnum, StatsType } from '../../rest/models/StatisticsParameters'
import RestTableData from '../../rest/models/RestTableData'
import PageNavigator from '../elements/PageNavigator'
import Cookies from 'js-cookie'
import PageSizeSelector from '../selectors/PageSizeSelector'
import StatsTypeSelector from '../selectors/StatsTypeSelector'
import SeasonSelector from '../selectors/SeasonSelector'
import CheckBoxSelector from '../selectors/CheckBoxSelector'
import PositionSelector from '../selectors/PositionSelector'
import NationalitySelector from '../selectors/NationalitySelector'
import AgeSelector from '../selectors/AgeSelector'
import LevelDataProps, { LevelDataPropsWrapper } from '../LevelDataProps'
import { LoadingEnum } from '../enums/LoadingEnum';
import { SelectorsEnum } from './SelectorsEnum'
import PlayersParameters from '../../rest/models/PlayersParameters'
import ExecutableComponent from '../sections/ExecutableComponent';
import { SectionState } from '../sections/Section';
import { Col, Container, Row } from 'react-bootstrap';

interface ModelTableState<ResponseModel> {
    model?: ResponseModel,
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
    playersParameters: PlayersParameters,
    playedInLastMatch: boolean
}


abstract class AbstractTableSection<TableProps extends LevelDataProps, RowModel, ResponseModel> 
        extends ExecutableComponent<LevelDataPropsWrapper<TableProps>, ModelTableState<ResponseModel> & SectionState, ResponseModel, DataRequest> {
    private statsTypes: Array<StatsTypeEnum>
    private selectors: Array<SelectorsEnum>
    private fistOpening: boolean = true

    constructor(props: LevelDataPropsWrapper<TableProps>, 
            defaultSortingField: string, 
            defaultStatsType: StatsType,
            statsTypes: Array<StatsTypeEnum>,
            selectors: Array<SelectorsEnum>) {
        super(props)
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
                },
                playedInLastMatch: false
            },
            selectedRow: (selectedRow === null) ? undefined: selectedRow,
            collapsed: false
        }

        this.pageSizeChanged=this.pageSizeChanged.bind(this);
        this.sortingChanged=this.sortingChanged.bind(this);
        this.statTypeChanged=this.statTypeChanged.bind(this);
        this.seasonChanged=this.seasonChanged.bind(this);
        this.playedAllMatchesChanged=this.playedAllMatchesChanged.bind(this);
        this.roleChanged=this.roleChanged.bind(this)
        this.nationalityChanged=this.nationalityChanged.bind(this)
        this.minMaxAgeChanged=this.minMaxAgeChanged.bind(this)
        this.playedInLastMatchChanged=this.playedInLastMatchChanged.bind(this)
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

    abstract row(index: number, className: string, model: RowModel): JSX.Element

    abstract responseModelToRowModel(responseModel?: ResponseModel): RestTableData<RowModel>

    stateFromResult(result?: ResponseModel): ModelTableState<ResponseModel> & SectionState {
        return {
            model: result,
            selectedRow: (result && this.fistOpening) ? this.state.selectedRow : undefined,
            collapsed: this.state.collapsed
        }
    }


    private mutateStatisticsParameters(mutate: any): DataRequest {
        return {
            ...this.state.dataRequest,
            statisticsParameters: {
                ...this.state.dataRequest.statisticsParameters,
                ...mutate
            }
        }
    }

    private mutatePlayerParameters(mutate: any): DataRequest {
        return {
            ...this.state.dataRequest,
            playersParameters: {
                ...this.state.dataRequest.playersParameters,
                ...mutate
            }
        }
    }

    pageSelected(pageNumber: number) {
        this.fistOpening = false

        let newDataRequest = this.mutateStatisticsParameters({page: pageNumber})
        this.updateWithRequest(newDataRequest)
    }

    pageSizeChanged(pageSize: number) {
        let newDataRequest = this.mutateStatisticsParameters({
            pageSize: pageSize,
            page: 0
        })
        this.fistOpening = false

        Cookies.set('hattid_page_size', pageSize.toString(), { sameSite: "Lax", expires: 180 })

        this.updateWithRequest(newDataRequest)
    }

    sortingChanged(sortingField: string) {
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

        let newDataRequest = this.mutateStatisticsParameters({
            sortingField: sortingField,
            sortingDirection: newSortingDirection
        })

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    statTypeChanged(statType: StatsType) {
        let newDataRequest = this.mutateStatisticsParameters({
            statsType: statType
        })

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    seasonChanged(season: number) {
        let newDataRequest = this.mutateStatisticsParameters({
            season: season
        })

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    playedAllMatchesChanged(playedAllMatches: boolean) {
        let newDataRequest = this.mutateStatisticsParameters({
            playedAllMatches: playedAllMatches
        })

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    roleChanged(role?: string) {
        let newDataRequest = this.mutatePlayerParameters({
            role: role
        })

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    nationalityChanged(nationality?: number) {
        let newDataRequest = this.mutatePlayerParameters({
            nationality: nationality
        })
        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    minMaxAgeChanged(minMax: [number?, number?]) {
        let newDataRequest = this.mutatePlayerParameters({
            minAge: minMax[0],
            maxAge: minMax[1]
        })
        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    playedInLastMatchChanged(playedInLastMatch: boolean) {
        let newDataRequest = Object.assign({}, this.state.dataRequest)

        newDataRequest.playedInLastMatch = playedInLastMatch

        this.fistOpening = false

        this.updateWithRequest(newDataRequest)
    }

    additionalSection(_model?: ResponseModel): JSX.Element {
        return <></>
    }

    renderSection(): JSX.Element {
        let restTableData = this.responseModelToRowModel(this.state.model)
        let seasonSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.SEASON_SELECTOR) !== -1) {
            seasonSelector = <Col lg={3} md={6}>
                    <SeasonSelector currentSeason={this.state.dataRequest.statisticsParameters.season}
                    seasonOffset={this.props.levelDataProps.seasonOffset()}
                    seasons={this.props.levelDataProps.seasons()}
                    callback={this.seasonChanged}/>
                </Col>
        }

        let statsTypeSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.STATS_TYPE_SELECTOR) !== -1) {
            statsTypeSelector = <Col lg={3} md={6}>
                    <StatsTypeSelector statsTypes={this.statsTypes}
                        rounds={this.props.levelDataProps.rounds(this.state.dataRequest.statisticsParameters.season)}
                        selectedStatType={this.state.dataRequest.statisticsParameters.statsType}
                        onChanged={this.statTypeChanged}
                    />
                </Col>
        }

        let pageSizeSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.PAGE_SIZE_SELECTOR) !== -1) {
            pageSizeSelector = <Col lg={2} md={3} className='ms-auto'>
                <PageSizeSelector 
                    selectedSize={this.state.dataRequest.statisticsParameters.pageSize}
                    linkAction={this.pageSizeChanged}/>
                </Col>
        }

        let navigatorProps = {
            pageNumber: this.state.dataRequest.statisticsParameters.page,
            isLastPage: (restTableData === undefined) ? true : restTableData.isLastPage
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
            playedAllMatchesSelector = <Col lg={2} md={4}>
                <CheckBoxSelector 
                    value={this.state.dataRequest.playedAllMatches}
                    callback={this.playedAllMatchesChanged}
                    title='filter.full_season'
                    />
                </Col>
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

        let playedInLastMatchSelector = <></>
        if(this.selectors.indexOf(SelectorsEnum.PLAYED_IN_LAST_MATCH_SELECTOR) !== -1) {
            playedInLastMatchSelector = <Col lg={2} md={5}>
                <CheckBoxSelector
                    value={this.state.dataRequest.playedInLastMatch}
                    callback={this.playedInLastMatchChanged}
                    title='filter.played_in_last_match' />
                </Col>
        }

        let indexOffset = this.state.dataRequest.statisticsParameters.pageSize * this.state.dataRequest.statisticsParameters.page 
        return <Container className='table-responsive'>
                <Row>
                    {seasonSelector}
                    {statsTypeSelector}
                    {playedAllMatchesSelector}
                    {playedInLastMatchSelector}
                    {pageSizeSelector}
                </Row>
                <Row>
                    <Col lg={3} md={6} className='d-flex align-items-center'>
                        {playerPositionsSelector}
                    </Col>
                    <Col lg={3} md={6} className='d-flex align-items-center'>
                        {nationalitySelector}
                    </Col>
                    <Col lg={3} md={6} className='d-flex align-items-center'>
                        {ageSelector}
                    </Col>
                </Row>
                <table className="table table-striped table-rounded table-sm small">
                    <thead>
                        {this.createColumnHeaders()}
                    </thead>
                    <tbody>
                        {restTableData?.entities?.map((entity, index) => 
                            <Fragment key={this.constructor.name + '_' + index}>
                                {this.row(indexOffset + index, ((this.state.selectedRow !== undefined) && this.state.selectedRow === indexOffset + index) ? "selected_row" : "", entity)}
                            </Fragment>
                            )}
                    </tbody>
                </table>

                {pageSelector}
                {this.additionalSection(this.state.model)}
                </Container>
    }
}

export default AbstractTableSection;
