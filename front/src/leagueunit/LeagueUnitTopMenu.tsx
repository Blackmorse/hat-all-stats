import React from 'react'
import LeagueUnitData from '../rest/models/LeagueUnitData'
import '../common/menu/TopMenu.css'
import { Link } from 'react-router-dom';

interface Props {
    leagueUnitData?: LeagueUnitData
}

class LeagueUnitTopMenu extends React.Component<Props> {
    render() {
        return <div className="header_inner">
            <Link to={"/league/" + this.props.leagueUnitData?.leagueId} className="header_link">{this.props.leagueUnitData?.leagueName}</Link>
            &#8674;
            <Link className="header_link" 
                to={"/league/" + this.props.leagueUnitData?.leagueId + "/divisionLevel/" + this.props.leagueUnitData?.divisionLevel}>
                    {this.props.leagueUnitData?.divisionLevelName}
            </Link>
            &#8674;
            <Link className="header_link" 
                    to={"/leagueUnit/" + this.props.leagueUnitData?.leagueUnitId}>
                {this.props.leagueUnitData?.leagueUnitName}  
            </Link>
            &#8674;
            <select className="href_select">
                <option>Select...</option>
                {this.props.leagueUnitData?.teams.map(([teamId, teamName]) => {
                    return <option>{teamName}</option>
                })}
            </select>
        </div>
    }
}

export default LeagueUnitTopMenu