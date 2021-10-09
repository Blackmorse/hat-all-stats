import React from 'react';
import { NearestMatches } from '../../rest/models/match/NearestMatch'
import './NearestMatchesTable.css'
import '../../common/sections/StatisticsSection.css'
import '../../i18n'
import { Translation } from 'react-i18next'
import Section from '../../common/sections/Section';
import NearestMatchesTable from './NearestMatchesTable'
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import ExecutableComponent, { LoadableState } from '../../common/sections/ExecutableComponent';
import TeamRequest from '../../rest/models/request/TeamRequest';
import { getNearestMatches } from '../../rest/Client'


interface Props {
    teamId: number
}

interface State {
    nearestMatches?: NearestMatches,
}

class PlayedAndUpcomingMatchesTable extends ExecutableComponent<Props, State, NearestMatches, TeamRequest, LoadableState<State, TeamRequest>> {
    constructor(props: Props) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {
                type: 'TeamRequest',
                teamId: this.props.teamId
            },
            state: {}
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

        if (this.state.state.nearestMatches === undefined) {
            return <></>
        }
        let nearestMatches = this.state.state.nearestMatches
        return  <Translation>
            {(t, { i18n}) => <div className="section_row">
            <div className="section_row_half_element">
                <PlayedNearestMatches nearestMatches={nearestMatches.playedMatches} />
            </div>
            <div className="section_row_half_element">
                <UpcomingNearestMatches nearestMatches={nearestMatches.upcomingMatches} />
            </div>
        </div>
    }
    </Translation>
    }
}

export default PlayedAndUpcomingMatchesTable