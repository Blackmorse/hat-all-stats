import React from 'react';
import { RouteComponentProps } from 'react-router';
import { getDivisionLevelData, getLeagueUnitIdByName } from '../rest/Client' 
import DivisionLevelData from '../rest/models/leveldata/DivisionLevelData';
import DivisionLevelTopMenu from './DivisionLevelTopMenu'
import { PagesEnum } from '../common/enums/PagesEnum';
import DivisionLevelDataProps from './DivisionLevelDataProps'
import DivisionLevelTeamHatstats from './DivisionLevelTeamHatstats';
import DivisionLevelLeagueUnits from './DivisionLevelLeagueUnits'
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import DivisionLevelPlayerGoalGames from './DivisionLevelPlayerGoalGames'
import DivisionLevelPlayerCards from './DivisionLevelPlayerCards'
import DivisionLevelPlayerSalaryTsi from './DivisionLevelPlayerSalaryTsi'
import DivisionLevelPlayerRatings from './DivisionLevelPlayerRatings'
import DivisionLevelPlayerInjuries from './DivisionLevelPlayerInjuries'
import DivisionLevelTeamSalaryTSI from './DivisionLevelTeamSalaryTSI'
import DivisionLevelTeamCards from './DivisionLevelTeamCards'
import DivisionLevelTeamRatings from './DivisionLevelTeamRatings'
import DivisionLevelTeamAgeInjury from './DivisionLevelTeamAgeInjury'
import DivisionLevelTeamGoalPoints from './DivisionLevelTeamGoalPoints'
import DivisionLevelTeamPowerRatings from './DivisionLevelTeamPowerRatings'
import DivisionLevelTeamFanclubFlags from './DivisionLevelTeamFanclubFlags'
import DivisionLevelTeamStreakTrophies from './DivisionLevelTeamStreakTrophies'
import DivisionLevelMatchTopHatstats from './DivisionLevelMatchTopHatstats'
import DivisionLevelMatchSurprising from './DivisionLevelMatchSurprising'
import DivisionLevelMatchSpectators from './DivisionLevelMatchSpectators'

interface MatchParams {
    leagueId: string,
    divisionLevel: string
}

interface Props extends RouteComponentProps<MatchParams>{}

class DivisionLevel extends CountryLevelLayout<Props, DivisionLevelData, DivisionLevelDataProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: DivisionLevelDataProps) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS,
            props => <DivisionLevelTeamHatstats levelDataProps={props}/>)
        pagesMap.set(PagesEnum.LEAGUE_UNITS,
            props => <DivisionLevelLeagueUnits levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <DivisionLevelPlayerGoalGames levelDataProps={props}/>) 
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <DivisionLevelPlayerCards levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <DivisionLevelPlayerSalaryTsi levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            props => <DivisionLevelPlayerRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <DivisionLevelPlayerInjuries levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            props => <DivisionLevelTeamSalaryTSI levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            props => <DivisionLevelTeamCards levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            props => <DivisionLevelTeamRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            props => <DivisionLevelTeamAgeInjury levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_GOAL_POINTS,
            props => <DivisionLevelTeamGoalPoints levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            props => <DivisionLevelTeamPowerRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            props => <DivisionLevelTeamFanclubFlags levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            props => <DivisionLevelTeamStreakTrophies levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            props => <DivisionLevelMatchTopHatstats levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            props => <DivisionLevelMatchSurprising levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            props => <DivisionLevelMatchSpectators levelDataProps={props} />)
        super(props, pagesMap)

        this.leagueUnitSelected=this.leagueUnitSelected.bind(this)
    }

    fetchLevelData(props: Props, callback: (data: DivisionLevelData) => void): void {
        getDivisionLevelData(Number(this.props.match.params.leagueId), Number(this.props.match.params.divisionLevel) ,
        callback)
    }

    makeModelProps(levelData: DivisionLevelData): DivisionLevelDataProps {
        return new DivisionLevelDataProps(levelData);
    }

    leagueUnitSelected(leagueUnitName: string) {
        getLeagueUnitIdByName(Number(this.props.match.params.leagueId), leagueUnitName, id => {
            this.props.history.push('/leagueUnit/' + id)
        })
    }

    topMenu(): JSX.Element {
        return <DivisionLevelTopMenu divisionLevelData={this.state.levelData} 
            callback={this.leagueUnitSelected}/>
    }
}

export default DivisionLevel