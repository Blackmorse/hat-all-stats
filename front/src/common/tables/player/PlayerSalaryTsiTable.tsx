import React from 'react'
import {useTranslation} from 'react-i18next'
import {getPlayerSalaryTsi} from "../../../rest/Client"
import PlayerSalaryTSI from "../../../rest/models/player/PlayerSalaryTSI"
import {StatsTypeEnum} from "../../../rest/models/StatisticsParameters"
import Mappings from '../../enums/Mappings'
import {ageFormatter, commasSeparated, salaryFormatter} from '../../Formatters'
import LevelDataProps, {LevelDataPropsWrapper} from "../../LevelDataProps"
import PlayerLink from '../../links/PlayerLink'
import HookAbstractTableSection from "../HookAbstractTableSection"
import {SelectorsEnum} from "../SelectorsEnum"
import TableColumns from '../TableColumns'


const PlayerSalaryTsiTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <HookAbstractTableSection<LevelProps, PlayerSalaryTSI>
        levelProps={props.levelDataProps}
        queryParams={props.queryParams}
        requestFunc={(request, callback) => getPlayerSalaryTsi(props.levelDataProps.createLevelRequest(), request.statisticsParameters, request.playerParameters, callback)}
        defaultSortingField='tsi'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYER_ROLES, SelectorsEnum.NATIONALITIES_SELECTOR,
                SelectorsEnum.AGE_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            {
                columnHeader: {title: t('table.player')},
                columnValue: {
                    provider: (pst) => <PlayerLink 
                                           id={pst.sortingKey.playerId}
                                           text={pst.sortingKey.firstName + ' ' + pst.sortingKey.lastName}
                                           nationality={pst.sortingKey.nationality}
                                           countriesMap={props.levelDataProps.countriesMap()}
                                           externalLink
                                        />
                }
            },
            TableColumns.teamTableColumn(pst => pst.sortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pst => pst.sortingKey),
            {
                columnHeader: {title: ''},
                columnValue: {
                    provider: (pst) => t(Mappings.roleToTranslationMap.get(pst.role) || '')
                }
            },
            {
                columnHeader: {
                    title: t('table.age'), sortingField: 'age'
                },
                columnValue: {
                    provider: (pst) => ageFormatter(pst.age), center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.tsi'), sortingField: 'tsi'
                },
                columnValue: {
                    provider: (pst) => commasSeparated(pst.tsi), center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.salary') + ', ' + props.levelDataProps.currency(), sortingField: 'salary'
                },
                columnValue: {
                    provider: (pst) => salaryFormatter(pst.salary, props.levelDataProps.currencyRate()), center: true
                }
            }
        ]}
    />
}

export default PlayerSalaryTsiTable
