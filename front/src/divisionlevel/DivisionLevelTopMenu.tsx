import React from 'react'
import { Link } from 'react-router-dom';
import DivisionLevelData from '../rest/models/DivisionLevelData'
import '../common/menu/TopMenu.css'


interface Props {
    divisionLevelData?: DivisionLevelData
}

class DivisionLevelTopMenu extends React.Component<Props> {
    render() {
        return <div className="header_inner">
          <Link to={"/league/" + this.props.divisionLevelData?.leagueId} className="header_link">{this.props.divisionLevelData?.leagueName}</Link>
          &#8674;
          <Link className="header_link" 
            to={"/league/" + this.props.divisionLevelData?.leagueId + "/divisionLevel/" + this.props.divisionLevelData?.divisionLevel}>
              {this.props.divisionLevelData?.divisionLevelName}
          </Link>
          &#8674;

          <select className="href_select">
              <option>Select...</option>
              {Array.from(Array(this.props.divisionLevelData?.leagueUnitsNumber), (_, i) => i + 1).map(leagueUnit => {
                  return <option>{leagueUnit}</option>
              })}
          </select>

          <img src="/logo.png" className="logo" alt="AlltidLike"/> 
        </div>
    }
}

export default DivisionLevelTopMenu