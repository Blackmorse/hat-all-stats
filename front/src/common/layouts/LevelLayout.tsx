import React, {useEffect, useState} from 'react'
import {Card} from 'react-bootstrap'
import {useTranslation} from 'react-i18next'
import {Link} from 'react-router-dom'
import LeftMenu from '../../common/menu/LeftMenu'
import LevelData from '../../rest/models/leveldata/LevelData'
import Mappings from '../enums/Mappings'
import {PagesEnum} from '../enums/PagesEnum'
import LevelDataProps from '../LevelDataProps'
import QueryParams from '../QueryParams'
import Layout from './Layout'


function parseQueryParams(): QueryParams {
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

    let teamIdParams = params.get('teamId')
    let teamId: number | undefined = undefined
    if(teamIdParams !== null) {
        teamId = Number(teamIdParams)
    }

        
    let pageStringParams = params.get('page')
    let pageString: string | undefined = undefined
    if(pageStringParams !== null) {
        pageString = pageStringParams
    }

    return {
        sortingField: sortingField,
        selectedRow: selectedRow,
        round: round,
        season: season,
        teamId: teamId,
        pageString: pageString
    }
}

export interface BaseLevelLayoutProps<Data extends LevelData, TableProps extends LevelDataProps<Data>> {
    pagesMap: Map<PagesEnum, (props: TableProps, queryParams: QueryParams) => JSX.Element>
    topMenu: (data?: Data) => JSX.Element
    fetchLevelData: (callback: (data: Data) => void, onError: () => void) => void
    makeModelProps: (levelData: Data) => TableProps
    documentTitle: (data: Data) => string
}

interface Props<Data extends LevelData, TableProps extends LevelDataProps<Data>> extends BaseLevelLayoutProps<Data, TableProps> {
    topLeftMenu: (data: Data, onPageChange: (page: PagesEnum) => void) => JSX.Element
}

const LevelLayout = <Data extends LevelData, TableProps extends LevelDataProps<Data>>(props: Props<Data, TableProps>) => {
    const t = useTranslation().t
    const [ queryParams ] = useState(parseQueryParams())
    //TODO 
    const [ page, setPage ] = useState((queryParams.pageString === undefined) ? 
        Array.from(props.pagesMap)[0][0] : Mappings.queryParamToPageMap.get(queryParams.pageString))
    const [ isError, setIsError ] = useState(false)
    const [ levelData, setLevelData ] = useState(undefined as Data | undefined)

    useEffect(() => {
        props.fetchLevelData(data => {
            setLevelData(data)
            setIsError(false)
            document.title = props.documentTitle(data) + ' - AlltidLike'
        },
        () => setIsError(true))
    }, [])

    let leftMenu = <>
            {(levelData !== undefined) ? props.topLeftMenu(levelData, setPage): <></>}
            <LeftMenu pages={Array.from(props.pagesMap.keys()).filter(p => (p !== PagesEnum.PROMOTIONS && p !== PagesEnum.TEAM_SEARCH && /*TODO */  p !== PagesEnum.TEAM_COMPARSION))} 
                    callback={leaguePage => setPage(leaguePage)}
                    title='menu.statistics'/>
            <LeftMenu pages={[PagesEnum.TEAM_SEARCH]} 
                    callback={leaguePage => setPage(leaguePage)}
                    title='menu.team_search' /> 
        </>

    let errorPopup: JSX.Element
    if (isError) {
        errorPopup = <div className="error_popup">
            <img src="/warning.gif" className="warning_img" alt="warning" />
            <span>
                Error! Page doesnt exist or internal error occured. 
                Try to <button className="warning_link" onClick={() => window.location.reload()}> reload </button> or return to the <Link className="warning_link" to="/">main page</Link>
            </span>
        </div>
    } else {
        errorPopup = <></>
    }
    let res: JSX.Element
    let jsxFunction = props.pagesMap.get(page!)
    if (levelData && jsxFunction) {
        res = jsxFunction(props.makeModelProps(levelData), queryParams)
    } else {
        res = <></>
    }
    let content = <>
            {errorPopup}
            <Card className="mt-3 shadow">
                <Card.Header className="lead">{t(page!)}</Card.Header>
                <Card.Body>{res}</Card.Body>
            </Card>
        </>

    return <Layout 
            leftMenu={leftMenu}
            topMenu={props.topMenu(levelData)}
            content={content}
        />
}

export default LevelLayout
