import React from 'react';
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import { LevelDataPropsWrapper } from '../../common/LevelDataProps'
import TeamData from '../../rest/models/leveldata/TeamData'
import TeamLevelDataProps from '../TeamLevelDataProps'
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import { getTeamsComparsion } from '../../rest/Client'
import '../NearestMatchesTable.css'
import '../overview/RankingTable.css'
import './CompareTeamsPage.css'
import CompareTeamsTable from './CompareTeamsTable'
import { Translation } from 'react-i18next'
import '../../i18n'
import RankingParametersProvider from '../../common/ranking/RankingParametersProvider'
import ExecutableComponent, { LoadableState } from '../../common/sections/ExecutableComponent';
import Section, { SectionState } from '../../common/sections/Section';

interface State {
    teamComparsion?: TeamComparsion
}

class CompareTeamsPageBase extends ExecutableComponent<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, 
    State, TeamComparsion, number | undefined, LoadableState<State, number | undefined> & SectionState> {
    
        constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props)
        
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: props.queryParams.teamId,
            state: {},
            collapsed: false
        }
    }
    
    executeDataRequest(dataRequest: number | undefined, 
            callback: (loadingState: LoadingEnum, result?: TeamComparsion) => void): void {
        if(dataRequest !== undefined) {
            getTeamsComparsion(this.props.levelDataProps.teamId(), dataRequest, callback)
        } else {
            callback(LoadingEnum.OK)
        }
    }

    stateFromResult(result?: TeamComparsion): State {
        return {
            teamComparsion: result
        }
    }

    renderSection(): JSX.Element {
        if (this.state.state.teamComparsion === undefined) {
            return <></>
        }
              
        let teamComparsion = this.state.state.teamComparsion
        return <Translation>{
            (t, { i18n }) => <>
            <div className="section_row">
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.HATSTATS()}
                />

                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.SALARY(this.props.levelDataProps.currencyRate(), this.props.levelDataProps.currency())}
                />
            </div>
            <div className="section_row">
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.TSI()}
                />
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.ATTACK()}
                />
            </div>
            <div className="section_row">
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.DEFENSE()}
                />
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.MIDFIELD()}
                />
            </div>
            <div className="section_row">
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.AGE()}
                />
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.RATING()}
                    diffFormatter={value => <>{value / 10}</>}
                />
            </div>
            <div className="section_row">
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.RATING_END_OF_MATCH()}
                    diffFormatter={value => <>{value / 10}</>}
                />
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.POWER_RATINGS()}
                />
            </div>
            <div className="section_row">
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.INJURY()}
                />
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.INJURY_COUNT()}
                />
            </div>
            <div className="section_row">
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.LODDAR_STATS()}
                />
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.FOUNDED_DATE()}
                />
            </div>
            </>
        }
        </Translation>
    }
}

const CompareTeamsPage = Section(CompareTeamsPageBase, _ => 'menu.comparsion_of_teams')
export default CompareTeamsPage