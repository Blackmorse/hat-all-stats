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
import { LoadingEnum } from '../common/enums/LoadingEnum';
import ExternalMatchLink from '../common/links/ExternalMatchLink';
import StatisticsSection from '../common/sections/StatisticsSection';

interface State {
    nearestMatches?: NearestMatches,
    loadingState: LoadingEnum
}

class NearestMatchesTable extends React.Component<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State> {

    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props)
        this.state = {loadingState: LoadingEnum.OK}
        this.componentDidMount=this.componentDidMount.bind(this)
        this.updateCurrent=this.updateCurrent.bind(this)
        this.thiss=this.thiss.bind(this)
    }

    updateCurrent() {
        let teamId = this.props.levelDataProps.teamId()
        let request: TeamRequest = {
            type: 'TeamRequest',
            teamId: teamId,
        }
        this.setState({nearestMatches: this.state.nearestMatches,
            loadingState: LoadingEnum.LOADING})
        getNearestMatches(request, 
            nearestMatches => this.setState({nearestMatches: nearestMatches, 
                loadingState: LoadingEnum.OK
            }),
            () => this.setState({
                loadingState: LoadingEnum.ERROR
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

        return <tr key={"nearest_match_" + nearestMatch.matchId}>
            <td className="matches_date">{moment(nearestMatch.matchDate).format('DD.MM.YYYY')}</td>
            <td className="matches_team"><TeamLink text={nearestMatch.homeTeamName} id={nearestMatch.homeTeamId} forceRefresh={true}/></td>
            <td className="matches_result">{result} <ExternalMatchLink id={nearestMatch.matchId} /></td>
            <td className="matches_team"><TeamLink text={nearestMatch.awayTeamName} id={nearestMatch.awayTeamId} forceRefresh={true}/></td>
        </tr>
    }

    private thiss() {
        return this
    }

    render() {
        let playedMatches: JSX.Element
        let upcomingMatches: JSX.Element
        if(!this.state.nearestMatches || this.state.loadingState === LoadingEnum.ERROR) {
            playedMatches = <Blur loadingState={this.state.loadingState} updateCallback={this.updateCurrent}/>
            upcomingMatches = <Blur loadingState={this.state.loadingState} updateCallback={this.updateCurrent}/>
        } else {
            let th = this
            playedMatches = new  (class extends StatisticsSection {
                renderContent() {
                    return (
                    <div className="statistics_section_inner">
                        <table className="statistics_table">
                            <tbody>{th.state.nearestMatches?.playedMatches.map(th.matchTableRow)}</tbody>
                        </table>
                    </div>
                    )
                }
            })({}, 'matches.played_matches').render()
            upcomingMatches = new (class extends StatisticsSection {
                renderContent() {
                    return <div className="statistics_section_inner">
                    <table className="statistics_table">
                        <tbody>{th.state.nearestMatches?.upcomingMatches.map(th.matchTableRow)}</tbody>
                    </table>
                </div>
                }
            })({}, 'matches.upcoming_matches').render()
        }        


        return  <Translation>
            {(t, { i18n}) => <div className="section_row">
            <div className="section_row_half_element">
                {playedMatches}
            </div>
            <div className="section_row_half_element">
                {upcomingMatches}
            </div>
        </div>
    }
    </Translation>
    }
}

export default NearestMatchesTable