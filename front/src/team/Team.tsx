import React from 'react'
import { RouteComponentProps } from 'react-router';
import PageLayout from '../common/PageLayout';
import TeamData from '../rest/models/TeamData';
import ModelTableTeamProps from './ModelTableTeamProps';
import { getTeamData } from '../rest/Client'
import { PagesEnum } from '../common/enums/PagesEnum';
import TeamTopMenu from './TeamTopMenu'
import TeamPlayerStats from './TeamPlayerStats'
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

class Team extends PageLayout<Props, TeamData, ModelTableTeamProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: ModelTableTeamProps) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_OVERVIEW, props => <>
                    <NearestMatchesTable modelTableProps={props}/>
                    <TeamRankingsTable modelTableProps={props}/>
                </>)
        pagesMap.set(PagesEnum.PLAYER_STATS, 
            props => <TeamPlayerStats modelTableProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, 
            props => <TeamPlayerGoalGames modelTableProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_CARDS, 
            props => <TeamPlayerCards modelTableProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, 
            props => <TeamPlayerSalaryTsi modelTableProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            props => <TeamPlayerRatings modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES, 
            props => <TeamPlayerInjuries modelTableProps={props} />)
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, 
            props => <TeamMatchTopHatstats modelTableProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING, 
            props => <TeamMatchSurprising modelTableProps={props} />)
        pagesMap.set(PagesEnum.MATCH_SPECTATORS, 
            props => <TeamMatchSpectators modelTableProps={props} />)

        super(props, pagesMap)
    }


    makeModelProps(levelData: TeamData): ModelTableTeamProps {
        return new ModelTableTeamProps(levelData)
    }

    fetchLevelData(props: Props, callback: (data: TeamData) => void): void {
        getTeamData(Number(this.props.match.params.teamId), callback)
    }
    topMenu(): JSX.Element {
        return <TeamTopMenu teamData={this.state.levelData}/>
    }

}

export default Team