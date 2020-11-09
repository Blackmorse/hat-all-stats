import React from 'react';
import NearestMatch, { NearestMatches } from '../rest/models/match/NearestMatch'
import './NearestMatchesTable.css'
import '../common/sections/StatisticsSection.css'
import '../i18n'
import { Translation } from 'react-i18next'
import { LevelDataPropsWrapper } from '../common/LevelDataProps'
import TeamData from '../rest/models/leveldata/TeamData';
import TeamLevelDataProps from './TeamLevelDataProps';
import TeamRequest from '../rest/models/request/TeamRequest';
import { getNearestMatches } from '../rest/Client'
import moment from 'moment'
import Blur from '../common/widgets/Blur'
import TeamLink from '../common/links/TeamLink'

interface State {
    nearestMatches?: NearestMatches,
    dataLoading: boolean,
    isError: boolean
}

class NearestMatchesTable extends React.Component<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State> {

    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props)
        this.state = {dataLoading: false, isError: false}
        this.componentDidMount=this.componentDidMount.bind(this)
        this.updateCurrent=this.updateCurrent.bind(this)
    }

    updateCurrent() {
        let teamId = this.props.levelDataProps.teamId()
        let request: TeamRequest = {
            type: 'TeamRequest',
            teamId: teamId,
        }
        this.setState({nearestMatches: this.state.nearestMatches,
                dataLoading: true,
                isError: false})
        getNearestMatches(request, 
            nearestMatches => this.setState({nearestMatches: nearestMatches, 
                dataLoading: false,
                isError: false}),
            () => this.setState({
                dataLoading: false,
                isError: true
            }))
    }

    componentDidMount() {
        this.updateCurrent()
    }


    matchTableRow(nearestMatch: NearestMatch): JSX.Element {
        let result: string
        if(nearestMatch.status === "FINISHED") {
            result = nearestMatch.homeGoals + " : " + nearestMatch.awayGoals 
        } else {
            result = "-:-"
        }

        //**cking workaround. Can't update the page.... 
        let refresh = () => {
            setTimeout( () => {window.location.reload()}, 100)
        }
        return <tr key={"nearest_match_" + nearestMatch.matchId}>
            <td className="matches_date">{moment(nearestMatch.matchDate).format('DD.MM.YYYY')}</td>
            <td className="matches_team"><TeamLink text={nearestMatch.homeTeamName} id={nearestMatch.homeTeamId} callback={() => refresh()}/></td>
            <td className="matches_result">{result}</td>
            <td className="matches_team"><TeamLink text={nearestMatch.awayTeamName} id={nearestMatch.awayTeamId} callback={() => refresh()}/></td>
        </tr>
    }

    render() {
        let playedMatches: JSX.Element
        let upcomingMatches: JSX.Element
        if(!this.state.nearestMatches || this.state.isError) {
            playedMatches = <Blur dataLoading={true} isError={this.state.isError} updateCallback={this.updateCurrent}/>
            upcomingMatches = <Blur dataLoading={true} isError={this.state.isError} updateCallback={this.updateCurrent}/>
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