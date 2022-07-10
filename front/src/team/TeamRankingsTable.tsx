import React from 'react';
import { getTeamRankings } from '../rest/Client'
import TeamLevelDataProps from './TeamLevelDataProps'
import TeamRequest from '../rest/models/request/TeamRequest';
import { LevelDataPropsWrapper } from '../common/LevelDataProps';
import '../i18n'
import RankingTable, { RankingData } from './overview/RankingTable'
import TeamRankingsStats from '../rest/models/team/TeamRankingsStats';
import { LoadingEnum } from '../common/enums/LoadingEnum';
import RankingParametersProvider from '../common/ranking/RankingParametersProvider'
import ExecutableComponent from '../common/sections/ExecutableComponent';
import Section, { SectionState } from '../common/sections/Section';
import StatsTypeSelector from '../common/selectors/StatsTypeSelector';
import { StatsType, StatsTypeEnum } from '../rest/models/StatisticsParameters';
import SeasonSelector from '../common/selectors/SeasonSelector';
import { Col, Container, Row } from 'react-bootstrap';

interface State {
    teamRankingsStats?: TeamRankingsStats,
    round: number,
    season: number
}

class TeamRankingsTableBase extends ExecutableComponent<LevelDataPropsWrapper<TeamLevelDataProps>, 
    State & SectionState, TeamRankingsStats, number> {
    
    constructor(props: LevelDataPropsWrapper<TeamLevelDataProps>) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: this.props.levelDataProps.currentSeason(),
            round: props.levelDataProps.currentRound(),
            season: props.levelDataProps.currentSeason(),
            collapsed: false
        }

        this.roundChanged=this.roundChanged.bind(this);
        this.seasonChanged=this.seasonChanged.bind(this);
    }

    executeDataRequest(dataRequest: number, callback: (loadingState: LoadingEnum, result?: TeamRankingsStats) => void): void {
        const teamRequest: TeamRequest = {type: 'TeamRequest', teamId: this.props.levelDataProps.teamId()}

        getTeamRankings(teamRequest, dataRequest, callback)
    }

    stateFromResult(result?: TeamRankingsStats | undefined): State & SectionState {
        if (result !== undefined) {
            let rounds = result.teamRankings.map(x => x.round)
            let maxRound = Math.max(...rounds)

            return {
                teamRankingsStats: (result) ? result : this.state.teamRankingsStats,
                round: maxRound,
                season: this.state.dataRequest,
                collapsed: this.state.collapsed
            }
        } else {
            return {
                teamRankingsStats: this.state.teamRankingsStats,
                round: this.state.round,
                season: this.state.dataRequest,
                collapsed: this.state.collapsed
            }
        }
        
    }

    roundChanged(statType: StatsType) {
        let newState = {
            round: statType.roundNumber!
        }
        this.setState(newState)
    }

    seasonChanged(season: number) {
        this.updateWithRequest(season)
    }

    renderSection(): JSX.Element {
        let teamRankingsStats = this.state.teamRankingsStats
        if(this.state.loadingState === LoadingEnum.LOADING || 
            teamRankingsStats === undefined ||
             this.state.loadingState === LoadingEnum.ERROR) {
                return <></>
        }


        let teamRankings = teamRankingsStats.teamRankings.filter(tr => tr.round <= this.state.round)

        if (teamRankings.length === 0 ) {
            return <></>
        }

        let leagueTeamsCount = teamRankingsStats.leagueTeamsCounts.find(x => x[0] === this.state.round)
        let divisionLevelTeamsCount = teamRankingsStats.divisionLevelTeamsCounts.find(x => x[0] === this.state.round)
        if (leagueTeamsCount === undefined || divisionLevelTeamsCount === undefined) {
            throw new Error("Unknown round")
        }      

        let rankingData: RankingData = {
            teamRankings: teamRankings,
            teamLevelDataProps: this.props.levelDataProps,
            round: this.state.round,
            season: this.state.season,
            leagueTeamsCount: leagueTeamsCount[1],
            divisionLevelTeamsCount: divisionLevelTeamsCount[1],
        }

        return <>
                <Container className='d-flex flex-row mb-2'>
                    <SeasonSelector currentSeason={this.state.season}
                        seasonOffset={this.props.levelDataProps.levelData.seasonOffset}
                        seasons={this.props.levelDataProps.seasons()}
                        callback={this.seasonChanged}
                    />
                    <StatsTypeSelector statsTypes={[StatsTypeEnum.ROUND]}
                        rounds={(teamRankingsStats as TeamRankingsStats).leagueTeamsCounts.map(x => x[0])}
                        selectedStatType={{statType: StatsTypeEnum.ROUND, roundNumber: this.state.round}}
                        onChanged={this.roundChanged}
                    />
                </Container>
                <Row>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.HATSTATS()}
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.SALARY(this.state.teamRankingsStats?.currencyRate, this.state.teamRankingsStats?.currencyName)} 
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.TSI()}
                        />
                    </Col>
                {/* </Row>
                <Row> */}
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.ATTACK()}  
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.DEFENSE()}
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.MIDFIELD()}
                        />
                    </Col>
                {/* </Row>
                <Row> */}
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.AGE()}
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.RATING()}
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.RATING_END_OF_MATCH()}
                        />
                    </Col>
                {/* </Row>
                <Row> */}
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.POWER_RATINGS()}
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.INJURY()}                               
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.INJURY_COUNT()}
                        />
                    </Col>
                {/* </Row>
                <Row> */}
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable 
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.LODDAR_STATS()}
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.FOUNDED_DATE()}
                        />
                    </Col>
                </Row>
            </>
    }
}

const TeamRankingsTable = Section(TeamRankingsTableBase, _ => 'menu.team_rankings')
export default TeamRankingsTable
