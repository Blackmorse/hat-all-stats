import { playersRequest, statisticsRequest } from '../Client';
import PlayerCards from '../models/player/PlayerCards';
import PlayerGoalGames from '../models/player/PlayerGoalsGames';
import PlayerInjury from '../models/player/PlayerInjury';
import PlayerRating from '../models/player/PlayerRating';
import PlayerSalaryTSI from '../models/player/PlayerSalaryTSI';

export let getPlayerSalaryTsi = playersRequest<PlayerSalaryTSI>('playerTsiSalary')

export let getPlayerCards = playersRequest<PlayerCards>('playerCards')

export let getPlayerGoalsGames = playersRequest<PlayerGoalGames>('playerGoalGames')

export let getPlayerInjuries = statisticsRequest<PlayerInjury>('playerInjuries')

export let getPlayerRatings = playersRequest<PlayerRating>('playerRatings')
