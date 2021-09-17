import React from 'react'
import { LoadingEnum } from '../../common/enums/LoadingEnum'
import SimilarMatchesStats from '../../rest/models/match/SimilarMatchesStats'
import { getSimilarMatchesStats } from '../../rest/Client'
import ExecutableComponent, { LoadableState } from '../../common/sections/ExecutableComponent'
import { SectionState } from '../../common/sections/Section'
import './MatchSimulatorInfo.css'
import i18n from '../../i18n'

interface Props {
    matchId: number
}

interface State {
    similarMatchesStats?: SimilarMatchesStats
}

class MatchSimulatorInfo extends ExecutableComponent<Props, State, SimilarMatchesStats, number, 
    LoadableState<State, number> & SectionState> {
    
    constructor(props: Props) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: props.matchId,
            state: {},
            collapsed: false
        }
    }

    componentDidMount() {}

    executeDataRequest(dataRequest: number, 
            callback: (loadingState: LoadingEnum, result?: SimilarMatchesStats) => void): void {
        getSimilarMatchesStats(dataRequest, 0.1, callback)
    }
    stateFromResult(result?: SimilarMatchesStats): State {
        return {similarMatchesStats: result}
    }

    renderSection() {
        if (this.state.state.similarMatchesStats === undefined) {
            return <button onClick={() => this.update()}>{i18n.t('match.check_result')}</button>
        }

        let stats = this.state.state.similarMatchesStats

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
                    {this.state.state.similarMatchesStats.wins}
                </span> 
                <span className="draw_span result_span" style={{width: drawRate.toString() + '%'}}>
                    {this.state.state.similarMatchesStats.draws}
                </span>
                <span className="away_team_span result_span" style={{width: awayRate.toString() + '%'}}>
                    {this.state.state.similarMatchesStats.loses}
                </span>
            </div>
            <div className="simulator_goals">
                <span className="home_goals">
                    {Math.round(stats.avgGoalsFor * 10) / 10}
                </span>
                <span className="goals_title">
                    Goals
                </span>
                <span className="away_goals">
                    {Math.round(stats.avgGoalsAgainst * 10) / 10}
                </span>
            </div>
        </div>
    }
}

export default MatchSimulatorInfo