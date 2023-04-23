import React from 'react'
import { useTranslation } from 'react-i18next'
import { getPlayerRatings } from '../../../rest/clients/PlayerStatsClient'
import PlayerRating from '../../../rest/models/player/PlayerRating'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const PlayerRatingsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

     return <TableSection<LevelProps, PlayerRating>
         levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getPlayerRatings(props.levelDataProps.createLevelRequest(), request.statisticsParameters, request.playerParameters, callback)}
        defaultSortingField='rating'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYER_ROLES, SelectorsEnum.NATIONALITIES_SELECTOR,
                SelectorsEnum.AGE_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        pageEnum={PagesEnum.PLAYER_RATINGS}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.player(pr => pr.playerSortingKey, props.levelDataProps.countriesMap()),
            TableColumns.teamTableColumn(pr => pr.playerSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pr => pr.playerSortingKey),
            TableColumns.role(pr => pr.role),
            TableColumns.ageTableColumn(pr => pr.age, 'age'),
            TableColumns.ratings(pr => pr.rating, t('table.rating'), 'rating'),
            TableColumns.ratings(pr => pr.ratingEndOfMatch, t('table.rating_end_of_match'), 'rating_end_of_match'),
        ]}
     />
}

export default PlayerRatingsTable
