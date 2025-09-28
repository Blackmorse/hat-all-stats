import {useTranslation} from 'react-i18next'
import {getTeamGoalPoints} from '../../../rest/Client'
import {StatsTypeEnum} from '../../../rest/models/StatisticsParameters'
import TeamGoalPoints from '../../../rest/models/team/TeamGoalPoints'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, {LevelDataPropsWrapper} from '../../LevelDataProps'
import TableSection from '../TableSection'
import {SelectorsEnum} from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const TeamGoalPointsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <TableSection<LevelProps, TeamGoalPoints>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamGoalPoints(props.levelDataProps.createLevelRequest(), request.statisticsParameters, request.playedAllMatches, request.oneTeamPerUnit, callback)}
        defaultSortingField='points'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYED_ALL_MATCHES_SELECTOR, SelectorsEnum.ONE_TEAM_PER_UNIT_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        pageEnum={PagesEnum.TEAM_GOAL_POINTS}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(tgp => tgp.sortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(tgp => tgp.sortingKey),
            {
                columnHeader: { title: t('table.win_abbr'), poppedHint: t('table.win'), sortingField: 'won', center: true },
                columnValue: { provider: (tgp) => tgp.won.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.lose_abbr'), poppedHint: t('table.lose'), sortingField: 'lost', center: true },
                columnValue: { provider: (tgp) => tgp.lost.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.draw_abbr'), poppedHint: t('table.draw'), sortingField: 'draw', center: true },
                columnValue: { provider: tgp => tgp.draw.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.goals_for_abbr'), poppedHint: t('table.goals_for'), sortingField: 'goals_for', center: true },
                columnValue: { provider: tgp => tgp.goalsFor.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.goals_against_abbr'), poppedHint: t('table.goals_against'), sortingField: 'goals_against', center: true },
                columnValue: { provider: tgp => tgp.goalsAgaints.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.goals_difference'), sortingField: 'goals_difference', center: true }, 
                columnValue: {
                    provider: tgp => tgp.goalsDifference.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.points_abbr'), poppedHint: t('table.points'), sortingField: 'points', center: true },
                columnValue: { provider: tgp => tgp.points.toString() , center: true } 
            }
        ]}
    />
}

export default TeamGoalPointsTable
