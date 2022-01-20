import React from 'react'
import { getWorldData } from '../rest/Client'
import WorldTopMenu from './WorldTopMenu'
import { useNavigate } from 'react-router';
import WorldLevelDataProps from './WorldLevelDataProps'
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu'
import WorldData from '../rest/models/leveldata/WorldData'
import LevelDataProps from '../common/LevelDataProps';
import LevelLayout from '../common/layouts/LevelLayout';
import pages from './WorldPages';


const World = () => {
    let pagesMap = pages()
    const navigate = useNavigate() 

    function leagueIdSelected(leagueId: number) {
        navigate('/league/' + leagueId)
    }

    return <LevelLayout<WorldData, LevelDataProps<WorldData>>
            pagesMap={pagesMap}
            topMenu={(levelData => <WorldTopMenu data={levelData} callback={leagueIdSelected}  />)}
            topLeftMenu={leveData => <WorldLeftLoadingMenu worldData={leveData} />}
            fetchLevelData={(callback, onError) => getWorldData(callback, onError)}
            makeModelProps={levelData => new WorldLevelDataProps(levelData)}
            documentTitle={_levelData => 'World'}
        />

}

export default World
