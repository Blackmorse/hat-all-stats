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