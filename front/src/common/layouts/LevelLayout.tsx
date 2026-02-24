import { type JSX, useEffect, useState } from 'react'
import {Link} from 'react-router-dom'
import LeftMenu from '../../common/menu/LeftMenu'
import { Callback, Failure, Success } from '../../rest/models/Https'
import NotFoundError from '../../rest/models/NotFoundLevel'
import {LoadingEnum} from '../enums/LoadingEnum'
import Mappings from '../enums/Mappings'
import {PagesEnum} from '../enums/PagesEnum'
import LevelDataProps from '../LevelDataProps'
import Layout from './Layout'
import Bot from '../widgets/Bot'


function parsePage(): string | undefined {
    const params = new URLSearchParams(window.location.search);

    return (params.get('pageName') === null) ? undefined : params.get('pageName') as string | undefined;
}

export interface BaseLevelLayoutProps<LevelProps extends LevelDataProps> {
    pagesMap: Map<PagesEnum, (props: LevelProps) => JSX.Element>
    topMenu: (props?: LevelProps) => JSX.Element
    fetchLevelData: (callback: Callback<LevelProps>) => void
    documentTitle: (levelProps: LevelProps) => string
}

interface Props<LevelProps extends LevelDataProps> extends BaseLevelLayoutProps<LevelProps> {
    topLeftMenu: (levelProps: LevelProps, onPageChange: (page: PagesEnum) => void) => JSX.Element
}

const LevelLayout = <LevelProps extends LevelDataProps>(props: Props<LevelProps>) => {
    const [ pageName ] = useState(parsePage())
    const [ page, setPage ] = useState((pageName === undefined) ? 
        Array.from(props.pagesMap)[0][0] : Mappings.queryParamToPageMap.get(pageName))

    const [ responseState, setResponseState ] = useState({loadingEnum: LoadingEnum.OK})
    const [ levelProps, setLevelProps ] = useState<LevelProps | undefined>(undefined)
    

    useEffect(() => {
       props.fetchLevelData(payload => {
            if (payload.loadingEnum === LoadingEnum.OK) {
                const success = payload as Success<LevelProps>
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

    if (responseState.loadingEnum === LoadingEnum.BOT) {
        return <Layout 
          leftMenu={<></>}
          content={<Bot />}
          topMenu={props.topMenu(levelProps)}
        />
    }
    const leftMenu = <>
            {(levelProps !== undefined) ? props.topLeftMenu(levelProps, setPage): <></>}
            <LeftMenu pages={Array.from(props.pagesMap.keys()).filter(p => (p !== PagesEnum.PROMOTIONS && p !== PagesEnum.TEAM_SEARCH && /*TODO */  p !== PagesEnum.TEAM_COMPARSION))} 
                    callback={leaguePage => setPage(leaguePage)}
                    title='menu.statistics'/>
            <LeftMenu pages={[PagesEnum.TEAM_SEARCH]} 
                    callback={leaguePage => setPage(leaguePage)}
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
        const notFoundError = (responseState as Failure).error as NotFoundError
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
    const jsxFunction = props.pagesMap.get(page!)
    if (levelProps && jsxFunction) {
        res = jsxFunction(levelProps)
    } else {
        res = <></>
    }
    const content = <>
            {errorPopup}
            {res}
        </>

    return <Layout 
            leftMenu={leftMenu}
            topMenu={props.topMenu(levelProps)}
            content={content}
        />
}

export default LevelLayout
