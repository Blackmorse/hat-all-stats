import { RouteComponentProps } from 'react-router';

import React from 'react';
import Layout from '../Layout';
import TopMenu from '../menu/TopMenu';
import LeftMenu from '../menu/LeftMenu'
import TeamHatstats from './TeamHatstats';
import LeagueUnits from './LeagueUnits'

enum LeaguePage {
    TEAM_HATSTATS,
    LEAGUE_UNITS
}

interface State {
    page: LeaguePage;
}

interface MatchParams {
    leagueId: string;
}

interface Props extends RouteComponentProps<MatchParams> {
}
  

export class League extends Layout<Props, State> {
  
    constructor(props: Props) {
        super(props)
        this.state = {page: LeaguePage.TEAM_HATSTATS}
    }    
    
    topMenu(): JSX.Element {
        return <TopMenu leagueId={Number(this.props.match.params.leagueId)}/>
    }

    leftMenu() {
        return <LeftMenu callback={(leaguePage) => this.setState({page: leaguePage})}/>
    }

    content() {
      let ret: JSX.Element;
      if (this.state.page === LeaguePage.TEAM_HATSTATS) {
          ret = <TeamHatstats leagueId={Number(this.props.match.params.leagueId)} />
      } else //if (this.state.page === LeaguePage.LEAGUE_UNITS) {
        {  
        ret = <LeagueUnits leagueId={Number(this.props.match.params.leagueId)} />
      }
      return ret;
    }
}

export default LeaguePage;

export interface LeagueProps {
    leagueId: number
}

