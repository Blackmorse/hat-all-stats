import React, { Fragment } from 'react'
import { LevelDataPropsWrapper } from '../common/LevelDataProps';
import TeamLevelDataProps from './TeamLevelDataProps'
import { LoadingEnum } from '../common/enums/LoadingEnum';
import TeamMatch from '../rest/models/match/TeamMatch';
import '../i18n'
import { getTeamMatches } from '../rest/Client'
import TeamMatchInfoSection from './matches/TeamMatchInfoSection'
import ExecutableComponent from '../common/sections/ExecutableComponent';
import Section, { SectionState } from '../common/sections/Section';

interface State {
    matches?: Array<TeamMatch>
}

class TeamMatchesBase extends ExecutableComponent<LevelDataPropsWrapper<TeamLevelDataProps>, 
    State & SectionState, Array<TeamMatch>, {}> {
    
    constructor(props: LevelDataPropsWrapper<TeamLevelDataProps>) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {},
            collapsed: false
        }
    }

    executeDataRequest(_dataRequest: {}, callback: (loadingState: LoadingEnum, result?: Array<TeamMatch>) => void): void {
        getTeamMatches(this.props.levelDataProps.teamId(), this.props.levelDataProps.currentSeason(), callback)
    }

    stateFromResult(result?: Array<TeamMatch>): State & SectionState {
        return {
            matches: (result) ? result : this.state.matches,
            collapsed: this.state.collapsed
        }
    }

    renderSection(): JSX.Element {
        return <Fragment>
                {this.state.matches?.map(match => {
                    return <TeamMatchInfoSection teamMatch={match} key={'team_matches_info_' + match.matchId} />
                })}
            </Fragment>
    }
}

const TeamMatches = Section(TeamMatchesBase, _ => 'menu.matches')
export default TeamMatches
