import { RouteComponentProps } from 'react-router';

import React from 'react';
import Layout from '../Layout';
import TopMenu from '../menu/TopMenu';
import LeftMenu from '../menu/LeftMenu'

interface MatchParams {
    leagueId: string;
}

interface Props extends RouteComponentProps<MatchParams> {
}
  

class League extends Layout<Props, {}> {
  
    constructor(props: Props) {
        super(props)
        this.state = {}
    }    
    
    topMenu(): JSX.Element {
        // return new TopMenu({leagueId: Number(this.props.match.params.leagueId)})
        return <TopMenu leagueId={Number(this.props.match.params.leagueId)}/>
    }

    leftMenu() {
        return <LeftMenu />
    }

    content() {
      return <span></span>
    }
}

export default League;