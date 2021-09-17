import React, { Fragment } from 'react'
import { LevelDataPropsWrapper } from '../common/LevelDataProps';
import TeamData from '../rest/models/leveldata/TeamData';
import TeamLevelDataProps from './TeamLevelDataProps'
import { LoadingEnum } from '../common/enums/LoadingEnum';
import TeamMatch from '../rest/models/match/TeamMatch';
import '../i18n'
import { getTeamMatches } from '../rest/Client'
import TeamMatchInfoSection from './matches/TeamMatchInfoSection'
import ExecutableComponent, { LoadableState } from '../common/sections/ExecutableComponent';
import Section, { SectionState } from '../common/sections/Section';

interface State {
    matches?: Array<TeamMatch>
}

class TeamMatchesBase extends ExecutableComponent<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, 
    State, Array<TeamMatch>, {}, LoadableState<State, {}> & SectionState> {
    
    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {},
            state: {},
            collapsed: false
        }
    }

    executeDataRequest(dataRequest: {}, callback: (loadingState: LoadingEnum, result?: Array<TeamMatch>) => void): void {
        getTeamMatches(this.props.levelDataProps.teamId(), this.props.levelDataProps.currentSeason(), callback)
    }

    stateFromResult(result?: Array<TeamMatch>): State {
        return {
            matches: (result) ? result : this.state.state.matches
        }
    }

    renderSection(): JSX.Element {
        return <Fragment>
                {this.state.state.matches?.map(match => {
                    return <TeamMatchInfoSection teamMatch={match} key={'team_matches_info_' + match.matchId} />
                })}
            </Fragment>
    }
}

const TeamMatches = Section(TeamMatchesBase, _ => 'menu.matches')
export default TeamMatches