import React from 'react'
import StatisticsSection from '../common/sections/StatisticsSection'
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
    loadingState: LoadingEnum,
    matches?: Array<TeamMatch>
}

class TeamMatches extends StatisticsSection<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State> {

    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props, "menu.matches")
        this.state = {
            loadingState: LoadingEnum.OK
        }
    }

    componentDidMount() {
        this.setState({
            matches: this.state.matches,
            loadingState: LoadingEnum.LOADING
        })

        getTeamMatches(this.props.levelDataProps.teamId(), this.props.levelDataProps.currentSeason(),
            (loadingStatus, matches) => this.setState({
                matches: (matches) ? matches : this.state.matches,
                loadingState: loadingStatus
            }))
    }
    
    updateCurrent(): void {
        this.componentDidMount()
    }

    renderSection(): JSX.Element {
        return <Translation>{
            (t, { i18n }) => <>
                {this.state.matches?.map(match => {
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