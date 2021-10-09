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
    ExecutableComponent<Props, State & SectionState, SingleMatch, number> {

    constructor(props: Props) {
        super(props, )
        this.state={
            loadingState: LoadingEnum.OK,
            collapsed: false,
            dataRequest: props.matchId
        }
    }
    
    executeDataRequest(dataRequest: number, callback: (loadingState: LoadingEnum, result?: SingleMatch) => void): void {
        getSingleMatch(dataRequest, callback)
    }

    stateFromResult(result?: SingleMatch): State & SectionState {
        return {
            singleMatch: result,
            collapsed: this.state.collapsed
        }
    }

    renderSection(): JSX.Element {
        if (this.state.singleMatch === undefined) {
            return <></>
        }
        return <TeamMatchInfo singleMatch={this.state.singleMatch}/>
    }
}

function sectionTitle(props: Props, state: LoadableState<number> & State & SectionState): JSX.Element {
    if (state.singleMatch === undefined) {
        return <></>
    }
    return <>
        {state.singleMatch.homeTeamName} - {state.singleMatch.awayTeamName}
    </>
}

type TeamMatchSectionState = LoadableState<number> & State & SectionState
const TeamMatchInfoExecutableSection = Section<Props, TeamMatchSectionState, GSection<Props, TeamMatchSectionState>>(
            TeamMatchInfoExecutableSectionBase, sectionTitle)


export default TeamMatchInfoExecutableSection