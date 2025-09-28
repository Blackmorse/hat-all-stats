import ax from 'axios';
import { LoadingEnum } from "../../common/enums/LoadingEnum"
import LevelRequest from "../models/request/LevelRequest"
import RestTableData from "../models/RestTableData"
import StatisticsParameters from "../models/StatisticsParameters"
import TeamSalaryTSI from "../models/team/TeamSalaryTSI"
import { createStatisticsParameters, startUrl, parseAxiosResponse, statisticsRequest } from '../Client'
import TeamHatstats from '../models/team/TeamHatstats';
import TeamFanclubFlags from '../models/team/TeamFanclubFlags';
import TeamStreakTrophies from '../models/team/TeamStreakTrophies';
import TeamRankingsStats from '../models/team/TeamRankingsStats';


const axios = ax.create({ baseURL: import.meta.env.VITE_HATTID_SERVER_URL })


export function getTeamSalaryTSI(request: LevelRequest,
        statisticsParameters: StatisticsParameters,
        playedInLastMatch: boolean,
        excludeZeroTsi: boolean,
        callback: (loadingEnum: LoadingEnum, entities?: RestTableData<TeamSalaryTSI>) => void) {
    const params = createStatisticsParameters(statisticsParameters)
    axios.get<RestTableData<TeamSalaryTSI>>(startUrl(request) + '/teamSalaryTsi?' + params.toString() + 
            '&playedInLastMatch=' + playedInLastMatch + '&excludeZeroTsi=' + excludeZeroTsi)
        .then(response => parseAxiosResponse(response, callback))
        .catch(_e => callback(LoadingEnum.ERROR))
}

export const getTeamHatstats = statisticsRequest<TeamHatstats>('teamHatstats')

export const getTeamFanclubFlags = statisticsRequest<TeamFanclubFlags>('teamFanclubFlags')

export const getTeamStreakTrophies = statisticsRequest<TeamStreakTrophies>('teamStreakTrophies')

export function getTeamRankings(teamId: number, season: number,
                                 callback: (loadingEnum: LoadingEnum, teamRankingsStats?: TeamRankingsStats) => void) {
    const seasonFilter = (season !== -1) ? `season=${season}` : ""
    axios.get<TeamRankingsStats>(`/api/team/${teamId}/teamRankings?${seasonFilter}`)
        .then(response => parseAxiosResponse(response, callback))
        .catch(_e => callback(LoadingEnum.ERROR))
}

export function getTeamRankingsRange(teamId: number, fromSeason: number, toSeason: number,
                                    callback: (loadingEnum: LoadingEnum, teamRankingsStats?: TeamRankingsStats) => void) {
    const seasonFilter = `fromSeason=${fromSeason}&toSeason=${toSeason}`
    axios.get<TeamRankingsStats>(`/api/team/${teamId}/teamRankingsRange?${seasonFilter}`)
        .then(response => parseAxiosResponse(response, callback))
        .catch(_e => callback(LoadingEnum.ERROR))
}
