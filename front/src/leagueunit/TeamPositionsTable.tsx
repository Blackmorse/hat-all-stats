import { type JSX } from 'react'
import { useTranslation } from 'react-i18next'
import { LoadingEnum } from '../common/enums/LoadingEnum'
import { PagesEnum } from '../common/enums/PagesEnum'
import { LevelDataPropsWrapper } from '../common/LevelDataProps'
import TeamLink from '../common/links/TeamLink'
import TableSection, { Request } from '../common/tables/TableSection'
import { SelectorsEnum } from '../common/tables/SelectorsEnum'
import TableColumns from '../common/tables/TableColumns'
import { getTeamPositions } from '../rest/Client'
import RestTableData from '../rest/models/RestTableData'
import { StatsTypeEnum } from '../rest/models/StatisticsParameters'
import LeagueUnitTeamStatsWithPositionDiff from '../rest/models/team/LeagueUnitTeamStat'
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps'

const TeamPositionsTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
    const [ t, _i18n ] = useTranslation()
    const requestF = (request: Request, callback: (loadingEnum: LoadingEnum, result?: RestTableData<LeagueUnitTeamStatsWithPositionDiff>) => void) => {
        getTeamPositions(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)
    }

    const trend = (teamPositionWithDiff: LeagueUnitTeamStatsWithPositionDiff) => {
        let trend: JSX.Element = <img src="/trend-gray.png" alt="same" />
        if(teamPositionWithDiff.positionDiff < 0) {
            trend = <img className="trend_up" src="/trend-green.png" alt="up" />
        } else if (teamPositionWithDiff.positionDiff > 0) {
            trend = <img className="trend_down" src="/trend-red.png" alt="down" />
        }
        return trend
    }

    return <TableSection<LeagueUnitLevelDataProps, LeagueUnitTeamStatsWithPositionDiff>
        levelProps={props.levelDataProps}
        requestFunc={requestF}
        defaultSortingField='points'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        pageEnum={PagesEnum.TEAM_HATSTATS}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            { columnHeader: { title: '' },
              columnValue:  { provider: t => trend(t)} 
            },
            { columnHeader: {title: t('table.team') },
              columnValue: { provider: t => <TeamLink
                  id={t.leagueUnitTeamStat.teamId}
                  text={t.leagueUnitTeamStat.teamName}
              />}
            },
            TableColumns.simpleNumber(t => t.leagueUnitTeamStat.games, {title: t('table.games_abbr'), poppedHint: t('table.games')}),
            TableColumns.simpleNumber(t => t.leagueUnitTeamStat.win, {title: t('table.win_abbr'), poppedHint: t('table.win')}, 'win'),
            TableColumns.simpleNumber(t => t.leagueUnitTeamStat.draw, {title: t('table.draw_abbr'), poppedHint: t('table.draw')}, 'draw'),
            TableColumns.simpleNumber(t => t.leagueUnitTeamStat.lost, {title: t('table.lose_abbr'), poppedHint: t('table.lose')}, 'lost'),
            TableColumns.simpleNumber(t => t.leagueUnitTeamStat.scored, {title: t('table.goals_for_abbr'), poppedHint: t('table.goals_for')}, 'scored'),
            TableColumns.simpleNumber(t => t.leagueUnitTeamStat.missed, {title: t('table.goals_against_abbr'), poppedHint: t('table.goals_againts')}, 'missed'),
            TableColumns.simpleNumber(t => t.leagueUnitTeamStat.points, {title: t('table.points')}, 'points'),
        ]}
    />
}

export default TeamPositionsTable
