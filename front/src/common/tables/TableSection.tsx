import React, {useEffect, useState} from 'react'
import {Col, Container, Row} from 'react-bootstrap'
import RestTableData from '../../rest/models/RestTableData'
import StatisticsParameters, {SortingDirection, StatsType, StatsTypeEnum} from '../../rest/models/StatisticsParameters'
import LevelDataProps from '../LevelDataProps'
import {Card} from 'react-bootstrap'
import Cookies from 'js-cookie'
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
import {HookMatchRow} from './rows/match/MatchRow'
import { PagesEnum } from '../enums/PagesEnum'
import Mappings from '../enums/Mappings'
import { useLocation } from 'react-router'
import Popup from 'reactjs-popup'
import { useTranslation } from 'react-i18next'
import './TableSection.css'

export interface Request {
    statisticsParameters: StatisticsParameters
    selectedRow?: number,
    playerParameters: PlayersParameters
    playedAllMatches: boolean
    playedInLastMatch: boolean
    oneTeamPerUnit: boolean
    excludeZeroTsi: boolean
}

interface TableQueryParams {
    pageName: string,
    selectedRow?: number,
    //statisticsParameters
    season: number,
    sortingField: string,
    sortingDirection: SortingDirection,
    statType: StatsTypeEnum,
    statTypeRound?: number,
    pageSize: number,
    pageNumber: number,
    //playerParameters
    nationality?: number,
    minAge?: number,
    maxAge?: number,
    role?: string,

    playedAllMatches: boolean,
    playedInLastMatch: boolean,
    oneTeamPerUnit: boolean,
    excludeZeroTsi: boolean
}

function parseParams<LevelProps extends LevelDataProps, Model>(props: Properties<LevelProps, Model>): TableQueryParams {
    let params = new URLSearchParams(window.location.search);

    let selectedRow = (params.get('selectedRow') === null) ? undefined : Number(params.get('selectedRow'))
    let cookiesPageSize = (Cookies.get('hattid_page_size') === undefined) ? 16 : Number(Cookies.get('hattid_page_size'))
    let pageSize = (params.get('pageSize') === null) ? cookiesPageSize : Number(params.get('pageSize'))
    let pageNumberFromParams = (params.get('pageNumber') === null) ? undefined : Number(params.get('pageNumber'))

    let pageNumberFromSelectedRow = (selectedRow) ? Math.floor(Number(selectedRow) / pageSize) : 0

    let pageNumber: number
    if (pageNumberFromParams === undefined) {
        pageNumber = pageNumberFromSelectedRow
    } else {
        pageNumber = (pageNumberFromParams === undefined) ? 0 : pageNumberFromParams
    }
    let statType = (params.get('statType') === null) ? props.defaultStatsType?.statType : Mappings.statsTypeMappings.ForwardMap.get(params.get('statType')!)!
    let statTypeRound: number | undefined = undefined
    if (statType === StatsTypeEnum.ROUND) {
        statTypeRound = (params.get('round') === null) ? props.levelProps.currentRound() : Number(params.get('round'))
    }

    return {
        pageName: Mappings.queryParamToPageMap.ReverseMap.get(props.pageEnum!)!,
        selectedRow: selectedRow,
        season: (params.get('season') === null) ? props.levelProps.currentSeason() : Number(params.get('season')),

        sortingField: (params.get('sortingField') === null) ? props.defaultSortingField : params.get('sortingField') as string,
        sortingDirection: (params.get('sortingDirection') === null) ? SortingDirection.DESC : Mappings.directionMappings.ForwardMap.get(params.get('sortingDirection')!)!,
        statType: statType,
        statTypeRound: statTypeRound,

        pageSize: pageSize,
        pageNumber: pageNumber,

        nationality: (params.get('nationality') === null) ? undefined : Number(params.get('nationality')) ,
        minAge: (params.get('minAge') === null) ? undefined : Number(params.get('minAge')),
        maxAge: (params.get('maxAge') === null) ? undefined : Number(params.get('maxAge')),
        role: (params.get('role') === null) ? undefined : params.get('role') as string | undefined,

        playedAllMatches: (params.get('playedAllMatches') === null) ? false : Boolean(params.get('playedAllMatches')),
        playedInLastMatch: (params.get('playedInLastMatch') === null) ? false : Boolean(params.get('playedInLastMatch')),
        oneTeamPerUnit: (params.get('oneTeamPerUnit') === null) ? false : Boolean(params.get('oneTeamPerUnit')),
        excludeZeroTsi: (params.get('excludeZeroTsi') === null) ? false : Boolean(params.get('excludeZeroTsi'))
    }
}

