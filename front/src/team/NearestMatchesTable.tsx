import React from 'react';
import NearestMatch, { NearestMatches } from '../rest/models/NearestMatch'
import './NearestMatchesTable.css'
import '../i18n'
import { Translation } from 'react-i18next'
import { ModelTablePropsWrapper } from '../common/ModelTable'
import TeamData from '../rest/models/TeamData';
import ModelTableTeamProps from './ModelTableTeamProps';
import TeamRequest from '../rest/models/request/TeamRequest';
import { getNearestMatches } from '../rest/Client'
import moment from 'moment'
interface State {
    nearestMatches?: NearestMatches
}

class NearestMatchesTable extends React.Component<ModelTablePropsWrapper<TeamData, ModelTableTeamProps>, State> {

    constructor(props: ModelTablePropsWrapper<TeamData, ModelTableTeamProps>) {
        super(props)
        this.state= {}
    }

    componentDidMount() {
        let request: TeamRequest = {
            type: 'TeamRequest',
            teamId: this.props.modelTableProps.teamId()
        }
        getNearestMatches(request, nearestMatches => this.setState({nearestMatches: nearestMatches}))
    }

    matchTableRow(nearestMatch: NearestMatch): JSX.Element {
        let result: string
        if(nearestMatch.status === "FINISHED") {
            result = nearestMatch.homeGoals + " : " + nearestMatch.awayGoals 
        } else {
            result = "-:-"
        }
        return <tr key={"nearest_match_" + nearestMatch.matchId}>
            <td className="matches_date">{moment(nearestMatch.matchDate).format('DD.MM.YYYY')}</td>
            <td className="matches_team">{nearestMatch.homeTeamName}</td>
            <td className="matches_result">{result}</td>
            <td className="matches_team">{nearestMatch.awayTeamName}</td>
        </tr>
    }

    render() {
        let playedMatches: JSX.Element
        let upcomingMatches: JSX.Element
        if(!this.state.nearestMatches) {
            playedMatches = <></>
            upcomingMatches = <></>
        } else {
            playedMatches = <>{this.state.nearestMatches?.playedMatches.map(this.matchTableRow)}</>
            upcomingMatches = <>{this.state.nearestMatches?.upcomingMatches.map(this.matchTableRow)}</>
        }        


        return  <Translation>
            {(t, { i18n}) => <div className="section_row">
            <div className="section_row_half_element">
                <section className="statistics_section">
                    <header className="statistics_header">
                        <span className="statistics_header_triangle">&#x25BC; {t("matches.played_matches")}</span>
                    </header>

                    <div className="statistics_section_inner">
                        <table className="statistics_table">
                            <tbody>
                                {playedMatches}
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
            <div className="section_row_half_element">
                <section className="statistics_section">
                    <header className="statistics_header">
                        <span className="statistics_header_triangle">&#x25BC; {t("matches.upcoming_matches")}</span>
                    </header>

                    <div className="statistics_section_inner">
                        <table className="statistics_table">
                            <tbody>
                                {upcomingMatches}
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    }
    </Translation>
    }
}

export default NearestMatchesTable