import ax, {AxiosResponse} from 'axios';
import {LoadingEnum} from '../../common/enums/LoadingEnum';
import LevelDataProps from '../../common/LevelDataProps';
import DivisionLevelDataProps from '../../divisionlevel/DivisionLevelDataProps';
import LeagueLevelDataProps from '../../league/LeagueLevelDataProps';
import LeagueUnitLevelDataProps from '../../leagueunit/LeagueUnitLevelDataProps';
import PlayerLevelDataProps from '../../player/PlayerLevelDataProps';
import TeamLevelDataProps from '../../team/TeamLevelDataProps';
import WorldLevelDataProps from '../../world/WorldLevelDataProps';
import DivisionLevelData from '../models/leveldata/DivisionLevelData';
import LeagueData from '../models/leveldata/LeagueData';
import LeagueUnitData from '../models/leveldata/LeagueUnitData';
import LevelData from '../models/leveldata/LevelData';
import PlayerData from '../models/leveldata/PlayerData';
import TeamData from '../models/leveldata/TeamData';
import WorldData from '../models/leveldata/WorldData';

const axios = ax.create({ baseURL: process.env.REACT_APP_HATTID_SERVER_URL })


function parseResponse<Data extends LevelData, Props extends LevelDataProps>(axiosResponse: AxiosResponse<Data, any>, converter: (levelData: Data) => Props, callback: (loadingEnum: LoadingEnum, props?: Props) => void) {
    if (axiosResponse.status === 404) {
        callback(LoadingEnum.NOT_FOUND)
    } else {
        callback(LoadingEnum.OK, converter(axiosResponse.data))
    }
}

function catchError(e: any, callback: (loadingEnum: LoadingEnum) => void) {
    if (e.response.status === 404) {
        callback(LoadingEnum.NOT_FOUND)
    } else {
        callback(LoadingEnum.ERROR)
    }
}


export function getLeagueData(leagueId: number, 
        callback: (loadingEnum: LoadingEnum, leagueLevelDataProps?: LeagueLevelDataProps) => void): void {
    axios.get<LeagueData>('/api/league/' + leagueId)
        .then(response => new LeagueLevelDataProps(response.data))
        .then(props => callback(LoadingEnum.OK, props))
        .catch(e => catchError(e, callback))
}


export function getDivisionLevelData(leagueId: number, divisionLevel: number, 
        callback: (loadingEnum: LoadingEnum, divisionLevelDataProps?: DivisionLevelDataProps) => void): void {

    axios.get<DivisionLevelData>('/api/league/' + leagueId + '/divisionLevel/' + divisionLevel)   
        .then(response => new DivisionLevelDataProps(response.data))
        .then(props => callback(LoadingEnum.OK, props))
        .catch(e => catchError(e, callback))
}


export function getLeagueUnitData(leagueUnitId: number, 
        callback: (loadingEnum: LoadingEnum, leagueUnitLevelDataProps?: LeagueUnitLevelDataProps) => void): void {
    axios.get<LeagueUnitData>('/api/leagueUnit/' + leagueUnitId)
        .then(response => new LeagueUnitLevelDataProps(response.data))
        .then(props => callback(LoadingEnum.OK, props))
        .catch(e => catchError(e, callback))
}


export function getTeamData(leagueId: number, 
        callback: (loadingEnum: LoadingEnum, teamData?: TeamLevelDataProps) => void): void {
    axios.get<TeamData>('/api/team/' + leagueId)
        .then(response => new TeamLevelDataProps(response.data))
        .then(props => callback(LoadingEnum.OK, props))
        .catch(e => catchError(e, callback))
}


export function getWorldData(callback: (loadingEnum: LoadingEnum, worldLevelDataProps?: WorldLevelDataProps) => void): void {
    axios.get<WorldData>('/api/overview/worldData')
        .then(response => new WorldLevelDataProps(response.data))
        .then(props => callback(LoadingEnum.OK, props))
        .catch(e => catchError(e, callback))
}


export function getPlayerData(playerId: number, callback: (loadingEnum: LoadingEnum, result?: PlayerLevelDataProps) => void) {
    axios.get<PlayerData>('/api/player/' + playerId)
        .then(response => new PlayerLevelDataProps(response.data))
        .then(props => callback(LoadingEnum.OK, props))
        .catch(e => catchError(e, callback))
}
