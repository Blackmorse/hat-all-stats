import React from 'react'
import ExecutableStatisticsSection from '../common/sections/ExecutableStatisticsSection'
import { LevelDataPropsWrapper } from '../common/LevelDataProps';
import TeamData from '../rest/models/leveldata/TeamData';
import TeamLevelDataProps from './TeamLevelDataProps'
import { LoadingEnum } from '../common/enums/LoadingEnum';
import TeamMatch from '../rest/models/match/TeamMatch';
import { Translation } from 'react-i18next'
import '../i18n'
import { getTeamMatches } from '../rest/Client'
import TeamMatchInfo from './matches/TeamMatchInfo'

interface State {
    matches?: Array<TeamMatch>
}

class TeamMatches extends ExecutableStatisticsSection<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State, Array<TeamMatch>, {}> {
    
    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props, "menu.matches")
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {},
            state: {}
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
        return <Translation>{
            (t, { i18n }) => <>
                {this.state.state.matches?.map(match => {
                    return <>
                        <TeamMatchInfo teamMatch={match}/>
                    </>
                })}
            </>
        }
        </Translation>
    }
}

export default TeamMatches