import { getWorldData } from '../rest/clients/LevelDataClient'
import WorldTopMenu from './WorldTopMenu'
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu'
import LevelLayout from '../common/layouts/LevelLayout';
import pages from './WorldPages';
import WorldLevelDataProps from './WorldLevelDataProps';


const World = () => {
    const pagesMap = pages()

    return <LevelLayout<WorldLevelDataProps>
            pagesMap={pagesMap}
            topMenu={(levelData => <WorldTopMenu levelProps={levelData} />)}
            topLeftMenu={leveData => <WorldLeftLoadingMenu worldLevelDataProps={leveData} />}
            fetchLevelData={callback => getWorldData(callback)}
            documentTitle={_levelData => 'World'}
        />

}

export default World
