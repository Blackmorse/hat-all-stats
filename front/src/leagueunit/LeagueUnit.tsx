import React from 'react'
import { RouteComponentProps } from 'react-router';
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps'
import LeagueUnitTopMenu from './LeagueUnitTopMenu';
import { getLeagueUnitData } from '../rest/Client'
import { PagesEnum } from '../common/enums/PagesEnum';
import TeamPositionsTable from './TeamPositionsTable';
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import PlayerGoalGamesTable from '../common/tables/player/PlayerGoalsGamesTable'
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable';
import TeamCardsTable from '../common/tables/team/TeamCardsTable';
import TeamRatingsTable from '../common/tables/team/TeamRatingsTable';
import TeamAgeInjuryTable from '../common/tables/team/TeamAgeInjuryTable';
import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable';
import TeamFanclubFlagsTable from '../common/tables/team/TeamFanclubFlagsTable';
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import QueryParams from '../common/QueryParams';
import PromotionsTable from '../common/pages/PromotionsTable';
import DreamTeamPage from '../common/pages/DreamTeamPage';


interface MatchParams {
    leagueUnitId: string
}

interface Props extends RouteComponentProps<MatchParams> {}

class LeagueUnit extends CountryLevelLayout<Props, LeagueUnitData, LeagueUnitLevelDataProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: LeagueUnitLevelDataProps, queryParams: QueryParams) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS, 
            (props, queryParams) => <>
                <TeamPositionsTable levelDataProps={props} queryParams={queryParams} />
                <TeamHatstatsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams}/>
                </>)
        pagesMap.set(PagesEnum.DREAM_TEAM,
            (props, queryParams) => <DreamTeamPage<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            (props, queryParams) => <PlayerGoalGamesTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            (props, queryParams) => <PlayerCardsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            (props, queryParams) => <PlayerSalaryTsiTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            (props, queryParams) => <PlayerRatingsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            (props, queryParams) => <PlayerInjuriesTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            (props, queryParams) => <TeamSalaryTSITable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            (props, queryParams) => <TeamCardsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            (props, queryParams) => <TeamRatingsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)  
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            (props, queryParams) => <TeamAgeInjuryTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            (props, queryParams) => <TeamPowerRatingsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            (props, queryParams) => <TeamFanclubFlagsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            (props, queryParams) => <TeamStreakTrophiesTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            (props, queryParams) => <MatchTopHatstatsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            (props, queryParams) => <MatchSurprisingTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            (props, queryParams) => <MatchSpectatorsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
        
        pagesMap.set(PagesEnum.PROMOTIONS,
            (props, queryParams) => <PromotionsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
           
        
        super(props, pagesMap)

        this.teamIdSelected=this.teamIdSelected.bind(this)
    }

    documentTitle(data: LeagueUnitData): string {
        return data.leagueUnitName
    }

    fetchLevelData(props: Props, 
            callback: (data: LeagueUnitData) => void,
            onError: () => void): void {
        getLeagueUnitData(Number(this.props.match.params.leagueUnitId), callback, onError)
    }

    makeModelProps(levelData: LeagueUnitData): LeagueUnitLevelDataProps {
        return new LeagueUnitLevelDataProps(levelData)
    }

    teamIdSelected(teamId: number) {
        this.props.history.push('/team/' + teamId)
    }

    topMenu(): JSX.Element {
        return <LeagueUnitTopMenu leagueUnitData={this.state.levelData}
          callback={this.teamIdSelected} />
    }

}

export default LeagueUnit