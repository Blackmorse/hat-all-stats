import React from 'react';
import { getTeamRankings } from '../rest/Client'
import TeamLevelDataProps from './TeamLevelDataProps'
import TeamRequest from '../rest/models/request/TeamRequest';
import { LevelDataPropsWrapper } from '../common/LevelDataProps';
import TeamData from '../rest/models/leveldata/TeamData';
import '../i18n'
import RankingTable, { RankingData } from './overview/RankingTable'
import TeamRankingsStats from '../rest/models/team/TeamRankingsStats';
import { LoadingEnum } from '../common/enums/LoadingEnum';
import RankingParametersProvider from '../common/ranking/RankingParametersProvider'
import ExecutableComponent, { LoadableState } from '../common/sections/ExecutableComponent';
import Section, { SectionState } from '../common/sections/Section';
import StatsTypeSelector from '../common/selectors/StatsTypeSelector';
import { StatsType, StatsTypeEnum } from '../rest/models/StatisticsParameters';
import SeasonSelector from '../common/selectors/SeasonSelector';
import './TeamRankingsTable.css'

interface State {
    teamRankingsStats?: TeamRankingsStats,
    round: number,
    season: number
}

class TeamRankingsTableBase extends ExecutableComponent<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, 
    State, TeamRankingsStats, number, LoadableState<State, number> & SectionState> {
    
    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: this.props.levelDataProps.currentSeason(),
            state: {
                round: props.levelDataProps.currentRound(),
                season: props.levelDataProps.currentSeason()
            },
            collapsed: false
        }

        this.roundChanged=this.roundChanged.bind(this);
        this.seasonChanged=this.seasonChanged.bind(this);
    }

    executeDataRequest(dataRequest: number, callback: (loadingState: LoadingEnum, result?: TeamRankingsStats) => void): void {
        const teamRequest: TeamRequest = {type: 'TeamRequest', teamId: this.props.levelDataProps.teamId()}

        getTeamRankings(teamRequest, dataRequest, callback)
    }

    stateFromResult(result?: TeamRankingsStats | undefined): State {
        if (result !== undefined) {
            let rounds = result.teamRankings.map(x => x.round)
            let maxRound = Math.max(...rounds)

            return {
                teamRankingsStats: (result) ? result : this.state.state.teamRankingsStats,
                round: maxRound,
                season: this.state.dataRequest
            }
        } else {
            return {
                teamRankingsStats: this.state.state.teamRankingsStats,
                round: this.state.state.round,
                season: this.state.dataRequest
            }
        }
        
    }

    roundChanged(statType: StatsType) {
        let newRound = statType.roundNumber as number
        let newState = Object.assign({}, this.state)
        newState.state.round = newRound

        this.setState(newState)
    }

    seasonChanged(season: number) {
        this.updateWithRequest(season)
    }

    renderSection(): JSX.Element {
        let teamRankingsStats = this.state.state.teamRankingsStats
        if(this.state.loadingState === LoadingEnum.LOADING || 
            teamRankingsStats === undefined ||
             this.state.loadingState === LoadingEnum.ERROR) {
                return <></>
        }


        let teamRankings = teamRankingsStats.teamRankings.filter(tr => tr.round <= this.state.state.round)

        if (teamRankings.length === 0 ) {
            return <></>
        }

        let leagueTeamsCount = teamRankingsStats.leagueTeamsCounts.find(x => x[0] === this.state.state.round)
        let divisionLevelTeamsCount = teamRankingsStats.divisionLevelTeamsCounts.find(x => x[0] === this.state.state.round)
        if (leagueTeamsCount === undefined || divisionLevelTeamsCount === undefined) {
            throw new Error("Unknown round")
        }      

        let rankingData: RankingData = {
            teamRankings: teamRankings,
            teamLevelDataProps: this.props.levelDataProps,
            round: this.state.state.round,
            leagueTeamsCount: leagueTeamsCount[1],
            divisionLevelTeamsCount: divisionLevelTeamsCount[1],
        }

        return <>
                <div className="table_settings_team_rankings">
                    <SeasonSelector currentSeason={this.state.state.season}
                        seasonOffset={this.props.levelDataProps.levelData.seasonOffset}
                        seasons={this.props.levelDataProps.seasons()}
                        callback={this.seasonChanged}
                    />
                    <StatsTypeSelector statsTypes={[StatsTypeEnum.ROUND]}
                        rounds={(teamRankingsStats as TeamRankingsStats).leagueTeamsCounts.map(x => x[0])}
                        selectedStatType={{statType: StatsTypeEnum.ROUND, roundNumber: this.state.state.round}}
                        onChanged={this.roundChanged}
                    />
                </div>
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
}

const TeamRankingsTable = Section(TeamRankingsTableBase, _ => 'menu.team_rankings')
export default TeamRankingsTable