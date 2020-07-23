import React from 'react';
import {getTeamRatings} from '../rest/Client';
import TeamRating from '../rest/models/TeamRating';
import './TeamHatstats.css'

interface State {
    teamRatings?: Array<TeamRating>
}

interface Props {
    leagueId: number
}

class TeamHatstats extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state={}
    }

   componentDidMount() {
        let leagueId = this.props.leagueId
        getTeamRatings(leagueId, teamRatings => this.setState({teamRatings: teamRatings}))
   } 

   render() {
       return <section className="statistics_section">
           <header className="statistics_header"><span className="statistics_header_triangle">&#x25BC;</span></header>
           <table className="statistics_table">
            <tr>
                <th className="position hint" popped-hint="table.position">table.position_abbr)</th>
                <th>table.team</th>
                <th className="value">table.league</th>
                <th className="value">table.hatstats</th>
                <th className="value">table.midfield</th>
                <th className="value">table.defense</th>
                <th className="value">table.attack</th>
            </tr>
                <tbody>
                {this.state.teamRatings?.map((teamRating, index) => {
                    return <tr>
                            <td>{index + 1}</td>
                            <td><a className="table_link" href="/#">{teamRating.teamName}</a></td>
                            <td className="value"><a className="table_link" href="/#">{teamRating.leagueUnitName}</a></td>
                            <td className="value">{teamRating.hatStats}</td>
                            <td className="value">{teamRating.midfield * 3}</td>
                            <td className="value">{teamRating.defense}</td>
                            <td className="value">{teamRating.attack}</td>
                    </tr>
                })}
                </tbody>
           </table>
       </section>
   }
}

export default TeamHatstats