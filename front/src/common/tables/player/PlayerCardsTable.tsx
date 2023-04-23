import React from 'react'
import { useTranslation } from 'react-i18next'
import { getPlayerCards } from '../../../rest/clients/PlayerStatsClient'
import PlayerCards from '../../../rest/models/player/PlayerCards'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const PlayerCardsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
const [ t, _i18n ] = useTranslation()

return <TableSection<LevelProps, PlayerCards>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getPlayerCards(props.levelDataProps.createLevelRequest(), request.statisticsParameters, request.playerParameters, callback)}
        defaultSortingField='yellow_cards'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYER_ROLES, SelectorsEnum.NATIONALITIES_SELECTOR,
                SelectorsEnum.AGE_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        pageEnum={PagesEnum.PLAYER_CARDS}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(pc => pc.playerSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pc => pc.playerSortingKey),
            TableColumns.role(pc => pc.role),
            TableColumns.ageTableColumn(pc => pc.age, 'age'),
            TableColumns.simpleNumber(pc => pc.games, t('table.games')),
            TableColumns.yellowCards(pc => pc.yellowCards, 'yellow_cards'),
            TableColumns.redCards(pc => pc.redCards, 'red_cards')
        ]}
    />
}

export default PlayerCardsTable
