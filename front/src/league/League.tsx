import { RouteComponentProps } from 'react-router';
import React from 'react';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout'
import TopMenu from './LeagueTopMenu';
import LeagueLeagueUnits from './LeagueLeagueUnits'
import { getLeagueData } from '../rest/Client';
import LeagueData from '../rest/models/leveldata/LeagueData';
import { PagesEnum } from '../common/enums/PagesEnum'
import LeagueTeamHatstats from './LeagueTeamHatstats'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import LevelDataProps from '../common/LevelDataProps';
import LeaguePlayerGoalGames from './LeaguePlayerGoalGames'
import LeaguePlayerCards from './LeaguePlayerCards'
import LeaguePlayerSalaryTsiTable from './LeaguePlayerSalaryTsiTable'
import LeaguePlayerRatings from './LeaguePlayerRatings'
import LeaguePlayerInjuries from './LeaguePlayerInjuries'
import LeagueTeamSalaryTSI from './LeagueTeamSalaryTSI'
import LeagueTeamCards from './LeagueTeamCards'
import LeagueTeamRatings from './LeagueTeamRatings'
import LeagueTeamAgeInjury from './LeagueTeamAgeInjury'
import LeagueTeamGoalPoints from './LeagueTeamGoalPoints'
import LeagueTeamPowerRatings from './LeagueTeamPowerRatings'
import LeagueTeamFanclubFlags from './LeagueTeamFanclubFlags'
import LeagueTeamStreakTrophies from './LeagueTeamStreakTrophies'
import LeagueMatchTopHatstats from './LeagueMatchTopHatstats'
import LeagueMatchSurprising from './LeagueMatchSurprising'
import LeagueMatchSpectators from './LeagueMatchSpectators'
import LeagueOverviewPage from './LeagueOverviewPage';

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
            props => <LeagueTeamHatstats levelDataProps={props} />)
        pagesMap.set(PagesEnum.LEAGUE_UNITS,
            props => <LeagueLeagueUnits levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <LeaguePlayerGoalGames levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <LeaguePlayerCards levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <LeaguePlayerSalaryTsiTable levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS, 
            props => <LeaguePlayerRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <LeaguePlayerInjuries levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            props => <LeagueTeamSalaryTSI levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            props => <LeagueTeamCards levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            props => <LeagueTeamRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            props => <LeagueTeamAgeInjury levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_GOAL_POINTS,
            props => <LeagueTeamGoalPoints levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            props => <LeagueTeamPowerRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            props => <LeagueTeamFanclubFlags levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            props => <LeagueTeamStreakTrophies levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            props => <LeagueMatchTopHatstats levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            props => <LeagueMatchSurprising levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            props => <LeagueMatchSpectators levelDataProps={props} />)
        
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