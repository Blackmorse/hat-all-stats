import React from 'react';
import { NearestMatches } from '../../rest/models/match/NearestMatch'
import '../../i18n'
import { Translation } from 'react-i18next'
import Section from '../../common/sections/Section';
import NearestMatchesTable from './NearestMatchesTable'
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import ExecutableComponent from '../../common/sections/ExecutableComponent';
import TeamRequest from '../../rest/models/request/TeamRequest';
import { getNearestMatches } from '../../rest/Client'
import { Row, Col } from 'react-bootstrap';


interface Props {
    teamId: number
}

interface State {
    nearestMatches?: NearestMatches,
}

class PlayedAndUpcomingMatchesTable extends ExecutableComponent<Props, State, NearestMatches, TeamRequest> {
    constructor(props: Props) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {
                type: 'TeamRequest',
                teamId: this.props.teamId
            }
        }
    }

    executeDataRequest(dataRequest: TeamRequest, callback: (loadingState: LoadingEnum, result?: NearestMatches) => void): void {
        getNearestMatches(dataRequest, callback)
    }

    stateFromResult(result?: NearestMatches): State {
        return {
            nearestMatches: result
        }
    }

    renderSection() {
        const PlayedNearestMatches = Section(NearestMatchesTable, _ => 'matches.played_matches')
        const UpcomingNearestMatches = Section(NearestMatchesTable, _ => 'matches.upcoming_matches')

        if (this.state.nearestMatches === undefined) {
            return <></>
        }
        let nearestMatches = this.state.nearestMatches
        return  <Translation>
            {(t, { i18n}) => <Row>
                <Col lg={6} className='my-1'>
                    <PlayedNearestMatches nearestMatches={nearestMatches.playedMatches} />
                </Col>
                <Col lg={6} className='my-1'>
                    <UpcomingNearestMatches nearestMatches={nearestMatches.upcomingMatches} />
                </Col>
            </Row>
    }
    </Translation>
    }
}

export default PlayedAndUpcomingMatchesTable