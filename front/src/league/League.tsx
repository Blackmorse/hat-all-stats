import { RouteComponentProps } from 'react-router';
import React from 'react';
import PageLayout from '../common/PageLayout'
import TopMenu from './LeagueTopMenu';
import LeagueLeagueUnits from './LeagueLeagueUnits'
import { getLeagueData } from '../rest/Client';
import LeagueData from '../rest/models/LeagueData';
import { PagesEnum } from '../common/enums/PagesEnum'
import LeagueTeamHatstats from './LeagueTeamHatstats'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import { ModelTableProps } from '../common/ModelTable';
import LeaguePlayerGoalGames from './LeaguePlayerGoalGames'
import LeaguePlayerCards from './LeaguePlayerCards'
import LeaguePlayerSalaryTsiTable from './LeaguePlayerSalaryTsiTable'
import LeaguePlayerRatings from './LeaguePlayerRatings'

interface MatchParams {
    leagueId: string;
}

interface Props extends RouteComponentProps<MatchParams> {}

class League extends PageLayout<Props, LeagueData, ModelTableLeagueProps> {
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