import React, {useEffect, useState} from 'react'
import {Col, Container, Row} from 'react-bootstrap'
import RestTableData from '../../rest/models/RestTableData'
import StatisticsParameters, {SortingDirection, StatsType, StatsTypeEnum} from '../../rest/models/StatisticsParameters'
import LevelDataProps from '../LevelDataProps'
import Cookies from 'js-cookie'
import QueryParams from '../QueryParams'
import TableColumn from './TableColumn'
import SortingTableTh from '../elements/SortingTableTh'
import {LoadingEnum} from '../enums/LoadingEnum'
import Blur from '../widgets/Blur'
import {SelectorsEnum} from './SelectorsEnum'
import SeasonSelector from '../selectors/SeasonSelector'
import StatsTypeSelector from '../selectors/StatsTypeSelector'
import CheckBoxSelector from '../selectors/CheckBoxSelector'
import PageSizeSelector from '../selectors/PageSizeSelector'
import PageNavigator from '../elements/PageNavigator'
import PositionSelector from '../selectors/PositionSelector'
import NationalitySelector from '../selectors/NationalitySelector'
import AgeSelector from '../selectors/AgeSelector'
import PlayersParameters from '../../rest/models/PlayersParameters'

interface Request {
    statisticsParameters: StatisticsParameters
    playerParameters: PlayersParameters
    playedAllMatches: boolean
    playedInLastMatch: boolean
    oneTeamPerUnit: boolean
}

function initialDataRequest<LevelProps extends LevelDataProps, Model>(props: Properties<LevelProps, Model>): Request {
    let pageSizeString = Cookies.get('hattid_page_size')
    let pageSize = (pageSizeString == null) ? 16 : Number(pageSizeString)

    let selectedRow = props.queryParams.selectedRow
    let page = (selectedRow) ? Math.floor(Number(selectedRow)/ pageSize) : 0

    let sortingField = props.queryParams.sortingField
    if (!sortingField) {
        sortingField = props.defaultSortingField
    }

    let statsType = props.defaultStatsType
    let round = props.queryParams.round
    if (round) {
        statsType = {statType: StatsTypeEnum.ROUND, roundNumber: round}
    }

    let seasonQp = props.queryParams.season
    let season = (seasonQp) ? seasonQp : props.levelProps.currentSeason()

    return { 
        statisticsParameters: {
            page: page,
            pageSize: pageSize,
            sortingField: sortingField,
            sortingDirection: SortingDirection.DESC,
            statsType: statsType,
            season: season
        },
        playerParameters: {
        },
        playedInLastMatch: false,
        playedAllMatches: true,
        oneTeamPerUnit: true
    }
}

interface Properties<LevelProps extends LevelDataProps, Model> {
    levelProps: LevelProps,
    queryParams: QueryParams,
    requestFunc: (request: Request, callback: (loadingEnum: LoadingEnum, result?: RestTableData<Model>) => void) => void,
    defaultSortingField: string,
    defaultStatsType: StatsType,
    tableColumns: Array<TableColumn<Model>>,
    selectors: Array<SelectorsEnum>,
    statsTypes: Array<StatsTypeEnum>
}


