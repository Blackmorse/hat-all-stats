import React from 'react'
import { LoadingEnum } from '../../common/enums/LoadingEnum'
import SimilarMatchesStats from '../../rest/models/match/SimilarMatchesStats'
import { getSimilarMatchesByRatings } from '../../rest/Client'
import ExecutableComponent from '../../common/sections/ExecutableComponent'
import { SectionState } from '../../common/sections/Section'
import './MatchSimulatorInfo.css'
import i18n from '../../i18n'
import SingleMatch from '../../rest/models/match/SingleMatch'

interface Props {
    singleMatch: SingleMatch
}

interface State {
    similarMatchesStats?: SimilarMatchesStats
}

class MatchSimulatorInfo extends ExecutableComponent<Props, State & SectionState, SimilarMatchesStats, SingleMatch> {
    
    constructor(props: Props) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: props.singleMatch,
            collapsed: false
        }
    }

    componentDidMount() {}

    executeDataRequest(dataRequest: SingleMatch, 
            callback: (loadingState: LoadingEnum, result?: SimilarMatchesStats) => void): void {
                getSimilarMatchesByRatings(dataRequest, 0.1, callback)
    }
    stateFromResult(result?: SimilarMatchesStats): State & SectionState {
        return {
            similarMatchesStats: result,
            collapsed: this.state.collapsed
        }
    }

    renderSection() {
        if (this.state.similarMatchesStats === undefined) {
            return <button onClick={() => this.update()}>{i18n.t('team.simulate_match')}</button>
        }

        let stats = this.state.similarMatchesStats

        let homeRate = Math.floor(100 * stats.wins / (stats.wins + stats.draws + stats.loses))
        let drawRate = Math.floor(100 * stats.draws / (stats.wins + stats.draws + stats.loses))
        let awayRate = Math.floor(100 * stats.loses / (stats.wins + stats.draws + stats.loses))
        if (stats.wins + stats.draws + stats.loses === 0) {
            homeRate = 33
            drawRate = 33
            awayRate = 33
        }

        return <div className="simulator_span">
            <div className="simulator_results">
                <span className="home_team_span result_span" style={{width: homeRate.toString() + '%'}}>
                    {this.state.similarMatchesStats.wins}
                </span> 
                <span className="draw_span result_span" style={{width: drawRate.toString() + '%'}}>
                    {this.state.similarMatchesStats.draws}
                </span>
                <span className="away_team_span result_span" style={{width: awayRate.toString() + '%'}}>
                    {this.state.similarMatchesStats.loses}
                </span>
            </div>
            <div className="simulator_goals">
                <span className="home_goals">
                    {Math.round(stats.avgGoalsFor * 10) / 10}
                </span>
                <span className="goals_title">
                    {i18n.t('overview.goals')}
                </span>
                <span className="away_goals">
                    {Math.round(stats.avgGoalsAgainst * 10) / 10}
                </span>
            </div>
        </div>
    }
}

export default MatchSimulatorInfo