import ax from 'axios';
import { LoadingEnum } from "../../common/enums/LoadingEnum"
import LevelRequest from "../models/request/LevelRequest"
import RestTableData from "../models/RestTableData"
import StatisticsParameters from "../models/StatisticsParameters"
import TeamSalaryTSI from "../models/team/TeamSalaryTSI"
import { createStatisticsParameters, startUrl, parseAxiosResponse, statisticsRequest } from '../Client'
import TeamHatstats from '../models/team/TeamHatstats';


const axios = ax.create({ baseURL: process.env.REACT_APP_HATTID_SERVER_URL })


export function getTeamSalaryTSI(request: LevelRequest,
        statisticsParameters: StatisticsParameters,
        playedInLastMatch: boolean,
        excludeZeroTsi: boolean,
        callback: (loadingEnum: LoadingEnum, entities?: RestTableData<TeamSalaryTSI>) => void) {
    let params = createStatisticsParameters(statisticsParameters)
    axios.get<RestTableData<TeamSalaryTSI>>(startUrl(request) + '/teamSalaryTsi?' + params.toString() + 
            '&playedInLastMatch=' + playedInLastMatch + '&excludeZeroTsi=' + excludeZeroTsi)
        .then(response => parseAxiosResponse(response, callback))
        .catch(_e => callback(LoadingEnum.ERROR))
}

export let getTeamHatstats = statisticsRequest<TeamHatstats>('teamHatstats')