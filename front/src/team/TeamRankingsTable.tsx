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
import { commasSeparated, ageFormatter, ratingFormatter, injuryFormatter } from '../common/Formatters'
import StatisticsSection from '../common/sections/StatisticsSection'
import { PagesEnum } from '../common/enums/PagesEnum';
import { LoadingEnum } from '../common/enums/LoadingEnum';

interface State {
    teamRankingsStats?: TeamRankingsStats
}

class TeamRankingsTable extends StatisticsSection<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State, TeamRankingsStats, {}> {
    
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
                            page={PagesEnum.TEAM_HATSTATS}
                            sortingField='hatstats'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.hatstats}
                            positionFunc={teamRanking => teamRanking.hatstatsPosition} 
                            formatter={commasSeparated}
                            title={t('table.hatstats')}   
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_SALARY_TSI}
                            sortingField='salary'
                            rankingData={rankingData}
                            valueFunc={teamRanking => Math.floor(teamRanking.salary / (this.state.state.teamRankingsStats?.currencyRate as number)) }
                            positionFunc={teamRanking => teamRanking.salaryPosition} 
                            formatter={commasSeparated}
                            title={t('table.salary') + ', ' + this.state.state.teamRankingsStats?.currencyName}   
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_SALARY_TSI}
                            sortingField='tsi'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.tsi}
                            positionFunc={teamRanking => teamRanking.tsiPosition} 
                            formatter={commasSeparated}
                            title={t('table.tsi')}   
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            page={PagesEnum.TEAM_HATSTATS}
                            sortingField='attack'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.attack}
                            positionFunc={teamRanking => teamRanking.attackPosition} 
                            formatter={commasSeparated}
                            title={t('table.attack')}   
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_HATSTATS}
                            sortingField='defense'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.defense}
                            positionFunc={teamRanking => teamRanking.defensePosition} 
                            formatter={commasSeparated}
                            title={t('table.defense')}   
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_HATSTATS}
                            sortingField='midfield'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.midfield}
                            positionFunc={teamRanking => teamRanking.midfieldPosition} 
                            formatter={commasSeparated}
                            title={t('table.midfield')}   
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            page={PagesEnum.TEAM_AGE_INJURY}
                            sortingField='age'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.age}
                            positionFunc={teamRanking => teamRanking.agePosition} 
                            formatter={ageFormatter}
                            title={t('table.age')}   
                            yAxisFunc={y => Number(y / 112)}
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_RATINGS}
                            sortingField='rating'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.rating}
                            positionFunc={teamRanking => teamRanking.ratingPosition} 
                            formatter={ratingFormatter}
                            title={t('table.rating')}   
                            yAxisFunc={y => y / 10}
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_RATINGS}
                            sortingField='rating_end_of_match'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.ratingEndOfMatch}
                            positionFunc={teamRanking => teamRanking.ratingEndOfMatchPosition} 
                            formatter={ratingFormatter}
                            title={t('table.rating_end_of_match')}   
                            yAxisFunc={y => y / 10}
                        />
                    </div>
                    <div className="rankings_grid_row">
                        <RankingTable 
                            page={PagesEnum.TEAM_POWER_RATINGS}
                            sortingField='power_rating'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.powerRating}
                            positionFunc={teamRanking => teamRanking.powerRatingPosition} 
                            formatter={commasSeparated}
                            title={t('table.power_rating')}   
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_AGE_INJURY}
                            sortingField='injury'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.injury}
                            positionFunc={teamRanking => teamRanking.injuryPosition} 
                            formatter={injuryFormatter}
                            title={t('table.total_injury_weeks')}
                               
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_AGE_INJURY}
                            sortingField='injury_count'
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