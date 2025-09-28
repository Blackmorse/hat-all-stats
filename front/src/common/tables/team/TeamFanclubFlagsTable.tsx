import { useTranslation } from 'react-i18next'
import { getTeamFanclubFlags } from '../../../rest/clients/TeamStatsClient'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import TeamFanclubFlags from '../../../rest/models/team/TeamFanclubFlags'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const TeamFanclubFlagsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <TableSection<LevelProps, TeamFanclubFlags>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamFanclubFlags(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='fanclub_size'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        pageEnum={PagesEnum.TEAM_FANCLUB_FLAGS}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(tff => tff.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(tff => tff.teamSortingKey),
            TableColumns.simpleNumber(tff => tff.fanclubSize, { title: t('table.fanclub_size') }, 'fanclub_size'),
            TableColumns.simpleNumber(tff => tff.homeFlags, { title: t('table.home_flags') }, 'home_flags'),
            TableColumns.simpleNumber(tff => tff.awayFlags, { title: t('table.away_flags') }, 'away_flags'),
            TableColumns.simpleNumber(tff => tff.allFlags, { title: t('table.all_flags') }, 'all_flags')
        ]}
    />
}

export default TeamFanclubFlagsTable
