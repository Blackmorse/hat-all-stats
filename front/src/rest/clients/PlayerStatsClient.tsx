import { playersRequest, statisticsRequest } from '../Client';
import PlayerCards from '../models/player/PlayerCards';
import PlayerGoalGames from '../models/player/PlayerGoalsGames';
import PlayerInjury from '../models/player/PlayerInjury';
import PlayerRating from '../models/player/PlayerRating';
import PlayerSalaryTSI from '../models/player/PlayerSalaryTSI';

export const getPlayerSalaryTsi = playersRequest<PlayerSalaryTSI>('playerTsiSalary')

export const getPlayerCards = playersRequest<PlayerCards>('playerCards')

export const getPlayerGoalsGames = playersRequest<PlayerGoalGames>('playerGoalGames')

export const getPlayerInjuries = statisticsRequest<PlayerInjury>('playerInjuries')

export const getPlayerRatings = playersRequest<PlayerRating>('playerRatings')
