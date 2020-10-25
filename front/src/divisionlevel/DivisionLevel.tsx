import React from 'react';
import { RouteComponentProps } from 'react-router';
import { getDivisionLevelData, getLeagueUnitIdByName } from '../rest/Client' 
import DivisionLevelData from '../rest/models/DivisionLevelData';
import DivisionLevelTopMenu from './DivisionLevelTopMenu'
import { PagesEnum } from '../common/enums/PagesEnum';
import ModelTableDivisionLevelProps from './ModelTableDivisionLevelProps'
import DivisionLevelTeamHatstats from './DivisionLevelTeamHatstats';
import DivisionLevelLeagueUnits from './DivisionLevelLeagueUnits'
import PageLayout from '../common/PageLayout';
import DivisionLevelPlayerGoalGames from './DivisionLevelPlayerGoalGames'
import DivisionLevelPlayerCards from './DivisionLevelPlayerCards'
import DivisionLevelPlayerSalaryTsi from './DivisionLevelPlayerSalaryTsi'
import DivisionLevelPlayerRatings from './DivisionLevelPlayerRatings'
import DivisionLevelPlayerInjuries from './DivisionLevelPlayerInjuries'

interface MatchParams {
    leagueId: string,
    divisionLevel: string
}

interface Props extends RouteComponentProps<MatchParams>{}

class DivisionLevel extends PageLayout<Props, DivisionLevelData, ModelTableDivisionLevelProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: ModelTableDivisionLevelProps) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS,
            props => <DivisionLevelTeamHatstats modelTableProps={props}/>)
        pagesMap.set(PagesEnum.LEAGUE_UNITS,
            props => <DivisionLevelLeagueUnits modelTableProps={props}/>)
        pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
            props => <DivisionLevelPlayerGoalGames modelTableProps={props}/>) 
        pagesMap.set(PagesEnum.PLAYER_CARDS,
            props => <DivisionLevelPlayerCards modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
            props => <DivisionLevelPlayerSalaryTsi modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS,
            props => <DivisionLevelPlayerRatings modelTableProps={props} />)
        pagesMap.set(PagesEnum.PLAYER_INJURIES,
            props => <DivisionLevelPlayerInjuries modelTableProps={props} />)
        super(props, pagesMap)

        this.leagueUnitSelected=this.leagueUnitSelected.bind(this)
    }

    fetchLevelData(props: Props, callback: (data: DivisionLevelData) => void): void {
        getDivisionLevelData(Number(this.props.match.params.leagueId), Number(this.props.match.params.divisionLevel) ,
        callback)
    }

    makeModelProps(levelData: DivisionLevelData): ModelTableDivisionLevelProps {
        return new ModelTableDivisionLevelProps(levelData);
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