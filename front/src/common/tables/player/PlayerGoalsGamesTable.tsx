import { useTranslation } from 'react-i18next'
import { getPlayerGoalsGames } from '../../../rest/clients/PlayerStatsClient'
import PlayerGoalGames from '../../../rest/models/player/PlayerGoalsGames'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const PlayerGoalsGamesTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <TableSection<LevelProps, PlayerGoalGames>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getPlayerGoalsGames(props.levelDataProps.createLevelRequest(), request.statisticsParameters, request.playerParameters, callback)}
        defaultSortingField='scored'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYER_ROLES, SelectorsEnum.NATIONALITIES_SELECTOR,
                SelectorsEnum.AGE_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        pageEnum={PagesEnum.PLAYER_GOAL_GAMES}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.player(pgg => pgg.playerSortingKey, props.levelDataProps.countriesMap()),
            TableColumns.teamTableColumn(pgg => pgg.playerSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pgg => pgg.playerSortingKey),
            TableColumns.role(pgg => pgg.role),
            TableColumns.ageTableColumn(pgg => pgg.age, 'age'),
            TableColumns.simpleNumber(pgg => pgg.games, { title: t('table.games_abbr'), poppedHint: t('table.games') }, 'games'),
            TableColumns.simpleNumber(pgg => pgg.playedMinutes, { title: t('table.minutes_abbr'), poppedHint: t('table.minutes') }, 'played'),
            TableColumns.simpleNumber(pgg => pgg.scored, { title: t('table.scored') }, 'scored'),
            {
                columnHeader: { title: t('table.minutes_per_goal_abbr'), sortingField: 'goal_rate', poppedHint: t('table.minutes_per_goal'), center: true },
                columnValue: { provider: (pgg) => Math.floor(pgg.goalRate).toString(), center: true }
            }
        ]}
    />
}

export default PlayerGoalsGamesTable
