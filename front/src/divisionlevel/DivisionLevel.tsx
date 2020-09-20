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
import { ModelTableProps } from '../common/ModelTable';

interface MatchParams {
    leagueId: string,
    divisionLevel: string
}

interface Props extends RouteComponentProps<MatchParams>{}

class DivisionLevel extends PageLayout<Props, DivisionLevelData> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: ModelTableProps<DivisionLevelData>) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS,
            props => <DivisionLevelTeamHatstats modelTableProps={props}/>)
        pagesMap.set(PagesEnum.LEAGUE_UNITS,
            props => <DivisionLevelLeagueUnits modelTableProps={props}/>)
        super(props, pagesMap)

        this.leagueUnitSelected=this.leagueUnitSelected.bind(this)
    }

    fetchLevelData(props: Props, callback: (data: DivisionLevelData) => void): void {
        getDivisionLevelData(Number(this.props.match.params.leagueId), Number(this.props.match.params.divisionLevel) ,
        callback)
    }

    makeModelProps(levelData: DivisionLevelData): ModelTableProps<DivisionLevelData> {
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