interface Properties<LevelProps extends LevelDataProps, Model> {
    levelProps: LevelProps,
    requestFunc: (request: Request, callback: (loadingEnum: LoadingEnum, result?: RestTableData<Model>) => void) => void,
    defaultSortingField: string,
    defaultStatsType: StatsType,
    tableColumns: Array<TableColumn<Model>>,
    selectors: Array<SelectorsEnum>,
    statsTypes: Array<StatsTypeEnum>,
    expandedRowFunc?: (model: Model) => JSX.Element,
    pageEnum: PagesEnum
}


const TableSection = <LevelProps extends LevelDataProps, Model>(props: Properties<LevelProps, Model>) => {
    let queryParameters = parseParams(props)
    let location = useLocation()  
    let [ t, _i18n ] = useTranslation()
    const [ updateCounter, setUpdateCounter ] = useState(0)
    const [ loadingEnum, setLoadingEnum ] = useState(LoadingEnum.LOADING)
    const [ oneTeamPerUnit, setOneTeamPerUnit ] = useState(queryParameters.oneTeamPerUnit)
    const [ nationality, setNationality ] = useState(queryParameters.nationality)
    const [ [ minAge, maxAge ], setMinMaxAge ] = useState ([queryParameters.minAge, queryParameters.maxAge])
    const [ role, setRole ] = useState(queryParameters.role)
    const [ pageNumber, setPageNumber ] = useState(queryParameters.pageNumber)
    const [ pageSize, setPageSize ] = useState(queryParameters.pageSize)
    const [ playedInLastMatch, setPlayedInLastMatch ] = useState(queryParameters.playedInLastMatch)
    const [ playedAllMatches, setPlayedAllMatches ] = useState(queryParameters.playedAllMatches)
    const [ statType, setStatType ] = useState({statType: queryParameters.statType, roundNumber: queryParameters.statTypeRound} as StatsType)
    const [ season, setSeason ] = useState(queryParameters.season)
    const [ sorting, setSorting ] = useState({field: queryParameters.sortingField, direction: queryParameters.sortingDirection})
    const [ excludeZeroTsi, setExcludeZeroTsi ] = useState(queryParameters.excludeZeroTsi)

    const [ data, setData ] = useState(undefined as RestTableData<Model> | undefined)

    useEffect(() => {
        setLoadingEnum(LoadingEnum.LOADING)
        props.requestFunc(createDataRequest(), 
            (loadingEnum, entities) => {
                setData(entities)         
                setLoadingEnum(loadingEnum)
            }
       ) 
    }, [ sorting, updateCounter, season, statType, playedAllMatches, playedInLastMatch, excludeZeroTsi, pageSize, pageNumber,  nationality, role, minAge, maxAge, oneTeamPerUnit ])

    function row(rowNum: number, entity: Model, className?: string): JSX.Element {
       return <HookMatchRow
           rowNum={rowNum}
           entity={entity}
           tableColumns={props.tableColumns}
           expandedRowFunc={props.expandedRowFunc}
           className={className}
        />
    }

    function createDataRequest(): Request {
        return {
            statisticsParameters: {
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
            oneTeamPerUnit: oneTeamPerUnit,
            excludeZeroTsi: excludeZeroTsi
        }
    }

    function updateSorting(sortingField: string) {
        if (sortingField === sorting.field) {
            if (sorting.direction === SortingDirection.DESC) {
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

    let zeroPlayerTsiSelector = <></>
    if(props.selectors.indexOf(SelectorsEnum.EXCLUDE_ZERO_TSI_PLAYERS) !== -1) {
        zeroPlayerTsiSelector = <Col lg={2} md={5}>
            <CheckBoxSelector
                value={excludeZeroTsi}
                callback={setExcludeZeroTsi}
                title='filter.exclude_zero_player_tsi' 
            />
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
        // ageSelector = <AgeSelector callback={setMinMaxAge}/> Why not working?
        ageSelector = <AgeSelector minAge={minAge} maxAge={maxAge} callback={(arr) => setMinMaxAge([ arr[0], arr[1] ])}/>
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

    let shareParams: TableQueryParams = {
        pageName: queryParameters.pageName,
        selectedRow: queryParameters.selectedRow,
        season: season,
        sortingField: sorting.field,
        sortingDirection: Mappings.directionMappings.ForwardMap.get(sorting.direction)!,
        statType: statType.statType,
        statTypeRound: statType.roundNumber,
        pageSize: pageSize,
        pageNumber: pageNumber,
        nationality: nationality,
        minAge: minAge,
        maxAge: maxAge,
        role: role,

        playedAllMatches: playedAllMatches,
        playedInLastMatch: playedInLastMatch,
        oneTeamPerUnit: oneTeamPerUnit,
        excludeZeroTsi: excludeZeroTsi
    }
    let anyParams: any = Object.assign({}, shareParams)
    let params = new URLSearchParams(anyParams)
    let keysToDel: Array<string> = []
    params.forEach((value, key) => {
        if (value === undefined || value === 'undefined') {
            keysToDel.push(key)
        }
    })

    keysToDel.forEach(key => params.delete(key))

    let sharedLink = window.location.protocol + '//' + window.location.host + location.pathname + '?' + params.toString()
    let sharedLinkPopup = <Popup position='bottom center' nested trigger={<button className='btn btn-sm btn-success me-5'> Share <i className="bi bi-caret-down-fill"></i></button>}>
            <div className='input-group bg-light border border-success p-2'>
                <input type="text" className="form-control" value={sharedLink} />
                <button type="button" className="btn btn-outline-secondary" onClick={() => {navigator.clipboard.writeText(sharedLink)}}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-clipboard" viewBox="0 0 16 16">
                      <path d="M4 1.5H3a2 2 0 0 0-2 2V14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V3.5a2 2 0 0 0-2-2h-1v1h1a1 1 0 0 1 1 1V14a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V3.5a1 1 0 0 1 1-1h1v-1z"></path>
                      <path d="M9.5 1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5v-1a.5.5 0 0 1 .5-.5h3zm-3-1A1.5 1.5 0 0 0 5 1.5v1A1.5 1.5 0 0 0 6.5 4h3A1.5 1.5 0 0 0 11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3z"></path>
                  </svg>
                </button>
            </div>
        </Popup>
    let content = 
            <Card className="mt-3 shadow">
                <Card.Header className="lead d-flex flex-row justify-content-between">{t(props.pageEnum)}{sharedLinkPopup}</Card.Header>
                <Card.Body>
       <Container className='table-responsive'>
        <Row>
            {seasonSelector} 
            {statsTypeSelector}
            {playedAllMatchesSelector}
            {playedInLastMatchSelector}
            {oneTeamPerUnitSelector}
            {zeroPlayerTsiSelector}
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
                {(props.expandedRowFunc === undefined) ? <></> : <td></td>}
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
                        return row(pageSize * pageNumber + index, entity,
                            ((queryParameters.selectedRow !== undefined) && queryParameters.selectedRow === pageSize * pageNumber + index) ? "selected_row" : "")
                    } )
                }
            </tbody>
        </table>
        {pageSelector}
    </Container></Card.Body>
            </Card>

    return <>
        <Blur loadingState={loadingEnum} updateCallback={() => setUpdateCounter(updateCounter + 1)}/>
        {content}
    </>
}

export default TableSection
