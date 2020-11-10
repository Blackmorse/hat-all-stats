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
                <TeamHatstatsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props}/>
                </>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <PlayerGoalGamesTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <PlayerCardsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <PlayerSalaryTsiTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            props => <PlayerRatingsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <PlayerInjuriesTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
            props => <TeamSalaryTSITable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_CARDS,
            props => <TeamCardsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />)
        pagesMap.set(PagesEnum.TEAM_RATINGS,
            props => <TeamRatingsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />)  
        pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
            props => <TeamAgeInjuryTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
            props => <TeamPowerRatingsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
            props => <TeamFanclubFlagsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />) 
        pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
            props => <TeamStreakTrophiesTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
            props => <MatchTopHatstatsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_SURPRISING,
            props => <MatchSurprisingTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />) 
        pagesMap.set(PagesEnum.MATCH_SPECTATORS,
            props => <MatchSpectatorsTable<LeagueUnitData, LeagueUnitLevelDataProps> levelDataProps={props} />) 
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