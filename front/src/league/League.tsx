import { RouteComponentProps } from 'react-router';
import React from 'react';
import CountryLevelLayout from '../common/CountryLevelLayout'
import TopMenu from './LeagueTopMenu';
import LeagueLeagueUnits from './LeagueLeagueUnits'
import { getLeagueData } from '../rest/Client';
import LeagueData from '../rest/models/leveldata/LeagueData';
import { PagesEnum } from '../common/enums/PagesEnum'
import LeagueTeamHatstats from './LeagueTeamHatstats'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import { ModelTableProps } from '../common/ModelTable';
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

interface MatchParams {
    leagueId: string;
}

interface Props extends RouteComponentProps<MatchParams> {}

class League extends CountryLevelLayout<Props, LeagueData, ModelTableLeagueProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: ModelTableLeagueProps) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS, 
            props => <LeagueTeamHatstats modelTableProps={props} />)
        pagesMap.set(PagesEnum.LEAGUE_UNITS,
            props => <LeagueLeagueUnits modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <LeaguePlayerGoalGames modelTableProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <LeaguePlayerCards modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <LeaguePlayerSalaryTsiTable modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS, 
            props => <LeaguePlayerRatings modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <LeaguePlayerInjuries modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            props => <LeagueTeamSalaryTSI modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            props => <LeagueTeamCards modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            props => <LeagueTeamRatings modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            props => <LeagueTeamAgeInjury modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_GOAL_POINTS,
            props => <LeagueTeamGoalPoints modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            props => <LeagueTeamPowerRatings modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            props => <LeagueTeamFanclubFlags modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            props => <LeagueTeamStreakTrophies modelTableProps={props} />)
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            props => <LeagueMatchTopHatstats modelTableProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            props => <LeagueMatchSurprising modelTableProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            props => <LeagueMatchSpectators modelTableProps={props} />)
        super(props, pagesMap)
    }    

    makeModelProps(levelData: LeagueData): ModelTableProps<LeagueData> {
        return new ModelTableLeagueProps(levelData)
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