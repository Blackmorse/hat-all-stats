import React, {useEffect, useState} from 'react';
import ExecutableComponent, {StateAndRequest} from "../common/sections/HookExecutableComponent";
import TeamRankingsStats from "../rest/models/team/TeamRankingsStats";
import TeamLevelDataProps from "./TeamLevelDataProps";
import {getTeamRankings2} from "../rest/Client";
import {Col, Container, Row} from "react-bootstrap";
import SeasonSelector from "../common/selectors/SeasonSelector";
import StatsTypeSelector from "../common/selectors/StatsTypeSelector";
import {StatsType, StatsTypeEnum} from "../rest/models/StatisticsParameters";
import RankingTable, {RankingData} from "./overview/RankingTable";
import RankingParametersProvider from "../common/ranking/RankingParametersProvider";

interface Props {
    props: TeamLevelDataProps
}

const TeamRankingsTable = ( { props }: Props) => {
    const [ round, setRound ] = useState<number>(props.currentRound())

    return <ExecutableComponent<number, TeamRankingsStats>
        initialRequest={ props.currentSeason() }
        responseToState={r => r || {
            teamRankings: [],
            leagueTeamsCounts: [],
            divisionLevelTeamsCounts: [],
            currencyRate: props.currencyRate(),
            currencyName: props.currency()
        }}
        executeRequest={(request, callback) => {
            getTeamRankings2(props.teamId(), request, callback)
        }}
        content={(stateAndRequest: StateAndRequest<number, TeamRankingsStats>) => {
            const teamRankingsStats = stateAndRequest.currentState


            let teamRankings = (stateAndRequest.currentRequest === -1)
                ? teamRankingsStats.teamRankings
                : teamRankingsStats.teamRankings.filter(tr => tr.round <= round)

            teamRankings = teamRankings.sort((a, b) => {
                if (a.season !== b.season) {
                    return a.season - b.season
                } else {
                    return a.round - b.round
                }
            })
            if (teamRankings.length === 0 ) {
                return <></>
            }

            let leagueTeamsCount = teamRankingsStats.leagueTeamsCounts.find(x => x[0] === round)
            let divisionLevelTeamsCount = teamRankingsStats.divisionLevelTeamsCounts.find(x => x[0] === round)
            if (leagueTeamsCount === undefined || divisionLevelTeamsCount === undefined) {
                throw new Error("Unknown round")
            }

            let rankingData: RankingData = {
                teamRankings: teamRankings,
                teamLevelDataProps: props,
                round: round,
                season: stateAndRequest.currentRequest,
                leagueTeamsCount: leagueTeamsCount[1],
                divisionLevelTeamsCount: divisionLevelTeamsCount[1],
            }
            return <>
                <Container className='d-flex flex-row mb-2'>
                    <SeasonSelector currentSeason={stateAndRequest.currentRequest}
                                    seasonOffset={props.levelData.seasonOffset}
                                    seasons={props.seasons()}
                                    all={true}
                                    callback={stateAndRequest.setRequest}
                    />
                    {(stateAndRequest.currentRequest !== -1) && <StatsTypeSelector statsTypes={[StatsTypeEnum.ROUND]}
                                       rounds={(teamRankingsStats as TeamRankingsStats).leagueTeamsCounts.map(x => x[0])}
                                       selectedStatType={{statType: StatsTypeEnum.ROUND, roundNumber: round}}
                                       onChanged={(statType: StatsType) =>
                                           setRound(statType.roundNumber!)
                                       }
                    />}
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
                            rankingParameters={RankingParametersProvider.SALARY(teamRankingsStats.currencyRate, teamRankingsStats.currencyName)}
                        />
                    </Col>
                    <Col lg={4} md={6} className='my-2'>
                        <RankingTable
                            rankingData={rankingData}
                            rankingParameters={RankingParametersProvider.TSI()}
                        />
                    </Col>
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
        }}
        />
}

export default TeamRankingsTable