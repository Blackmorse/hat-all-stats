import React from 'react'
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import SingleMatch from '../../rest/models/match/SingleMatch';
import { getSingleMatch } from '../../rest/Client'
import TeamMatchInfo from './TeamMatchInfo';
import ExecutableComponent, { LoadableState } from '../../common/sections/ExecutableComponent';
import Section, { GSection, SectionState } from '../../common/sections/Section';

interface Props {
    matchId: number
}

interface State {
    singleMatch?: SingleMatch
}

class TeamMatchInfoExecutableSectionBase extends 
    ExecutableComponent<Props, State, SingleMatch, number, LoadableState<State, number> & SectionState> {

    constructor(props: Props) {
        super(props, )
        this.state={
            loadingState: LoadingEnum.OK,
            collapsed: false,
            state: {},
            dataRequest: props.matchId
        }
    }
    
    executeDataRequest(dataRequest: number, callback: (loadingState: LoadingEnum, result?: SingleMatch) => void): void {
        getSingleMatch(dataRequest, callback)
    }

    stateFromResult(result?: SingleMatch): State {
        return {
            singleMatch: result
        }
    }

    renderSection(): JSX.Element {
        if (this.state.state.singleMatch === undefined) {
            return <></>
        }
        return <TeamMatchInfo singleMatch={this.state.state.singleMatch}/>
    }
}

function sectionTitle(props: Props, state: LoadableState<State, number>): JSX.Element {
    if (state.state.singleMatch === undefined) {
        return <></>
    }
    return <>
        {state.state.singleMatch.homeTeamName} - {state.state.singleMatch.awayTeamName}
    </>
}

type TeamMatchSectionState = LoadableState<State, number> & SectionState
const TeamMatchInfoExecutableSection = Section<Props, TeamMatchSectionState, GSection<Props, TeamMatchSectionState>>(
            TeamMatchInfoExecutableSectionBase, sectionTitle)


export default TeamMatchInfoExecutableSection