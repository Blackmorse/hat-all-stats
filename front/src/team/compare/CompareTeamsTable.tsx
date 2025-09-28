import React, { type JSX } from 'react';
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import DiffPosition from '../../common/widgets/DiffPosition'
import DiffValue from '../../common/widgets/DiffValue'
import LeagueLink from '../../common/links/LeagueLink';
import TeamLevelDataProps from '../TeamLevelDataProps';
import RankingParameters from '../../common/ranking/RankingParameters'
import TeamLink from '../../common/links/TeamLink';
import '../../common/charts/Charts.css'
import ChartLink from '../../common/charts/ChartLink'
import CompareTeamsValuesChart from './CompareTeamsValuesChart';
import CompareTeamsPositionsChart from './CompareTeamsPositionsChart';
import { Card, Col, Row } from 'react-bootstrap';

interface Props {
    teamComparsion: TeamComparsion,
    teamLevelDataProps: TeamLevelDataProps,
    rankingParameters: RankingParameters,
    diffFormatter?: (value: number) => JSX.Element
}


class CompareTeamsTable extends React.Component<Props> {

    constructor(props: Props) {
        super(props)
        this.chartContent = this.chartContent.bind(this)
    }

    chartContent(): JSX.Element {
        return <>
        <CompareTeamsValuesChart 
            teamComparsion={this.props.teamComparsion}
            valueFunc={this.props.rankingParameters.valueFunc}
            title={this.props.rankingParameters.title}
            formatterFunc={this.props.rankingParameters.yAxisFunc}
        />
        <CompareTeamsPositionsChart 
            teamComparsion={this.props.teamComparsion}
            positionFunc={this.props.rankingParameters.positionFunc}
            teamLevelDataProps={this.props.teamLevelDataProps}
        /></>
    }

