import React from 'react';
import { Link } from 'react-router-dom';
import LeagueData from '../rest/models/LeagueData'
import '../common/menu/TopMenu.css'
import { toArabian } from "../common/Utils"

interface Props {
    leagueData?: LeagueData,
    callback: (divisionLevel: number) => void
}

class LeagueTopMenu extends React.Component<Props> {    

  onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
    this.props.callback(toArabian(event.currentTarget.value))
  }
  
  render() {
      return <div className="header_inner">
        <Link to={"/league/" + this.props.leagueData?.leagueId} className="header_link">{this.props.leagueData?.leagueName}</Link>
        &#8674;

        <select className="href_select" onChange={this.onChanged}>
          <option value={undefined}>Select...</option>
          {this.props.leagueData?.divisionLevels.map(divisionLevel => {
            return <option value={divisionLevel} key={'division_level_select_' + divisionLevel}>{divisionLevel}</option>}
          )}
        </select>

        <img src="/logo.png" className="logo" alt="AlltidLike"/>          
    </div>
  }
}

export default LeagueTopMenu