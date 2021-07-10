import React from 'react';
import { getTeamRankings } from '../rest/Client'
import TeamLevelDataProps from './TeamLevelDataProps'
import TeamRequest from '../rest/models/request/TeamRequest';
import { LevelDataPropsWrapper } from '../common/LevelDataProps';
import TeamData from '../rest/models/leveldata/TeamData';
import { Translation } from 'react-i18next'
import '../i18n'
import RankingTable, { RankingData } from './overview/RankingTable'
import TeamRankingsStats from '../rest/models/team/TeamRankingsStats';
import ExecutableStatisticsSection from '../common/sections/ExecutableStatisticsSection'
import { LoadingEnum } from '../common/enums/LoadingEnum';
import RankingParametersProvider from '../common/ranking/RankingParametersProvider'

interface State {
    teamRankingsStats?: TeamRankingsStats
}

class TeamRankingsTable extends ExecutableStatisticsSection<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State, TeamRankingsStats, {}> {
    
    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props, "menu.team_rankings")
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {},
            state: {}
        }
    }

    executeDataRequest(dataRequest: {}, callback: (loadingState: LoadingEnum, result?: TeamRankingsStats) => void): void {
        const teamRequest: TeamRequest = {type: 'TeamRequest', teamId: this.props.levelDataProps.teamId()}

        getTeamRankings(teamRequest, callback)
    }

    stateFromResult(result?: TeamRankingsStats | undefined): State {
        return {
            teamRankingsStats: (result) ? result : this.state.state.teamRankingsStats
        }
    }

    renderSection(): JSX.Element {
        if(this.state.loadingState === LoadingEnum.LOADING || 
            !this.state.state.teamRankingsStats ||
             this.state.state.teamRankingsStats.teamRankings.length === 0 ||
             this.state.loadingState === LoadingEnum.ERROR) {
                return <></>
        }

        let leagueTeamsCount = this.state.state.teamRankingsStats.leagueTeamsCount
        let divisionLevelTeamsCount = this.state.state.teamRankingsStats.divisionLevelTeamsCount

        let teamRankings = this.state.state.teamRankingsStats.teamRankings

        let rankingData: RankingData = {
            teamRankings: teamRankings,
            teamLevelDataProps: this.props.levelDataProps,
            leagueTeamsCount: leagueTeamsCount,
            divisionLevelTeamsCount: divisionLevelTeamsCount,
        }

        return <Translation>{
            (t, { i18n }) => <>
                <div className="rankings_grid">
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.HATSTATS()}
                        />
                        <RankingTable
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.SALARY(this.state.state.teamRankingsStats?.currencyRate, this.state.state.teamRankingsStats?.currencyName)} 
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.TSI()}
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.ATTACK()}  
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.DEFENSE()}
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.MIDFIELD()}
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.AGE()}
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.RATING()}
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.RATING_END_OF_MATCH()}
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.POWER_RATINGS()}
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.INJURY()}                               
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.INJURY_COUNT()}
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.LODDAR_STATS()}
                        />
                    </div>
                </div>
            </>
            }
        </Translation>
    }
}

export default TeamRankingsTable