import React from 'react'
import { LoadingEnum } from '../../common/enums/LoadingEnum'
import SimilarMatchesStats from '../../rest/models/match/SimilarMatchesStats'
import { getSimilarMatchesByRatings } from '../../rest/Client'
import ExecutableComponent from '../../common/sections/ExecutableComponent'
import { SectionState } from '../../common/sections/Section'
import './MatchSimulatorInfo.css'
import i18n from '../../i18n'
import SingleMatch from '../../rest/models/match/SingleMatch'
import { Col, Container, Row } from 'react-bootstrap'

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
            return <button className='btn btn-success' onClick={() => this.update()}>{i18n.t('team.simulate_match')}</button>
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

        return <div className='w-75'>
            <Container className='d-flex justify-content-center'>
                <span className='home_team_span result_span' style={{width: homeRate.toString() + '%'}}>
                    {this.state.similarMatchesStats.wins}
                </span> 
                <span className='draw_span result_span' style={{width: drawRate.toString() + '%'}}>
                    {this.state.similarMatchesStats.draws}
                </span>
                <span className='away_team_span result_span' style={{width: awayRate.toString() + '%'}}>
                    {this.state.similarMatchesStats.loses}
                </span>
            </Container>
            <Container className='mt-2'>
                <Row className='px-3'>
                   <Col lg={4}>{Math.round(stats.avgGoalsFor * 10) / 10}</Col> 
                   <Col className='text-center' lg={4}>{i18n.t('overview.goals')}</Col> 
                   <Col className='text-end' lg={4}>{Math.round(stats.avgGoalsAgainst * 10) / 10}</Col> 
                </Row>
            </Container>
        </div>
    }
}

export default MatchSimulatorInfo