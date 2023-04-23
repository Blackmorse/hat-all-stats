import ax from 'axios';
import {LoadingEnum} from '../../common/enums/LoadingEnum';
import DivisionLevelDataProps from '../../divisionlevel/DivisionLevelDataProps';
import LeagueLevelDataProps from '../../league/LeagueLevelDataProps';
import LeagueUnitLevelDataProps from '../../leagueunit/LeagueUnitLevelDataProps';
import PlayerLevelDataProps from '../../player/PlayerLevelDataProps';
import TeamLevelDataProps from '../../team/TeamLevelDataProps';
import WorldLevelDataProps from '../../world/WorldLevelDataProps';
import { Callback } from '../models/Https';
import DivisionLevelData from '../models/leveldata/DivisionLevelData';
import LeagueData from '../models/leveldata/LeagueData';
import LeagueUnitData from '../models/leveldata/LeagueUnitData';
import PlayerData from '../models/leveldata/PlayerData';
import TeamData from '../models/leveldata/TeamData';
import WorldData from '../models/leveldata/WorldData';
import NotFoundError from '../models/NotFoundLevel';

const axios = ax.create({ baseURL: process.env.REACT_APP_HATTID_SERVER_URL })


function catchError<T>(e: any, callback: Callback<T>) {
    if (e.response.status === 404) {
        callback({
            loadingEnum: LoadingEnum.NOT_FOUND, 
            error: (e.response.data as NotFoundError)
        })
    } else {
        callback({loadingEnum: LoadingEnum.ERROR, error: {}})
    }
}


export function getLeagueData(leagueId: number, 
        callback: Callback<LeagueLevelDataProps>): void {
    axios.get<LeagueData>('/api/league/' + leagueId)
        .then(response => new LeagueLevelDataProps(response.data))
        .then(props => callback({loadingEnum: LoadingEnum.OK, model: props}))
        .catch(e => catchError(e, callback))
}


export function getDivisionLevelData(leagueId: number, divisionLevel: number, 
        callback: Callback<DivisionLevelDataProps>): void {

    axios.get<DivisionLevelData>('/api/league/' + leagueId + '/divisionLevel/' + divisionLevel)   
        .then(response => new DivisionLevelDataProps(response.data))
        .then(props => callback({loadingEnum: LoadingEnum.OK, model: props}))
        .catch(e => catchError(e, callback))
}


export function getLeagueUnitData(leagueUnitId: number, callback: Callback<LeagueUnitLevelDataProps>): void {
    axios.get<LeagueUnitData>('/api/leagueUnit/' + leagueUnitId)
        .then(response => new LeagueUnitLevelDataProps(response.data))
        .then(props => callback({loadingEnum: LoadingEnum.OK, model: props}))
        .catch(e => catchError(e, callback))
}


export function getTeamData(leagueId: number, callback: Callback<TeamLevelDataProps>): void {
    axios.get<TeamData>('/api/team/' + leagueId)
        .then(response => new TeamLevelDataProps(response.data))
        .then(props => callback({loadingEnum: LoadingEnum.OK, model: props}))
        .catch(e => catchError(e, callback))
}


export function getWorldData(callback: Callback<WorldLevelDataProps>): void {
    axios.get<WorldData>('/api/overview/worldData')
        .then(response => new WorldLevelDataProps(response.data))
        .then(props => callback({loadingEnum: LoadingEnum.OK, model: props}))
        .catch(e => catchError(e, callback))
}



export function getPlayerData(playerId: number, callback: Callback<PlayerLevelDataProps>) {
    axios.get<PlayerData>('/api/player/' + playerId)
        .then(response => new PlayerLevelDataProps(response.data))
        .then(props => callback({loadingEnum: LoadingEnum.OK, model: props}))
        .catch(e => catchError(e, callback))
}
