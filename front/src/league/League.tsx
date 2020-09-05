import { RouteComponentProps } from 'react-router';

import React from 'react';
import PageLayout from '../common/PageLayout'
import TopMenu from './LeagueTopMenu';
import LeftMenu from '../menu/LeftMenu'
import LeagueLeagueUnits from './LeagueLeagueUnits'
import { getLeagueData } from '../rest/Client';
import LeagueData from '../rest/models/LeagueData';
import { PagesEnum } from '../common/enums/PagesEnum'
import LeagueTeamHatstats from './LeagueTeamHatstats'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import { ModelTableProps } from '../common/ModelTable';

interface MatchParams {
    leagueId: string;
}

interface Props extends RouteComponentProps<MatchParams> {}

export class League extends PageLayout<Props, LeagueData> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: ModelTableProps<LeagueData>) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_HATSTATS, 
            props => <LeagueTeamHatstats modelTableProps={props} />)
        pagesMap.set(PagesEnum.LEAGUE_UNITS,
            props => <LeagueLeagueUnits modelTableProps={props} />)
        super(props, pagesMap)
        this.state = {leaguePage: PagesEnum.TEAM_HATSTATS}
    }    

    makeModelProps(pageData: LeagueData): ModelTableProps<LeagueData> {
        return new ModelTableLeagueProps(pageData)
    }

    componentDidMount() {
        const oldState = this.state
        getLeagueData(Number(this.props.match.params.leagueId), leagueData => 
            this.setState({
                leaguePage: oldState.leaguePage,
                levelData: leagueData
            }))
    }
    
    topMenu(): JSX.Element {
        return <TopMenu leagueData={this.state.levelData}
            callback={divisionLevel => {this.props.history.push('/league/' + this.state.levelData?.leagueId + '/divisionLevel/' + divisionLevel)}}/>
    }

    leftMenu() {
        return <LeftMenu callback={(leaguePage) => this.setState({leaguePage: leaguePage})}/>
    }
}