import React from 'react'
import ExecutableComponent, { LoadableState } from '../../common/sections/ExecutableComponent';
import SingleMatch from '../../rest/models/match/SingleMatch';
import { combineMatches } from '../../rest/Client'
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import TeamMatchInfo from '../matches/TeamMatchInfo';
import Section, { SectionState } from '../../common/sections/Section';

interface Props {
    firstTeamId: number,
    firstMatchId: number,
    secondTeamId: number,
    secondMatchId: number
}

type Request = Props

interface State {
    singleMatch?: SingleMatch
}

class CombinedMatchesInfo extends ExecutableComponent<Props, State, SingleMatch, Request, LoadableState<State, Request> & SectionState> {
    
    constructor(props: Props) {
        super(props)
        this.state = {
            collapsed: false,
            loadingState: LoadingEnum.OK,
            dataRequest: props,
            state: {}
        }
    }

    executeDataRequest(dataRequest: Props, callback: (loadingState: LoadingEnum, result?: SingleMatch) => void): void {
        combineMatches(dataRequest.firstTeamId, dataRequest.firstMatchId, dataRequest.secondTeamId, dataRequest.secondMatchId, callback)
    }

    stateFromResult(result?: SingleMatch): State {
        return {singleMatch: result}
    }

    renderSection(): JSX.Element {
        if (this.state.state.singleMatch === undefined) {
            return <></>
        }
        return <TeamMatchInfo singleMatch={this.state.state.singleMatch}/>
    }
}

const CombinedMatchesInfoSection = Section(CombinedMatchesInfo, _ => <>! Match Simulation</>)

export default CombinedMatchesInfoSection