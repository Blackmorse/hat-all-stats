import React from 'react'
import { RouteComponentProps } from 'react-router';
import LeagueUnitData from '../rest/models/LeagueUnitData';
import PageLayout from '../common/PageLayout';
import LevelData from '../rest/models/LevelData';
import { ModelTableProps } from '../common/ModelTable';
import ModelTableLeagueUnitProps from './ModelTableLeagueUnitProps'
import LeagueUnitTopMenu from './LeagueUnitTopMenu';
import { getLeagueUnitData } from '../rest/Client'
import { PagesEnum } from '../common/enums/PagesEnum';
import LeagueUnitTeamHatstats from './LeagueUnitTeamHatstats'

interface MatchParams {
    leagueUnitId: string
}

interface Props extends RouteComponentProps<MatchParams> {}

class LeagueUnit extends PageLayout<Props, LeagueUnitData> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: ModelTableProps<LeagueUnitData>) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS, 
            props => <LeagueUnitTeamHatstats modelTableProps={props}/>)
        super(props, pagesMap)
    }

    fetchLevelData(props: Props, callback: (data: LeagueUnitData) => void): void {
        getLeagueUnitData(Number(this.props.match.params.leagueUnitId), callback)
    }

    makeModelProps(levelData: LeagueUnitData): ModelTableProps<LevelData> {
        return new ModelTableLeagueUnitProps(levelData)
    }

    topMenu(): JSX.Element {
        return <LeagueUnitTopMenu leagueUnitData={this.state.levelData}/>
    }

}

export default LeagueUnit