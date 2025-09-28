import { useTranslation } from 'react-i18next'
import { getTeamRatings } from '../../../rest/Client'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import TeamRating from '../../../rest/models/team/TeamRating'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const TeamRatingsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <TableSection<LevelProps, TeamRating>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamRatings(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='rating'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        pageEnum={PagesEnum.TEAM_RATINGS}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(tr => tr.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(tr => tr.teamSortingKey),
            TableColumns.ratings(tr => tr.rating, t('table.rating'), 'rating'),
            TableColumns.ratings(tr => tr.ratingEndOfMatch, t('table.rating_end_of_match'), 'rating_end_of_match')
        ]}
    />
}

export default TeamRatingsTable
