import React from 'react'
import { RouteComponentProps } from 'react-router';
import LeagueUnitData from '../rest/models/LeagueUnitData';
import PageLayout from '../common/PageLayout';
import ModelTableLeagueUnitProps from './ModelTableLeagueUnitProps'
import LeagueUnitTopMenu from './LeagueUnitTopMenu';
import { getLeagueUnitData } from '../rest/Client'
import { PagesEnum } from '../common/enums/PagesEnum';
import LeagueUnitTeamHatstats from './LeagueUnitTeamHatstats'
import TeamPositionsTable from './TeamPositionsTable';
import LeagueUnitPlayerGoalGames from './LeagueUnitPlayerGoalGames'
import LeagueUnitPlayerCards from './LeagueUnitPlayerCards'
import LeagueUnitPlayerSalaryTsi from './LeagueUnitPlayerSalaryTsi'
import LeagueUnitPlayerRatings from './LeagueUnitPlayerRatings'
import LeagueUnitPlayerInjuries from './LeagueUnitPlayerInjuries'
import LeagueUnitTeamSalaryTSI from './LeagueUnitTeamSalaryTSI'
import LeagueUnitTeamCards from './LeagueUnitTeamCards'
import LeagueUnitTeamRatings from './LeagueUnitTeamRatings'
import LeagueUnitTeamAgeInjury from './LeagueUnitTeamAgeInjury'
import LeagueUnitTeamPowerRatings from './LeagueUnitTeamPowerRatings'
import LeagueUnitTeamFanclubFlags from './LeagueUnitTeamFanclubFlags'
import LeagueUnitTeamStreakTrophies from './LeagueUnitTeamStreakTrophies'
import LeagueUnitMatchTopHatstats from './LeagueUnitMatchTopHatstats'
import LeagueUnitMatchSurprising from './LeagueUnitMatchSurprising'
import LeagueUnitMatchSpectators from './LeagueUnitMatchSpectators'

interface MatchParams {
    leagueUnitId: string
}

interface Props extends RouteComponentProps<MatchParams> {}

class LeagueUnit extends PageLayout<Props, LeagueUnitData, ModelTableLeagueUnitProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: ModelTableLeagueUnitProps) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS, 
            props => <>
                <TeamPositionsTable modelTableProps={props} />
                <LeagueUnitTeamHatstats modelTableProps={props}/>
                </>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <LeagueUnitPlayerGoalGames modelTableProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <LeagueUnitPlayerCards modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <LeagueUnitPlayerSalaryTsi modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            props => <LeagueUnitPlayerRatings modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <LeagueUnitPlayerInjuries modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            props => <LeagueUnitTeamSalaryTSI modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            props => <LeagueUnitTeamCards modelTableProps={props} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            props => <LeagueUnitTeamRatings modelTableProps={props} />)  
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            props => <LeagueUnitTeamAgeInjury modelTableProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            props => <LeagueUnitTeamPowerRatings modelTableProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            props => <LeagueUnitTeamFanclubFlags modelTableProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            props => <LeagueUnitTeamStreakTrophies modelTableProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            props => <LeagueUnitMatchTopHatstats modelTableProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            props => <LeagueUnitMatchSurprising modelTableProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            props => <LeagueUnitMatchSpectators modelTableProps={props} />) 
        super(props, pagesMap)

        this.teamIdSelected=this.teamIdSelected.bind(this)
    }

    fetchLevelData(props: Props, callback: (data: LeagueUnitData) => void): void {
        getLeagueUnitData(Number(this.props.match.params.leagueUnitId), callback)
    }

    makeModelProps(levelData: LeagueUnitData): ModelTableLeagueUnitProps {
        return new ModelTableLeagueUnitProps(levelData)
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