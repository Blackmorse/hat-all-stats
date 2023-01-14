import React, {useEffect, useState} from 'react'
import {Card} from 'react-bootstrap'
import {useTranslation} from 'react-i18next'
import {Link} from 'react-router-dom'
import LeftMenu from '../../common/menu/LeftMenu'
import { Callback, Failure, Success } from '../../rest/models/Https'
import NotFoundError from '../../rest/models/NotFoundLevel'
import {LoadingEnum} from '../enums/LoadingEnum'
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

export interface BaseLevelLayoutProps<LevelProps extends LevelDataProps> {
    pagesMap: Map<PagesEnum, (props: LevelProps, queryParams: QueryParams) => JSX.Element>
    topMenu: (props?: LevelProps) => JSX.Element
    fetchLevelData: (callback: Callback<LevelProps>) => void
    documentTitle: (levelProps: LevelProps) => string
}

interface Props<LevelProps extends LevelDataProps> extends BaseLevelLayoutProps<LevelProps> {
    topLeftMenu: (levelProps: LevelProps, onPageChange: (page: PagesEnum) => void) => JSX.Element
}

const LevelLayout = <LevelProps extends LevelDataProps>(props: Props<LevelProps>) => {
    const t = useTranslation().t
    const [ queryParams ] = useState(parseQueryParams())
    // after the changing page query params should not apply
    const [ updateCounter, setUpdateCounter ] = useState(0)
    //TODO 
    const [ page, setPage ] = useState((queryParams.pageString === undefined) ? 
        Array.from(props.pagesMap)[0][0] : Mappings.queryParamToPageMap.get(queryParams.pageString))

    const [ responseState, setResponseState ] = useState({loadingEnum: LoadingEnum.OK})
    const [ levelProps, setLevelProps ] = useState<LevelProps | undefined>(undefined)
    

    useEffect(() => {
        props.fetchLevelData(payload => {
            if (payload.loadingEnum === LoadingEnum.OK) {
                let success = payload as Success<LevelProps>
                setResponseState(success)
                setLevelProps(success.model)
                document.title = props.documentTitle(success.model!) + ' - Hattid'
            } else if(payload.loadingEnum === LoadingEnum.NOT_FOUND) {
                setResponseState(payload)
            } else {
                setResponseState(payload)
            }
        })
    }, [])

    let setThisPage = (pagesEnum: PagesEnum) => {
        setUpdateCounter(updateCounter + 1)
        setPage(pagesEnum)
    }

    let leftMenu = <>
            {(levelProps !== undefined) ? props.topLeftMenu(levelProps, setThisPage): <></>}
            <LeftMenu pages={Array.from(props.pagesMap.keys()).filter(p => (p !== PagesEnum.PROMOTIONS && p !== PagesEnum.TEAM_SEARCH && /*TODO */  p !== PagesEnum.TEAM_COMPARSION))} 
                    callback={leaguePage => setThisPage(leaguePage)}
                    title='menu.statistics'/>
            <LeftMenu pages={[PagesEnum.TEAM_SEARCH]} 
                    callback={leaguePage => setThisPage(leaguePage)}
                    title='menu.team_search' /> 
        </>

    let errorPopup: JSX.Element
    if (responseState.loadingEnum === LoadingEnum.ERROR) {
        errorPopup = <div className="error_popup">
            <img src="/warning.gif" className="warning_img" alt="warning" />
            <span>
                Error! Page doesnt exist or internal error occured. 
                Try to <button className="warning_link" onClick={() => window.location.reload()}> reload </button> or return to the <Link className="warning_link" to="/">main page</Link>
            </span>
        </div>
    } else if (responseState.loadingEnum === LoadingEnum.NOT_FOUND) {
        let notFoundError = (responseState as Failure).error as NotFoundError
        errorPopup = <div className="error_popup">
            <img src="/warning.gif" className="warning_img" alt="warning" />
            <span>
                404: no entity ({notFoundError.entityType}) with id {notFoundError.entityId} was found 
            </span>
        </div>
    } else {
        errorPopup = <></>
    }
    let res: JSX.Element
    let jsxFunction = props.pagesMap.get(page!)
    if (levelProps && jsxFunction) {
        if(updateCounter < 1) {
            res = jsxFunction(levelProps, queryParams)
        } else {
            res = jsxFunction(levelProps, {})
        }
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
            topMenu={props.topMenu(levelProps)}
            content={content}
        />
}

export default LevelLayout
