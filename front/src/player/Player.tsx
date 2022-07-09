import React from 'react'
import {useMatch} from 'react-router'
import {PagesEnum} from '../common/enums/PagesEnum'
import CountryLevelLayout from '../common/layouts/CountryLevelLayout'
import QueryParams from '../common/QueryParams'
import {getPlayerData} from '../rest/Client'
import PlayerData from '../rest/models/leveldata/PlayerData'
import PlayerLevelDataProps from './PlayerLevelDataProps'
import PlayerTopMenu from './PlayerTopMenu'


const Player = () => {
    const pagesMap = new Map<PagesEnum, (props: PlayerLevelDataProps, queryParams: QueryParams) => JSX.Element>() 
    pagesMap.set(PagesEnum.PLAYER_DETAILS, (props, queryParams) => <></>)

    const playerId = useMatch('/player/:playerId')

    return <CountryLevelLayout<PlayerData, PlayerLevelDataProps>
            pagesMap={pagesMap}
            documentTitle={levelData => levelData.firstName + ' ' + levelData.lastName}
            makeModelProps={levelData => new PlayerLevelDataProps(levelData)}
            fetchLevelData={(callback, onError) => getPlayerData(Number(playerId?.params.playerId), callback, onError)}
            topMenu={levelData => <PlayerTopMenu data={levelData}/>}
        />
}

export default Player