    render() {
        const team1Rankings = this.props.teamComparsion.team1Rankings
        const team1LastRanking = team1Rankings[team1Rankings.length - 1]
        const team1PreviousRanking = (team1Rankings.length > 1) ? team1Rankings[team1Rankings.length - 2] : undefined
        const team2Rankings = this.props.teamComparsion.team2Rankings
        const team2PreviousRanking = (team2Rankings.length > 1) ? team2Rankings[team2Rankings.length - 2] : undefined
        const team2LastRanking = team2Rankings[team2Rankings.length - 1]

        let team1PreviousPositionDiff: JSX.Element = <></>
        let team1PreviousValueDiff: JSX.Element = <></>

        let team2PreviousPositionDiff: JSX.Element = <></>
        let team2PreviousValueDiff: JSX.Element = <></>
        if (team1PreviousRanking !== undefined && team2PreviousRanking !== undefined) {
            const team1PreviousPositionDiffNumber = <DiffPosition
                positionFunc={this.props.rankingParameters.positionFunc}
                previousRanking={team1PreviousRanking}
                lastRanking={team1LastRanking} />

            team1PreviousPositionDiff = <LeagueLink id={this.props.teamLevelDataProps.leagueId()} 
                tableLink={true}
                text={team1PreviousPositionDiffNumber} 
                page={this.props.rankingParameters.page} 
                queryParams={{
                    sortingField: this.props.rankingParameters.sortingField,
                    selectedRow: this.props.rankingParameters.positionFunc(team1PreviousRanking),
                    round: team1PreviousRanking.round
                }}
            />

            const team1PreviousValueDiffNumber = <DiffValue
                valueFunc={this.props.rankingParameters.valueFunc}
                previousRanking={team1PreviousRanking}
                lastRanking={team1LastRanking}
                formatter={(this.props.diffFormatter === undefined) ? this.props.rankingParameters.formatter : this.props.diffFormatter}
            />

            team1PreviousValueDiff = <LeagueLink id={this.props.teamLevelDataProps.leagueId()} 
                tableLink={true}
                text={team1PreviousValueDiffNumber} 
                page={this.props.rankingParameters.page} 
                queryParams={{
                    sortingField: this.props.rankingParameters.sortingField,
                    selectedRow: this.props.rankingParameters.positionFunc(team1PreviousRanking),
                    round: team1PreviousRanking.round
                }}
            />

            const team2PreviousPositionDiffValue = <DiffPosition
                positionFunc={this.props.rankingParameters.positionFunc}
                previousRanking={team2PreviousRanking}
                lastRanking={team2LastRanking} />

            team2PreviousPositionDiff = <LeagueLink id={this.props.teamLevelDataProps.leagueId()} 
                tableLink={true}
                text={team2PreviousPositionDiffValue} 
                page={this.props.rankingParameters.page}
                queryParams={{ 
                    sortingField: this.props.rankingParameters.sortingField,
                    selectedRow: this.props.rankingParameters.positionFunc(team2PreviousRanking),
                    round: team2PreviousRanking.round
                }}
            />

            const team2PreviousValueDiffNumber = <DiffValue
                valueFunc={this.props.rankingParameters.valueFunc}
                previousRanking={team2PreviousRanking}
                lastRanking={team2LastRanking}
                formatter={(this.props.diffFormatter === undefined) ? this.props.rankingParameters.formatter : this.props.diffFormatter}
            />

            team2PreviousValueDiff = <LeagueLink id={this.props.teamLevelDataProps.leagueId()} 
                tableLink={true}
                text={team2PreviousValueDiffNumber} 
                page={this.props.rankingParameters.page}
                queryParams={{
                    sortingField: this.props.rankingParameters.sortingField,
                    selectedRow: this.props.rankingParameters.positionFunc(team2PreviousRanking),
                    round: team2PreviousRanking.round
                }}
            />
        }

        return <Card className='mb-3'>
        <Card.Header className='text-center'>
            {this.props.rankingParameters.title}
            {<ChartLink chartContent={this.chartContent} />}
        </Card.Header>
        <Card.Body>
            <Row>
                <Col></Col>
                <Col className='text-center'>
                    <TeamLink id={team1LastRanking.teamId} text={team1LastRanking.teamName} forceRefresh />
                </Col>
                <Col className='text-center'>
                    <TeamLink id={team2LastRanking.teamId} text={team2LastRanking.teamName} forceRefresh />
                </Col>
            </Row>
            <hr className='mt-1 mb-3'/>
            <Row className='mb-2'>
                <Col className='very-small-font text-center'>
                    {this.props.rankingParameters.title}
                </Col>
                <Col className='d-flex flex-row align-items-center justify-content-center'>
                    <span className='me-1 small-font'>
                        {this.props.rankingParameters.formatter(this.props.rankingParameters.valueFunc(team1LastRanking))}
                    </span>
                    <span>
                        <span className='very-small-font'>{team1PreviousValueDiff}</span>
                    </span>
                </Col>
                <Col className='d-flex flex-row align-items-center justify-content-center'>
                    <span className='me-1 small-font'>
                        {this.props.rankingParameters.formatter(this.props.rankingParameters.valueFunc(team2LastRanking))}
                    </span>
                    <span>
                        <span className='very-small-font'>{team2PreviousValueDiff}</span>
                    </span>
                </Col>
            </Row>
            <Row>
                <Col className='very-small-font text-center'>
                    <LeagueLink id={this.props.teamLevelDataProps.leagueId()} text={this.props.teamLevelDataProps.leagueName()} />
                </Col>
                <Col className='d-flex flex-row align-items-center justify-content-center'>
                    <span className="me-1 small-font">
                        <LeagueLink id={this.props.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={(this.props.rankingParameters.positionFunc(team1LastRanking) + 1).toString()}
                            page={this.props.rankingParameters.page} 
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField,
                                selectedRow: this.props.rankingParameters.positionFunc(team1LastRanking),
                                round: team1LastRanking.round
                            }}
                        />
                    </span>
                    <span className='very-small-font'>
                        <span>{team1PreviousPositionDiff}</span>
                    </span>
                </Col>
                <Col className='d-flex flex-row align-items-center justify-content-center'>
                    <span className='me-1 small-font'>                  
                        <LeagueLink id={this.props.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={(this.props.rankingParameters.positionFunc(team2LastRanking) + 1).toString()}
                            page={this.props.rankingParameters.page} 
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField,
                                selectedRow: this.props.rankingParameters.positionFunc(team2LastRanking),
                                round: team2LastRanking.round
                            }}
                        />
                    </span>
                    <span className='very-small-font'>
                        <span>{team2PreviousPositionDiff}</span>
                    </span>
                </Col>
            </Row>
        </Card.Body>
    </Card>
    }
}

export default CompareTeamsTable
