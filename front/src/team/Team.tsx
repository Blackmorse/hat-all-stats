import React from 'react'
import { RouteComponentProps } from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import TeamData from '../rest/models/leveldata/TeamData';
import TeamLevelDataProps from './TeamLevelDataProps';
import { getTeamData } from '../rest/Client'
import { PagesEnum } from '../common/enums/PagesEnum';
import TeamTopMenu from './TeamTopMenu'
import TeamRankingsTable from './TeamRankingsTable'
import NearestMatchesTable from './NearestMatchesTable'
import TeamPlayerCards from './TeamPlayerCards'
import TeamPlayerGoalGames from './TeamPlayerGoalGames'
import TeamPlayerSalaryTsi from './TeamPlayerSalaryTsi'
import TeamPlayerRatings from './TeamPlayerRatings'
import TeamPlayerInjuries from './TeamPlayerInjuries'
import TeamMatchTopHatstats from './TeamMatchTopHatstats'
import TeamMatchSurprising from './TeamMatchSurprising'
import TeamMatchSpectators from './TeamMatchSpectators'

interface MatchParams {
    teamId: string
}

interface Props extends RouteComponentProps<MatchParams> {}

class Team extends CountryLevelLayout<Props, TeamData, TeamLevelDataProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: TeamLevelDataProps) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_OVERVIEW, props => <>
                    <NearestMatchesTable levelDataProps={props}/>
                    <TeamRankingsTable levelDataProps={props}/>
                </>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, 
            props => <TeamPlayerGoalGames levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS, 
            props => <TeamPlayerCards levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, 
            props => <TeamPlayerSalaryTsi levelDataProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            props => <TeamPlayerRatings levelDataProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES, 
            props => <TeamPlayerInjuries levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, 
            props => <TeamMatchTopHatstats levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING, 
            props => <TeamMatchSurprising levelDataProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SPECTATORS, 
            props => <TeamMatchSpectators levelDataProps={props} />)

        super(props, pagesMap)
    }


    makeModelProps(levelData: TeamData): TeamLevelDataProps {
        return new TeamLevelDataProps(levelData)
    }

    fetchLevelData(props: Props, callback: (data: TeamData) => void): void {
        getTeamData(Number(this.props.match.params.teamId), callback)
    }
    topMenu(): JSX.Element {
        return <TeamTopMenu teamData={this.state.levelData}/>
    }

}

export default Team