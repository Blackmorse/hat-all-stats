import React from 'react'
import { getTeamCards } from '../../../rest/Client'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import TeamCards from '../../../rest/models/team/TeamCards'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const TeamCardsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    return <TableSection<LevelProps, TeamCards>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamCards(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='yellow_cards'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        pageEnum={PagesEnum.TEAM_CARDS}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(tc => tc.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(tc => tc.teamSortingKey),
            TableColumns.yellowCards(tc => tc.yellowCards, 'yellow_cards'),
            TableColumns.redCards(tc => tc.redCards, 'red_cards')
        ]}
    />
}

export default TeamCardsTable
