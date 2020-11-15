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
    loadingState: LoadingEnum
}

class TeamRankingsTable extends StatisticsSection<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State> {
    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props, "menu.team_rankings")
        this.state = {
            loadingState: LoadingEnum.OK
        }
        this.updateCurrent=this.updateCurrent.bind(this);
    }


    updateCurrent(): void {
        this.componentDidMount()
    }

    componentDidMount() {
        const teamRequest: TeamRequest = {type: 'TeamRequest', teamId: this.props.levelDataProps.teamId()}
        this.setState({
            teamRankingsStats: this.state.teamRankingsStats,
            loadingState: LoadingEnum.LOADING
        })

        getTeamRankings(teamRequest, 
            (loadingStatus, teamRankingsStats) => this.setState({
                teamRankingsStats: (teamRankingsStats) ? teamRankingsStats : this.state.teamRankingsStats, 
                loadingState: loadingStatus})
            )
    }

    renderSection(): JSX.Element {
        if(this.state.loadingState === LoadingEnum.LOADING || !this.state.teamRankingsStats || this.state.loadingState === LoadingEnum.ERROR) {
                return <></>
        }

        let leagueTeamsCount = this.state.teamRankingsStats.leagueTeamsCount
        let divisionLevelTeamsCount = this.state.teamRankingsStats.divisionLevelTeamsCount

        let teamRankings = this.state.teamRankingsStats.teamRankings

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
                            valueFunc={teamRanking => teamRanking.salary / (this.state.teamRankingsStats?.currencyRate as number) }
                            positionFunc={teamRanking => teamRanking.salaryPosition} 
                            formatter={commasSeparated}
                            title={t('table.salary') + ', ' + this.state.teamRankingsStats?.currencyName}   
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
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_RATINGS}
                            sortingField='rating'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.rating}
                            positionFunc={teamRanking => teamRanking.ratingPosition} 
                            formatter={ratingFormatter}
                            title={t('table.rating')}   
                        />
                        <RankingTable 
                            page={PagesEnum.TEAM_RATINGS}
                            sortingField='rating_end_of_match'
                            rankingData={rankingData}
                            valueFunc={teamRanking => teamRanking.ratingEndOfMatch}
                            positionFunc={teamRanking => teamRanking.ratingEndOfMatchPosition} 
                            formatter={ratingFormatter}
                            title={t('table.rating_end_of_match')}   
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