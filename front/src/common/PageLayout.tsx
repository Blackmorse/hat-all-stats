import Layout from '../Layout'
import { PagesEnum } from './enums/PagesEnum'
import { ModelTableProps } from './ModelTable'
import React from 'react'
import LevelData from '../rest/models/LevelData'
import { Translation } from 'react-i18next'
import LeftMenu from '../common/menu/LeftMenu'
import '../i18n'
import './PageLayout.css'

export interface PageLayoutState<Data extends LevelData> {
    leaguePage: PagesEnum,
    levelData?: Data
}

abstract class PageLayout<Props, Data extends LevelData, TableProps extends ModelTableProps<Data>> extends Layout<Props, PageLayoutState<Data>> {
    pagesMap = new Map<PagesEnum, (props: TableProps) => JSX.Element>()

    constructor(props: Props,
        pagesMap: Map<PagesEnum, (props:  TableProps) => JSX.Element>) {
        super(props)
        this.pagesMap = pagesMap
        this.state = {leaguePage: Array.from(pagesMap)[0][0]}
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
        return <LeftMenu pages={Array.from(this.pagesMap.keys())} 
            callback={leaguePage =>{this.setState({leaguePage: leaguePage})}}/>
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

export default PageLayout