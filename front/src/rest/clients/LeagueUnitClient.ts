import ax from 'axios';
import { parseAxiosResponse } from '../Client';
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import { TeamHatstatsChart } from '../models/team/TeamHatstats';
import { TeamCardsChart } from '../models/team/TeamCards';
import { TeamSalaryTSIChart } from '../models/team/TeamSalaryTSI';
import { TeamRatingChart } from '../models/team/TeamRating';
import { TeamAgeInjuryChart } from '../models/team/TeamAgeInjury';
import { TeamPowerRatingChart } from '../models/team/TeamPowerRating';
import { TeamFanclubFlagsChart } from '../models/team/TeamFanclubFlags';
import { TeamStreakTrophiesChart } from '../models/team/TeamStreakTrophies';

const axios = ax.create({ baseURL: import.meta.env.VITE_HATTID_SERVER_URL })

export interface RequestParams {
    season: number,
    playedInLastMatch?: boolean,
    excludeZeroTsi?: boolean,
}

function chart<T>(endpointName: string): (leagueUnitId: number, requestParams: RequestParams, callback: (loadingEnum: LoadingEnum, t?: T[]) => void) => void {
	return (leagueUnitId, requestParams, callback) => {
		axios.get<T[]>(`/api/leagueUnit/${leagueUnitId}/${endpointName}?season=${requestParams.season}`)
			.then(response => parseAxiosResponse(response, callback))
			.catch(_e => callback(LoadingEnum.ERROR))
	}
}

export const teamHatstatsChart = chart<TeamHatstatsChart>("teamHatstatsChart")
export const teamCardsChart = chart<TeamCardsChart>("teamCardsChart")
export const teamRatingsChart = chart<TeamRatingChart>("teamRatingsChart")
export const teamAgeInjuriesChart = chart<TeamAgeInjuryChart>("teamAgeInjuriesChart")
export const teamPowerRatingsChart = chart<TeamPowerRatingChart>("teamPowerRatingsChart")
export const teamFanclubFlagsChart = chart<TeamFanclubFlagsChart>("teamFanclubFlagsChart")
export const teamStreakTrophiesChart = chart<TeamStreakTrophiesChart>("teamStreakTrophiesChart")

export async function teamSalaryTsiChart(leagueUnitId: number, requestParams: RequestParams, callback: (loadingEnum: LoadingEnum, teamSalaryTSIChart?: TeamSalaryTSIChart[]) => void) {
	try {
        const response = await axios.get<TeamSalaryTSIChart[]>(`/api/leagueUnit/${leagueUnitId}/teamSalaryTsiChart?season=${requestParams.season}&playedInLastMatch=${requestParams.playedInLastMatch}&excludeZeroTsi=${requestParams.excludeZeroTsi}`);
        return parseAxiosResponse(response, callback);
    } catch (_e) {
        return callback(LoadingEnum.ERROR);
    }
}
