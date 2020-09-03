import { RouteComponentProps } from 'react-router';

import React from 'react';
import Layout from '../Layout';
import TopMenu from './LeagueTopMenu';
import LeftMenu from '../menu/LeftMenu'
import TeamHatstats from './TeamHatstats';
import LeagueUnits from './LeagueUnits'
import { getLeagueData } from '../rest/Client';
import LeagueData from '../rest/models/LeagueData';

enum LeaguePage {
    TEAM_HATSTATS,
    LEAGUE_UNITS
}

interface State {
    leaguePage: LeaguePage
    leagueData?: LeagueData
}

interface MatchParams {
    leagueId: string;
}

interface Props extends RouteComponentProps<MatchParams> {
}
  

export class League extends Layout<Props, State> {
  
    constructor(props: Props) {
        super(props)
        this.state = {leaguePage: LeaguePage.TEAM_HATSTATS}
    }    

    componentDidMount() {
        const oldState = this.state
        getLeagueData(Number(this.props.match.params.leagueId), leagueData => 
            this.setState({
                leaguePage: oldState.leaguePage,
                leagueData: leagueData
            }))
    }
    
    topMenu(): JSX.Element {
        return <TopMenu leagueData={this.state.leagueData}
            callback={divisionLevel => {this.props.history.push('/league/' + this.state.leagueData?.leagueId + '/divisionLevel/' + divisionLevel)}}/>
    }

    leftMenu() {
        return <LeftMenu callback={(leaguePage) => this.setState({leaguePage: leaguePage})}/>
    }

    content() {
      let ret: JSX.Element;
      if (this.state.leagueData) {
        if (this.state.leaguePage === LeaguePage.TEAM_HATSTATS) {
            ret = <TeamHatstats leagueData={this.state.leagueData} />
        } else //if (this.state.page === LeaguePage.LEAGUE_UNITS) {
        {  
            ret = <LeagueUnits leagueData={this.state.leagueData} />
        }
      } else {
          ret = <></>
      }
      return ret;
    }
}

export default LeaguePage;

export interface LeagueProps {
    leagueData: LeagueData
}