const HookAbstractTableSection = <LevelProps extends LevelDataProps, Model>(props: Properties<LevelProps, Model>) => {
    let initDataRequest = initialDataRequest(props)
    const [ updateCounter, setUpdateCounter ] = useState(0)
    const [loadingEnum, setLoadingEnum] = useState(LoadingEnum.LOADING)

    const [ oneTeamPerUnit, setOneTeamPerUnit ] = useState(initDataRequest.oneTeamPerUnit)
    const [ nationality, setNationality ] = useState(undefined as number | undefined)
    const [ [ minAge, maxAge ], setMinMaxAge ] = useState ([undefined, undefined] as [number?, number?])
    const [ role, setRole ] = useState(undefined as string | undefined)
    const [ pageNumber, setPageNumber ] = useState(initDataRequest.statisticsParameters.page)
    const [ pageSize, setPageSize ] = useState(initDataRequest.statisticsParameters.pageSize)
    const [ playedInLastMatch, setPlayedInLastMatch ] = useState(initDataRequest.playedInLastMatch)
    const [ playedAllMatches, setPlayedAllMatches ] = useState(initDataRequest.playedAllMatches)
    const [ statType, setStatType ] = useState(initDataRequest.statisticsParameters.statsType)
    const [ season, setSeason ] = useState(initDataRequest.statisticsParameters.season)
    const [ sorting, setSorting ] = useState({field: initDataRequest.statisticsParameters.sortingField, direction: initDataRequest.statisticsParameters.sortingDirection})

    const [ data, setData ] = useState(undefined as RestTableData<Model> | undefined)

    useEffect(() => {
        setLoadingEnum(LoadingEnum.LOADING)
        props.requestFunc(createDataRequest(), 
            (loadingEnum, entities) => {
                setData(entities)         
                setLoadingEnum(loadingEnum)
            }
       ) 
    }, [ sorting, updateCounter, season, statType, playedAllMatches, playedInLastMatch, pageSize, pageNumber,  nationality, role, minAge, maxAge, oneTeamPerUnit ])

    function createDataRequest(): Request {
        return {
            statisticsParameters: {
                ...initDataRequest.statisticsParameters,
                season: season,
                sortingField: sorting.field,
                sortingDirection: sorting.direction,
                statsType: statType,
                pageSize: pageSize,
                page: pageNumber
            },
            playerParameters: {
                nationality: nationality,
                minAge: minAge,
                maxAge: maxAge,
                role: role
            },
            playedAllMatches: playedAllMatches,
            playedInLastMatch: playedInLastMatch,
            oneTeamPerUnit: oneTeamPerUnit
        }
    }

    function updateSorting(sortingField: string) {
        if (sortingField === sorting.field) {
            if (sorting.direction == SortingDirection.DESC) {
                setSorting({field: sortingField, direction: SortingDirection.ASC})
            } else {
                setSorting({field: sortingField, direction: SortingDirection.DESC})
            }
        } else {
            setSorting({field: sortingField, direction: SortingDirection.DESC})
        }
    }

    let seasonSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.SEASON_SELECTOR) !== -1) {
        seasonSelector = <Col lg={3} md={6}>
                <SeasonSelector currentSeason={season}
                seasonOffset={props.levelProps.seasonOffset()}
                seasons={props.levelProps.seasons()}
                callback={setSeason}/>
            </Col>
    }

    let statsTypeSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.STATS_TYPE_SELECTOR) !== -1) {
        statsTypeSelector = <Col lg={3} md={6}>
                <StatsTypeSelector statsTypes={props.statsTypes}
                    rounds={props.levelProps.rounds(season)}
                    selectedStatType={statType}
                    onChanged={setStatType}
                />
            </Col>
    }

    let playedAllMatchesSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.PLAYED_ALL_MATCHES_SELECTOR) !== -1) {
        playedAllMatchesSelector = <Col lg={2} md={4}>
            <CheckBoxSelector 
                value={playedAllMatches}
                callback={setPlayedAllMatches}
                title='filter.full_season'
                />
            </Col>
    }

    let playedInLastMatchSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.PLAYED_IN_LAST_MATCH_SELECTOR) !== -1) {
        playedInLastMatchSelector = <Col lg={2} md={5}>
            <CheckBoxSelector
                value={playedInLastMatch}
                callback={setPlayedInLastMatch}
                title='filter.played_in_last_match' />
            </Col>
    }

    let pageSizeSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.PAGE_SIZE_SELECTOR) !== -1) {
        pageSizeSelector = <Col lg={2} md={3} className='ms-auto'>
            <PageSizeSelector 
                selectedSize={pageSize}
                linkAction={(pageSize) => {
                    Cookies.set('hattid_page_size', pageSize.toString(), { sameSite: "Lax", expires: 180 })
                    setPageSize(pageSize)
                }} />
            </Col>
    }

    let pageSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.PAGE_SELECTOR) !== -1) {
        pageSelector = <PageNavigator 
            pageNumber={pageNumber} 
            isLastPage={(data === undefined) ? true : data.isLastPage} 
            linkAction={setPageNumber}/> 
    }

    let playerPositionsSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.PLAYER_ROLES) !== -1) {
        playerPositionsSelector = <PositionSelector 
            value={role}
            callback={setRole}/>
    }

    let nationalitySelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.NATIONALITIES_SELECTOR) !== -1) {
        nationalitySelector = <NationalitySelector 
            value={nationality}
            countryMap={props.levelProps.countriesMap()}
            callback={setNationality} />
    }

    let ageSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.AGE_SELECTOR) !== -1) {
        ageSelector = <AgeSelector callback={setMinMaxAge}/>
    }

    let oneTeamPerUnitSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.ONE_TEAM_PER_UNIT_SELECTOR) !== -1) {
        oneTeamPerUnitSelector = <Col lg={2} md={5}>
            <CheckBoxSelector
                value={oneTeamPerUnit}
                callback={setOneTeamPerUnit}
                title='filter.one_team_per_unit' />
            </Col>
    }

    let content = <Container className='table-responsive'>
        <Row>
            {seasonSelector} 
            {statsTypeSelector}
            {playedAllMatchesSelector}
            {playedInLastMatchSelector}
            {oneTeamPerUnitSelector}
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
            <Col lg={3} md={6} className='d-flex align-items-center'>
            </Col>
        </Row>
        <table className="table table-striped table-rounded table-sm small">
            <thead>
                <tr>
                {
                    props.tableColumns.map(tableColumn => {
                        let sortingInfo = (tableColumn.columnHeader.sortingField === undefined) ? undefined : {
                            field: tableColumn.columnHeader.sortingField, 
                            state: {callback: updateSorting, currentSorting: sorting.field, sortingDirection: sorting.direction}
                        }
                        return <SortingTableTh
                            title={tableColumn.columnHeader.title}
                            poppedHint={tableColumn.columnHeader.poppedHint}
                            sorting={sortingInfo}
                            center={tableColumn.columnHeader.center}
                        />
                    } )
                }
                </tr>
            </thead>
            <tbody>
                {
                    data?.entities.map((entity, index) => {
                        return <tr>
                            {props.tableColumns.map(tableColumn => {
                                return <td className={(tableColumn.columnValue.center === undefined || !tableColumn.columnValue.center) ? '' : 'text-center'}>{tableColumn.columnValue.provider(entity, pageSize * pageNumber + index)}</td>
                            } ) }
                        </tr>
                    } )
                }
            </tbody>
        </table>
        {pageSelector}
    </Container>

    return <>
        <Blur loadingState={loadingEnum} updateCallback={() => setUpdateCounter(updateCounter + 1)}/>
        {content}
    </>
}

export default HookAbstractTableSection
