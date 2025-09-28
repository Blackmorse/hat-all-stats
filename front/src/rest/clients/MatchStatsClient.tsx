import ax from 'axios';
import { LoadingEnum } from "../../common/enums/LoadingEnum";
import { parseAxiosResponse } from '../Client';
import SimilarMatchesStats from "../models/match/SimilarMatchesStats";
import SingleMatch from "../models/match/SingleMatch";

const axios = ax.create({ baseURL: import.meta.env.VITE_HATTID_SERVER_URL })

export interface SimilarMatchesRequest {
    singleMatch: SingleMatch,
    accuracy: number,
    considerTacticType: boolean,
    considerTacticSkill: boolean,
    considerSetPiecesLevels: boolean
}

export function getSimilarMatchesByRatingsWithAnnoy(similarMatchesRequest: SimilarMatchesRequest,
    callback: (loadingEnum: LoadingEnum, result?: SimilarMatchesStats) => void): void {

    const parameters: any = {}

    parameters.accuracy = similarMatchesRequest.accuracy
    parameters.considerTacticType = similarMatchesRequest.considerTacticType
    parameters.considerTacticSkill = similarMatchesRequest.considerTacticSkill
    parameters.considerSetPiecesLevels = similarMatchesRequest.considerSetPiecesLevels

    const parametersString = new URLSearchParams(parameters).toString()

    axios.post<SimilarMatchesStats>('/api/matches/similarMatchesByRatingsWithAnnoy?' + parametersString, similarMatchesRequest.singleMatch)
        .then(response => parseAxiosResponse(response, callback))
        .catch(_e => callback(LoadingEnum.ERROR))
}
