import Layout from '../Layout'
import { PagesEnum } from './enums/PagesEnum'
import { ModelTableProps } from './ModelTable'
import React from 'react'
import LevelData from '../rest/models/LevelData'

interface PageLayoutState<Data extends LevelData> {
    leaguePage: PagesEnum,
    levelData?: Data
}

abstract class PageLayout<Props, Data extends LevelData> extends Layout<Props, PageLayoutState<Data>> {
    pagesMap = new Map<PagesEnum, (props: ModelTableProps<LevelData>) => JSX.Element>()

    constructor(props: Props,
        pagesMap: Map<PagesEnum, (props:  ModelTableProps<LevelData>) => JSX.Element>) {
        super(props)
        this.pagesMap = pagesMap
    }

    abstract makeModelProps(levelData: LevelData):  ModelTableProps<LevelData>

    content() {
        let res: JSX.Element
        let jsxFunction = this.pagesMap.get(this.state.leaguePage)
        if (this.state.levelData && jsxFunction) {
            res = jsxFunction(this.makeModelProps(this.state.levelData))
        } else {
            res = <></>
        }
        return res
    }
}

export default PageLayout