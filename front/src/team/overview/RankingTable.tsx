import React from 'react';
import TeamRanking from '../../rest/models/team/TeamRanking'
import { Translation } from 'react-i18next'
import '../../i18n'
import './RankingTable.css'
import '../../common/elements/Trends.css'
import TeamLevelDataProps from '../TeamLevelDataProps';
import LeagueLink from '../../common/links/LeagueLink';
import DivisionLevelLink from '../../common/links/DivisionLevelLink';
import ChartWindow from './ChartWindow'
import { toRoman } from "../../common/Utils"
import DiffPosition from '../../common/widgets/DiffPosition'
import DiffValue from '../../common/widgets/DiffValue'
import RankingParameters from '../../common/ranking/RankingParameters';

export interface RankingData {
    teamRankings: Array<TeamRanking>,
    round: number,
    teamLevelDataProps: TeamLevelDataProps,
    leagueTeamsCount: number,
    divisionLevelTeamsCount: number
}

interface Props {
    rankingData: RankingData,
    rankingParameters: RankingParameters
}

interface State {
    chart: boolean
}

class RankingTable extends React.Component<Props, State> {
    
    constructor(props: Props) {
        super(props)
        this.state = {chart: false}
        this.closeWindow=this.closeWindow.bind(this)
    }

    closeWindow() {
        this.setState({
            chart: false
        })
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

        let diffValueContent: JSX.Element
        let divisionLevelDiffPositionContent: JSX.Element
        if (previousDivisionLevelRanking) {
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

        let leagueDiffPositionContent: JSX.Element
        if(previousLeagueRanking) {
            leagueDiffPositionContent = <DiffPosition 
                positionFunc={positionFunc}
                previousRanking={previousLeagueRanking}
                lastRanking={lastLeagueRanking} />
        }

            return <Translation>{
                (t, { i18n }) => 
        <span className="ranking">
            <span className="ranking_name">
                {this.props.rankingParameters.title}
                <img className="chart_img" src='/chart.svg' onClick={() => this.setState({chart: !this.state.chart})} alt=" chart"/>
            </span>
            {(this.state.chart) ? 
                <ChartWindow callback={this.closeWindow}
                    divisionLevelRankings={divisionLevelRankings}
                    leagueRankings={leagueRankings}
                    valueFunc={this.props.rankingParameters.valueFunc}
                    positionFunc={this.props.rankingParameters.positionFunc}
                    title={this.props.rankingParameters.title} 
                    teamLevelDataProps={this.props.rankingData.teamLevelDataProps}
                    yAxisfunc={this.props.rankingParameters.yAxisFunc}
                /> 
                : <></>}
            <table className="ranking_table">
                <tbody>
                <tr className="ranking_row">
                    <td className="ranking_row_name">{this.props.rankingParameters.title}</td>
                    <td className="ranking_row_value">{formatter(valueFunc(lastDivisionLevelRanking))}</td>
                    <td className="ranking_row_diff">
                        <div className="ranking_row_diff_value">
                            {diffValueContent}
                        </div>
                    </td>
                </tr>
                <tr className="ranking_row">
                    <td className="ranking_row_name">
                        <LeagueLink  tableLink={true}
                            id={this.props.rankingData.teamLevelDataProps.leagueId()}
                            text={this.props.rankingData.teamLevelDataProps.levelData.leagueName}
                            />
                    </td>
                    <td className="ranking_row_value">
                        <LeagueLink id={this.props.rankingData.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={(positionFunc(lastLeagueRanking) + 1).toString()} 
                            page={this.props.rankingParameters.page} 
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField,
                                selectedRow: positionFunc(lastLeagueRanking),
                                round: this.props.rankingData.round
                            }}
                        />
                        /{this.props.rankingData.leagueTeamsCount}
                    </td>
                    <td className="ranking_row_diff">
                        {(previousLeagueRanking) ? <LeagueLink id={this.props.rankingData.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={leagueDiffPositionContent} 
                            page={this.props.rankingParameters.page}
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField,
                                selectedRow: positionFunc(previousLeagueRanking),
                                round: this.props.rankingData.round - 1
                            }}
                        /> : <></>
                        }
                    </td>
                </tr>
                <tr className="ranking_row">
                    <td className="ranking_row_name">
                        <DivisionLevelLink
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={lastDivisionLevelRanking.divisionLevel}
                            text={toRoman(lastDivisionLevelRanking.divisionLevel)}
                            />
                    </td>
                    <td className="ranking_row_value">
                        <DivisionLevelLink 
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={lastDivisionLevelRanking.divisionLevel}
                            text={(positionFunc(lastDivisionLevelRanking) + 1).toString()}
                            page={this.props.rankingParameters.page}
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField, 
                                selectedRow: positionFunc(lastDivisionLevelRanking),
                                round: this.props.rankingData.round
                            }}
                        />/{this.props.rankingData.divisionLevelTeamsCount}
                    </td>
                    <td className="ranking_row_diff">
                    {(previousDivisionLevelRanking) ? <DivisionLevelLink 
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={this.props.rankingData.teamLevelDataProps.levelData.divisionLevel}
                            text={divisionLevelDiffPositionContent}
                            page={this.props.rankingParameters.page}
                            queryParams={{
                                sortingField: this.props.rankingParameters.sortingField, 
                                selectedRow: positionFunc(previousDivisionLevelRanking),
                                round: this.props.rankingData.round - 1
                            }}
                        /> : <></>
                    }
                    </td>
                </tr>
                </tbody>
            </table>
        </span>
        }
        </Translation>
    }
}

export default RankingTable