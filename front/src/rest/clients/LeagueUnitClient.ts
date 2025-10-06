import ax from 'axios';
import { parseAxiosResponse } from '../Client';
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import { TeamHatstatsChart } from '../models/team/TeamHatstats';
import { TeamCardsChart } from '../models/team/TeamCards';

const axios = ax.create({ baseURL: import.meta.env.VITE_HATTID_SERVER_URL })

export function teamHatstatsChart(leagueUnitId: number, season: number,
								 callback: (loadingEnum: LoadingEnum, teamHatstatsChart?: TeamHatstatsChart[]) => void) {
	axios.get<TeamHatstatsChart[]>(`/api/leagueUnit/${leagueUnitId}/teamHatstatsChart?season=${season}`)
		.then(response => parseAxiosResponse(response, callback))
		.catch(_e => callback(LoadingEnum.ERROR))
}

export function teamCardsChart(leagueUnitId: number, season: number,
	callback: (loadingEnum: LoadingEnum, teamCardsChart?: TeamCardsChart[]) => void) {
	axios.get<TeamCardsChart[]>(`/api/leagueUnit/${leagueUnitId}/teamCardsChart?season=${season}`)
		.then(response => parseAxiosResponse(response, callback))
		.catch(_e => callback(LoadingEnum.ERROR))
}
