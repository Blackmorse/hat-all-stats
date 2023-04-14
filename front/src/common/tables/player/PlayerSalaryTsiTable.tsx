import React from 'react'
import {useTranslation} from 'react-i18next'
import { getPlayerSalaryTsi } from '../../../rest/clients/PlayerStatsClient'
import PlayerSalaryTSI from "../../../rest/models/player/PlayerSalaryTSI"
import {StatsTypeEnum} from "../../../rest/models/StatisticsParameters"
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, {LevelDataPropsWrapper} from "../../LevelDataProps"
import HookAbstractTableSection from "../HookAbstractTableSection"
import {SelectorsEnum} from "../SelectorsEnum"
import TableColumns from '../TableColumns'


const PlayerSalaryTsiTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <HookAbstractTableSection<LevelProps, PlayerSalaryTSI>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getPlayerSalaryTsi(props.levelDataProps.createLevelRequest(), request.statisticsParameters, request.playerParameters, callback)}
        defaultSortingField='tsi'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYER_ROLES, SelectorsEnum.NATIONALITIES_SELECTOR,
                SelectorsEnum.AGE_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        pageEnum={PagesEnum.PLAYER_SALARY_TSI}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.player(pst => pst.sortingKey, props.levelDataProps.countriesMap()),
            TableColumns.teamTableColumn(pst => pst.sortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pst => pst.sortingKey),
            TableColumns.role(pst => pst.role),
            TableColumns.ageTableColumn(pst => pst.age, 'age'),
            TableColumns.tsi(pst => pst.tsi, t('table.tsi'), 'tsi'),
            TableColumns.salary(pst => pst.salary, props.levelDataProps.currencyRate(), t('table.salary') + ', ' + props.levelDataProps.currency(), 'salary')
        ]}
    />
}

export default PlayerSalaryTsiTable
