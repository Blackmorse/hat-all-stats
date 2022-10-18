import React from 'react'
import {useMatch} from 'react-router'
import {PagesEnum} from '../common/enums/PagesEnum'
import CountryLevelLayout from '../common/layouts/CountryLevelLayout'
import QueryParams from '../common/QueryParams'
import {getPlayerData} from '../rest/clients/LevelDataClient'
import PlayerDetailsSection from './PlayerDetailsSection'
import PlayerLevelDataProps from './PlayerLevelDataProps'
import PlayerTopMenu from './PlayerTopMenu'


const Player = () => {
    const pagesMap = new Map<PagesEnum, (props: PlayerLevelDataProps, queryParams: QueryParams) => JSX.Element>() 
    

    pagesMap.set(PagesEnum.PLAYER_DETAILS, (props, _queryParams) => <PlayerDetailsSection playerProps={props} />)

    const playerId = useMatch('/player/:playerId')

    return <CountryLevelLayout<PlayerLevelDataProps>
            pagesMap={pagesMap}
            documentTitle={levelData => levelData.firstName() + ' ' + levelData.lastName()}
            fetchLevelData={callback => getPlayerData(Number(playerId?.params.playerId), callback)}
            topMenu={levelProps => <PlayerTopMenu levelProps={levelProps}/>}
        />
}

export default Player
