import React from 'react';
import { Link } from 'react-router-dom';
import LeagueData from './../rest/models/LeagueData'
import './TopMenu.css'



interface Props {
    leagueData?: LeagueData;
}

class TopMenu extends React.Component<Props> {      
    render() {
        return <div className="header_inner">
          <Link to={"/league/" + this.props.leagueData?.leagueId} className="header_link">{this.props.leagueData?.leagueName}</Link>
          &#8674;

          <select className="href_select">
            {this.props.leagueData?.divisionLevels.map(divisionLevel => {
              return <option value={divisionLevel} key={'division_level_select_' + divisionLevel}>{divisionLevel}</option>}
            )}
          </select>

          <img src="/logo.png" className="logo" alt="AlltidLike"/>          
      </div>
    }
}

export default TopMenu