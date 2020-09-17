import React from 'react';
import { RouteComponentProps } from 'react-router';
import { getDivisionLevelData } from '../rest/Client' 
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
    }

    makeModelProps(levelData: DivisionLevelData): ModelTableProps<DivisionLevelData> {
        return new ModelTableDivisionLevelProps(levelData);
    }

    componentDidMount() {
        const oldState = this.state
        getDivisionLevelData(Number(this.props.match.params.leagueId), Number(this.props.match.params.divisionLevel),
            divisionLevelData => this.setState({
                levelData: divisionLevelData,
                leaguePage: oldState.leaguePage
            }))
    }

    topMenu(): JSX.Element {
        return <DivisionLevelTopMenu divisionLevelData={this.state.levelData} />
    }
}

export default DivisionLevel