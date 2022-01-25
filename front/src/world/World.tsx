import React from 'react'
import { getWorldData } from '../rest/Client'
import WorldTopMenu from './WorldTopMenu'
import WorldLevelDataProps from './WorldLevelDataProps'
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu'
import WorldData from '../rest/models/leveldata/WorldData'
import LevelDataProps from '../common/LevelDataProps';
import LevelLayout from '../common/layouts/LevelLayout';
import pages from './WorldPages';


const World = () => {
    let pagesMap = pages()

    return <LevelLayout<WorldData, LevelDataProps<WorldData>>
            pagesMap={pagesMap}
            topMenu={(levelData => <WorldTopMenu data={levelData} />)}
            topLeftMenu={leveData => <WorldLeftLoadingMenu worldData={leveData} />}
            fetchLevelData={(callback, onError) => getWorldData(callback, onError)}
            makeModelProps={levelData => new WorldLevelDataProps(levelData)}
            documentTitle={_levelData => 'World'}
        />

}

export default World
