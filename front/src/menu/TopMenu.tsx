import React from 'react';
import { Link } from 'react-router-dom';
import LeagueData from './../rest/models/LeagueData'
import getLeagueData from '../rest/Client';
import './TopMenu.css'

interface Props {
    leagueId: number
}

interface State {
    leagueData?: LeagueData;
}

class TopMenu extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state = {}
    }

    componentDidMount() {
        let leagueId = this.props.leagueId
        getLeagueData(Number(leagueId), leagueData => this.setState({
          leagueData: leagueData
        }))
    }

      
    render() {
        return <div className="header_inner">
          <Link to={"/league/" + this.props.leagueId} className="header_link">{this.state.leagueData?.leagueName}</Link>
          &#8674;

          <select className="href_select">
            {this.state.leagueData?.divisionLevels.map(divisionLevel => {
              return <option value={divisionLevel}>{divisionLevel}</option>}
            )}
          </select>

          <img src="/logo.png" className="logo" alt="AlltidLike"/>          
      </div>
    }
}

export default TopMenu