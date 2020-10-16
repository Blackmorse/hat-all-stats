import React from 'react';
import { getTeamRankings } from '../rest/Client'
import ModelTableTeamProps from './ModelTableTeamProps'
import TeamRequest from '../rest/models/request/TeamRequest';
import { ModelTablePropsWrapper } from '../common/ModelTable';
import TeamData from '../rest/models/TeamData';
import { Translation } from 'react-i18next'
import '../i18n'
import RankingTable from './overview/RankingTable'
import TeamRankingsStats from '../rest/models/TeamRankingsStats';
import { commasSeparated, ageFormatter, ratingFormatter, injuryFormatter } from '../common/Formatters'
import StatisticsSection from '../common/StatisticsSection'

interface State {
    teamRankingsStats?: TeamRankingsStats
    dataLoading: boolean,
    isError: boolean
}

class TeamRankingsTable extends StatisticsSection<ModelTablePropsWrapper<TeamData, ModelTableTeamProps>, State> {
    constructor(props: ModelTablePropsWrapper<TeamData, ModelTableTeamProps>) {
        super(props, "menu.team_rankings")
        this.state = {
            dataLoading: false,
            isError: false
        }
        this.updateCurrent=this.updateCurrent.bind(this);
    }


    updateCurrent(): void {
        this.componentDidMount()
    }

    componentDidMount() {
        const teamRequest: TeamRequest = {type: 'TeamRequest', teamId: this.props.modelTableProps.teamId()}
        this.setState({
            teamRankingsStats: this.state.teamRankingsStats,
            dataLoading: true,
            isError: false
        })


        getTeamRankings(teamRequest, 
            teamRankingsStats => this.setState({teamRankingsStats: teamRankingsStats, 
                dataLoading: false,
                isError: false}),
                () => this.setState({
                    teamRankingsStats: this.state.teamRankingsStats,
                    dataLoading: false,
                    isError: true
                }))
    }

    renderSection(): JSX.Element {
        if(this.state.dataLoading || !this.state.teamRankingsStats || this.state.isError) {
                return <></>
        }

        let leagueTeamsCount = this.state.teamRankingsStats.leagueTeamsCount
        let divisionLevelTeamsCount = this.state.teamRankingsStats.divisionLevelTeamsCount

        let teamRankings = this.state.teamRankingsStats.teamRankings

        let divisionLevelRankings = teamRankings.filter(teamRanking => teamRanking.rank_type === "division_level")
        let leagueRankings = teamRankings.filter(teamRanking => teamRanking.rank_type === "league_id")

        let lastLeagueRanking = leagueRankings[leagueRankings.length - 1]
        let previousLeagueRanking = (leagueRankings.length > 1) ? leagueRankings[leagueRankings.length - 2] : undefined

        let lastDivisionLevelRanking = divisionLevelRankings[divisionLevelRankings.length - 1]
        let previousDivisionLevelRanking = (divisionLevelRankings.length > 1) ? divisionLevelRankings[divisionLevelRankings.length - 2] : undefined;

        let rankingData = {
            lastLeagueRanking: lastLeagueRanking, 
            previousLeagueRanking: previousLeagueRanking,
            lastDivisionLevelRanking: lastDivisionLevelRanking,
            previousDivisionLevelRanking: previousDivisionLevelRanking,
            modelTableTeamProps: this.props.modelTableProps,
            leagueTeamsCount: leagueTeamsCount,
            divisionLevelTeamsCount: divisionLevelTeamsCount,
        }

        return <Translation>{
            (t, { i18n }) => <>
                <div className="rankings_grid">
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.hatstats}
                            positionFunc={teamRanking => teamRanking.hatstatsPosition} 
                            formatter={commasSeparated}
                            title={t('table.hatstats')}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.salary}
                            positionFunc={teamRanking => teamRanking.salaryPosition} 
                            formatter={commasSeparated}
                            title={t('table.salary') + ', ' + this.state.teamRankingsStats?.currencyName}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.tsi}
                            positionFunc={teamRanking => teamRanking.tsiPosition} 
                            formatter={commasSeparated}
                            title={t('table.tsi')}   
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.attack}
                            positionFunc={teamRanking => teamRanking.attackPosition} 
                            formatter={commasSeparated}
                            title={t('table.attack')}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.defense}
                            positionFunc={teamRanking => teamRanking.defensePosition} 
                            formatter={commasSeparated}
                            title={t('table.defense')}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.midfield}
                            positionFunc={teamRanking => teamRanking.midfieldPosition} 
                            formatter={commasSeparated}
                            title={t('table.midfield')}   
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.age}
                            positionFunc={teamRanking => teamRanking.agePosition} 
                            formatter={ageFormatter}
                            title={t('table.age')}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.rating}
                            positionFunc={teamRanking => teamRanking.ratingPosition} 
                            formatter={ratingFormatter}
                            title={t('table.rating')}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.ratingEndOfMatch}
                            positionFunc={teamRanking => teamRanking.ratingEndOfMatchPosition} 
                            formatter={ratingFormatter}
                            title={t('table.rating_end_of_match')}   
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.powerRating}
                            positionFunc={teamRanking => teamRanking.powerRatingPosition} 
                            formatter={commasSeparated}
                            title={t('table.power_rating')}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.injury}
                            positionFunc={teamRanking => teamRanking.injuryPosition} 
                            formatter={injuryFormatter}
                            title={t('table.total_injury_weeks')}   
                        />
                        <RankingTable 
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.injuryCount}
                            positionFunc={teamRanking => teamRanking.injuryCountPosition} 
                            formatter={commasSeparated}
                            title={t('table.total_injury_number')}   
                        />
                    </div>
                </div>
            </>
            }
        </Translation>
    }
}

export default TeamRankingsTable