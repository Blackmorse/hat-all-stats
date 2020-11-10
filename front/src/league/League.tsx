import { RouteComponentProps } from 'react-router';
import React from 'react';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout'
import TopMenu from './LeagueTopMenu';
import { getLeagueData } from '../rest/Client';
import LeagueData from '../rest/models/leveldata/LeagueData';
import { PagesEnum } from '../common/enums/PagesEnum'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import LevelDataProps from '../common/LevelDataProps';
import LeagueOverviewPage from './LeagueOverviewPage';
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import LeagueUnitsTable from '../common/tables/leagueunit/LeagueUnitsTable';
import PlayerGoalGamesTable from '../common/tables/player/PlayerGoalsGamesTable'
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable';
import TeamCardsTable from '../common/tables/team/TeamCardsTable';
import TeamRatingsTable from '../common/tables/team/TeamRatingsTable';
import TeamAgeInjuryTable from '../common/tables/team/TeamAgeInjuryTable';
import TeamGoalPointsTable from '../common/tables/team/TeamGoalPointsTable';
import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable';
import TeamFanclubFlagsTable from '../common/tables/team/TeamFanclubFlagsTable';
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';

interface MatchParams {
    leagueId: string;
}

interface Props extends RouteComponentProps<MatchParams> {}

class League extends CountryLevelLayout<Props, LeagueData, LeagueLevelDataProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: LeagueLevelDataProps) => JSX.Element>()
        pagesMap.set(PagesEnum.OVERVIEW, 
            props => <LeagueOverviewPage levelDataProps={props} title='' />)
        pagesMap.set(PagesEnum.TEAM_HATSTATS, 
            props => <TeamHatstatsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.LEAGUE_UNITS,
            props => <LeagueUnitsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <PlayerGoalGamesTable<LeagueData, LeagueLevelDataProps> levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <PlayerCardsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <PlayerSalaryTsiTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS, 
            props => <PlayerRatingsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <PlayerInjuriesTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            props => <TeamSalaryTSITable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            props => <TeamCardsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            props => <TeamRatingsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            props => <TeamAgeInjuryTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_GOAL_POINTS,
            props => <TeamGoalPointsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            props => <TeamPowerRatingsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            props => <TeamFanclubFlagsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            props => <TeamStreakTrophiesTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            props => <MatchTopHatstatsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            props => <MatchSurprisingTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            props => <MatchSpectatorsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} />)
        
        super(props, pagesMap)
    }    

    makeModelProps(levelData: LeagueData): LevelDataProps<LeagueData> {
        return new LeagueLevelDataProps(levelData)
    }

    fetchLevelData(props: Props, callback: (data: LeagueData) => void): void {
        getLeagueData(Number(this.props.match.params.leagueId), callback)
    }
    
    topMenu(): JSX.Element {
        return <TopMenu leagueData={this.state.levelData}
            callback={divisionLevel => {this.props.history.push('/league/' + this.state.levelData?.leagueId + '/divisionLevel/' + divisionLevel)}}/>
    }
}

export default League