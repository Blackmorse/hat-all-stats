import React from 'react'
import {useTranslation} from 'react-i18next'
import {getTeamGoalPoints} from '../../../rest/Client'
import {StatsTypeEnum} from '../../../rest/models/StatisticsParameters'
import TeamGoalPoints from '../../../rest/models/team/TeamGoalPoints'
import LevelDataProps, {LevelDataPropsWrapper} from '../../LevelDataProps'
import LeagueUnitLink from '../../links/LeagueUnitLink'
import TeamLink from '../../links/TeamLink'
import HookAbstractTableSection from '../HookAbstractTableSection'
import {SelectorsEnum} from '../SelectorsEnum'

const TeamGoalPointsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <HookAbstractTableSection<LevelProps, TeamGoalPoints>
        levelProps={props.levelDataProps}
        queryParams={props.queryParams}
        requestFunc={(request, callback) => getTeamGoalPoints(props.levelDataProps.createLevelRequest(), request.statisticsParameters, request.playedAllMatches, request.oneTeamPerUnit, callback)}
        defaultSortingField='points'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYED_ALL_MATCHES_SELECTOR, SelectorsEnum.ONE_TEAM_PER_UNIT_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        tableColumns={[
            {
                columnHeader: {
                    title: t('table.position_abbr'), poppedHint: t('table.position')
                },
                columnValue: {
                    provider: (_tgp, index) => (index + 1).toString()
                }
            },
            {
                columnHeader: {
                    title: t('table.team')
                },
                columnValue: {
                    provider: (tgp) => <TeamLink id={tgp.teamSortingKey.teamId} text={tgp.teamSortingKey.teamName}/>
                }
            }, 
            {
                columnHeader: {
                    title: t('table.league'), center: true
                },
                columnValue: {
                    provider: (tgp) => <LeagueUnitLink id={tgp.teamSortingKey.leagueUnitId} text={tgp.teamSortingKey.leagueUnitName}/>, center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.win_abbr'), poppedHint: t('table.win'), sortingField: 'won', center: true
                },
                columnValue: {
                    provider: (tgp) => tgp.won.toString(), center: true
            }
            },
            {
                columnHeader: {
                    title: t('table.lose_abbr'), poppedHint: t('table.lose'), sortingField: 'lost', center: true
                },
                columnValue: {
                    provider: (tgp) => tgp.lost.toString(), center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.draw_abbr'), poppedHint: t('table.draw'), sortingField: 'draw', center: true
                },
                columnValue: {
                    provider: tgp => tgp.draw.toString(), center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.goals_for_abbr'), poppedHint: t('table.goals_for'), sortingField: 'goals_for', center: true
                },
                columnValue: {
                    provider: tgp => tgp.goalsFor.toString(), center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.goals_against_abbr'), poppedHint: t('table.goals_against'), sortingField: 'goals_against', center: true
                },
                columnValue: {
                    provider: tgp => tgp.goalsAgaints.toString(), center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.goals_difference'), sortingField: 'goals_difference', center: true
                },
                columnValue: {
                    provider: tgp => tgp.goalsDifference.toString(), center: true
                }
            },
            {
                columnHeader: {
                    title: t('table.points_abbr'), poppedHint: t('table.points'), sortingField: 'point', center: true
                },
                columnValue: {
                    provider: tgp => tgp.points.toString() , center: true
                } 
            }
        ]}
    />
}

export default TeamGoalPointsTable
