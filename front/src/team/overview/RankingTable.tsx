import React from 'react';
import TeamRanking from '../../rest/models/team/TeamRanking'
import '../../i18n'
import '../../common/elements/Trends.css'
import TeamLevelDataProps from '../TeamLevelDataProps';
import LeagueLink from '../../common/links/LeagueLink';
import DivisionLevelLink from '../../common/links/DivisionLevelLink';
import { toRoman } from "../../common/Utils"
import DiffPosition from '../../common/widgets/DiffPosition'
import DiffValue from '../../common/widgets/DiffValue'
import RankingParameters from '../../common/ranking/RankingParameters';
import '../../common/charts/Charts.css'
import ChartLink from '../../common/charts/ChartLink';
import ValueRankingsChart from './ValueRankingsChart';
import PositionRankingChart from './PositionRankingChart';
import { Card, Col, Row } from 'react-bootstrap';

export interface RankingData {
    teamRankings: Array<TeamRanking>,
    round: number,
    season: number,
    teamLevelDataProps: TeamLevelDataProps,
    leagueTeamsCount: number,
    divisionLevelTeamsCount: number
}

interface Props {
    rankingData: RankingData,
    rankingParameters: RankingParameters
}

class RankingTable extends React.Component<Props>{
    constructor(props: Props) {
        super(props)
        this.chartContent = this.chartContent.bind(this)
    }

    chartContent(): JSX.Element {
        return <>
            <ValueRankingsChart 
                leagueRankings={this.props.rankingData.teamRankings.filter(teamRanking => teamRanking.rankType === "league_id")}
                valueFunc={this.props.rankingParameters.valueFunc}
                title={this.props.rankingParameters.title}
                formatterFunc={this.props.rankingParameters.yAxisFunc}
                />
            <PositionRankingChart 
                leagueRankings={this.props.rankingData.teamRankings.filter(teamRanking => teamRanking.rankType === "league_id")}
                divisionLevelRankings={this.props.rankingData.teamRankings.filter(teamRanking => teamRanking.rankType === "division_level")}
                positionFunc={this.props.rankingParameters.positionFunc}
                teamLevelDataProps={this.props.rankingData.teamLevelDataProps}
            />
        </>
    }

    render() {
        let formatter = this.props.rankingParameters.formatter
        let valueFunc = this.props.rankingParameters.valueFunc
        let positionFunc = this.props.rankingParameters.positionFunc

        let divisionLevelRankings = this.props.rankingData.teamRankings.filter(teamRanking => teamRanking.rankType === "division_level")
        let leagueRankings = this.props.rankingData.teamRankings.filter(teamRanking => teamRanking.rankType === "league_id")

        let lastLeagueRanking = leagueRankings[leagueRankings.length - 1]
        let previousLeagueRanking = (leagueRankings.length > 1) ? leagueRankings[leagueRankings.length - 2] : undefined

        let lastDivisionLevelRanking = divisionLevelRankings[divisionLevelRankings.length - 1]
        let previousDivisionLevelRanking = (divisionLevelRankings.length > 1) ? divisionLevelRankings[divisionLevelRankings.length - 2] : undefined;

        let diffValueContent: JSX.Element | undefined = undefined
        let divisionLevelDiffPositionContent: JSX.Element | undefined = undefined
        if (previousDivisionLevelRanking !== undefined) {
            divisionLevelDiffPositionContent = <DiffPosition
                positionFunc={positionFunc}
                previousRanking={previousDivisionLevelRanking}
                lastRanking={lastDivisionLevelRanking} 
                />

            diffValueContent = <DiffValue
                 formatter={formatter}
                 valueFunc={valueFunc}
                 previousRanking={previousDivisionLevelRanking}
                 lastRanking={lastDivisionLevelRanking} />
        }

        let leagueDiffPositionContent: JSX.Element | undefined 
        if(previousLeagueRanking) {
            leagueDiffPositionContent = <DiffPosition 
                positionFunc={positionFunc}
                previousRanking={previousLeagueRanking}
                lastRanking={lastLeagueRanking} />
        }

            return <Card>
            <Card.Header className='text-center'>
                {this.props.rankingParameters.title}
                {<ChartLink chartContent={this.chartContent} />}
            </Card.Header>
            <Card.Body>
                <Row className='my-1'>
                    <Col className='d-flex justify-content-end'>
                        <span className='very-small-font'>
                            {this.props.rankingParameters.title}
                        </span>
                    </Col>
                    <Col className='small-font text-center d-flex align-items-center justify-content-center'>{formatter(valueFunc(lastDivisionLevelRanking))}</Col>
                    <Col className='d-flex align-items-center'>
                        <span className='very-small-font d-flex flex-row align-items-center'>{diffValueContent}</span>
                    </Col>
                </Row>
                <Row className='my-1'>
                    <Col className='very-small-font d-flex justify-content-end'>
                        <LeagueLink  tableLink={true}
                            id={this.props.rankingData.teamLevelDataProps.leagueId()}
                            text={this.props.rankingData.teamLevelDataProps.leagueName()}
                            />
                    </Col>
                    <Col className='text-center small-font'>
                        <LeagueLink id={this.props.rankingData.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={(positionFunc(lastLeagueRanking) + 1).toString()} 
                            page={this.props.rankingParameters.page} 
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField,
                                selectedRow: positionFunc(lastLeagueRanking),
                                round: this.props.rankingData.round,
                                season: this.props.rankingData.season
                            }}
                        />
                        /{this.props.rankingData.leagueTeamsCount}
                    </Col>
                    <Col className='very-small-font'>
                        {(previousLeagueRanking) ? <LeagueLink id={this.props.rankingData.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={leagueDiffPositionContent} 
                            page={this.props.rankingParameters.page}
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField,
                                selectedRow: positionFunc(previousLeagueRanking),
                                round: this.props.rankingData.round - 1,
                                season: this.props.rankingData.season
                            }}
                        /> : <></>
                        }
                    </Col>
                </Row>
                <Row className='my-1'>
                    <Col className='very-small-font d-flex justify-content-end'>
                        <DivisionLevelLink
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={lastDivisionLevelRanking.divisionLevel}
                            text={toRoman(lastDivisionLevelRanking.divisionLevel)}
                            />
                    </Col>
                    <Col className='text-center small-font'>
                        <DivisionLevelLink 
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={lastDivisionLevelRanking.divisionLevel}
                            text={(positionFunc(lastDivisionLevelRanking) + 1).toString()}
                            page={this.props.rankingParameters.page}
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField, 
                                selectedRow: positionFunc(lastDivisionLevelRanking),
                                round: this.props.rankingData.round,
                                season: this.props.rankingData.season
                            }}
                        />/{this.props.rankingData.divisionLevelTeamsCount}
                    </Col>
                    <Col className='very-small-font'>
                    {(previousDivisionLevelRanking) ? <DivisionLevelLink 
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={this.props.rankingData.teamLevelDataProps.divisionLevel()}
                            text={divisionLevelDiffPositionContent}
                            page={this.props.rankingParameters.page}
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField, 
                                selectedRow: positionFunc(previousDivisionLevelRanking),
                                round: this.props.rankingData.round - 1,
                                season: this.props.rankingData.season
                            }}
                        /> : <></>
                    }
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    }
}

export default RankingTable
