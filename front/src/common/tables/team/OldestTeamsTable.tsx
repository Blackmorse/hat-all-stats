import React from 'react'
import LevelDataProps, { LevelDataPropsWrapper } from "../../LevelDataProps";
import { useTranslation } from 'react-i18next';
import TableSection from '../TableSection';
import OldestTeam from '../../../rest/models/team/OldestTeam'
import { getOldestTeams } from '../../../rest/Client'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { PagesEnum } from '../../enums/PagesEnum';
import { SelectorsEnum } from '../SelectorsEnum';
import TableColumns from '../TableColumns';
import { dateFormatter } from '../../Formatters';


const OldestTeamsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()
    
    return <TableSection<LevelProps, OldestTeam>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getOldestTeams(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='founded_date'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}        
        pageEnum={PagesEnum.OLDEST_TEAMS}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(ot => ot.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(ot => ot.teamSortingKey),
            {
                columnHeader: { title: t('team.date_of_foundation'), sortingField: 'founded_date' },
                columnValue:  { provider: ot => dateFormatter(ot.foundedDate), center: true }
            }
        ]}
    />
}

export default OldestTeamsTable
