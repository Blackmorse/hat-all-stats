import React from 'react'
import { RouteComponentProps } from 'react-router';
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps'
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

class LeagueUnit extends CountryLevelLayout<Props, LeagueUnitData, LeagueUnitLevelDataProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: LeagueUnitLevelDataProps) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS, 
            props => <>
                <TeamPositionsTable levelDataProps={props} />
                <LeagueUnitTeamHatstats levelDataProps={props}/>
                </>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <LeagueUnitPlayerGoalGames levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <LeagueUnitPlayerCards levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <LeagueUnitPlayerSalaryTsi levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            props => <LeagueUnitPlayerRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <LeagueUnitPlayerInjuries levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            props => <LeagueUnitTeamSalaryTSI levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            props => <LeagueUnitTeamCards levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            props => <LeagueUnitTeamRatings levelDataProps={props} />)  
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            props => <LeagueUnitTeamAgeInjury levelDataProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            props => <LeagueUnitTeamPowerRatings levelDataProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            props => <LeagueUnitTeamFanclubFlags levelDataProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            props => <LeagueUnitTeamStreakTrophies levelDataProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            props => <LeagueUnitMatchTopHatstats levelDataProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            props => <LeagueUnitMatchSurprising levelDataProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            props => <LeagueUnitMatchSpectators levelDataProps={props} />) 
        super(props, pagesMap)

        this.teamIdSelected=this.teamIdSelected.bind(this)
    }

    fetchLevelData(props: Props, callback: (data: LeagueUnitData) => void): void {
        getLeagueUnitData(Number(this.props.match.params.leagueUnitId), callback)
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