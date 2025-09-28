import TeamHatstats from '../../../rest/models/team/TeamHatstats';
import LevelDataProps, { LevelDataPropsWrapper } from "../../LevelDataProps";
import TableSection from '../TableSection';
import { getTeamHatstats } from '../../../rest/clients/TeamStatsClient';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { SelectorsEnum } from '../SelectorsEnum';
import TableColumns from '../TableColumns';
import { useTranslation } from 'react-i18next';
import { loddarStats } from '../../Formatters';
import { PagesEnum } from '../../enums/PagesEnum';

const TeamHatstatsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()
    
    return <TableSection<LevelProps, TeamHatstats>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamHatstats(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='hatstats'
        defaultStatsType={{statType: StatsTypeEnum.MAX}}
        pageEnum={PagesEnum.TEAM_HATSTATS}
        statsTypes={[StatsTypeEnum.AVG, StatsTypeEnum.MAX, StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
            SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(ths => ths.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(ths => ths.teamSortingKey),
            {
                columnHeader: { title: t('table.hatstats'), sortingField: 'hatstats'},
                columnValue:  { provider: ths => ths.hatStats.toString(), center: true}
            },
            {
                columnHeader: { title: t('table.midfield'), sortingField: 'midfield'},
                columnValue:  { provider: ths => (ths.midfield * 3).toString(), center: true }
            },
            {
                columnHeader: { title: t('table.defense'), sortingField: 'defense'},
                columnValue:  { provider: ths => ths.defense.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.attack'), sortingField: 'attack'},
                columnValue:  { provider: ths => ths.attack.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.loddar_stats'), sortingField: 'loddar_stats'},
                columnValue:  { provider: ths => loddarStats(ths.loddarStats), center: true }
            }
        ]}
    />
}

export default TeamHatstatsTable
