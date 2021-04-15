import React from 'react';
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import DiffPosition from '../../common/widgets/DiffPosition'
import DiffValue from '../../common/widgets/DiffValue'
import LeagueLink from '../../common/links/LeagueLink';
import TeamLevelDataProps from '../TeamLevelDataProps';
import '../overview/RankingTable.css'
import CompareTeamsChartWindow from './CompareTeamsChartWindow'
import RankingParameters from '../../common/ranking/RankingParameters'
import TeamLink from '../../common/links/TeamLink';

interface Props {
    teamComparsion: TeamComparsion,
    teamLevelDataProps: TeamLevelDataProps,
    rankingParameters: RankingParameters,
    diffFormatter?: (value: number) => JSX.Element
}

interface State {
    chart: boolean
}

class CompareTeamsTable extends React.Component<Props, State> {

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
        let team1Rankings = this.props.teamComparsion.team1Rankings
        let team1LastRanking = team1Rankings[team1Rankings.length - 1]
        let team1PreviousRanking = (team1Rankings.length > 1) ? team1Rankings[team1Rankings.length - 2] : undefined
        let team2Rankings = this.props.teamComparsion.team2Rankings
        let team2PreviousRanking = (team2Rankings.length > 1) ? team2Rankings[team2Rankings.length - 2] : undefined
        let team2LastRanking = team2Rankings[team2Rankings.length - 1]

        let team1PreviousPositionDiff: JSX.Element = <></>
        let team1PreviousValueDiff: JSX.Element = <></>

        let team2PreviousPositionDiff: JSX.Element = <></>
        let team2PreviousValueDiff: JSX.Element = <></>
        if (team1PreviousRanking !== undefined && team2PreviousRanking !== undefined) {
            let team1PreviousPositionDiffNumber = <DiffPosition
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

            let team1PreviousValueDiffNumber = <DiffValue
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

            let team2PreviousPositionDiffValue = <DiffPosition
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

            let team2PreviousValueDiffNumber = <DiffValue
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

        return <div className="section_row_half_element">
        <span className="ranking">
        <span className="ranking_name">
            {this.props.rankingParameters.title}
            <img className="chart_img" src='/chart.svg' onClick={() => this.setState({chart: !this.state.chart})} alt=" chart"/>
        </span>
        {(this.state.chart) ? 
                <CompareTeamsChartWindow callback={this.closeWindow}
                    teamComparsion={this.props.teamComparsion}
                    valueFunc={this.props.rankingParameters.valueFunc}
                    positionsFunc={this.props.rankingParameters.positionFunc}
                    title={this.props.rankingParameters.title}
                    teamLevelDataProps={this.props.teamLevelDataProps}
                /> 
                : <></>}
        <div className="comparsion_gridtable">
            <div className="first_line comparsion_gridtable_entry">

            </div>
            <div className="first_line comparsion_gridtable_entry">
                <TeamLink id={team1LastRanking.teamId} text={team1LastRanking.teamName} forceRefresh />
            </div>
            <div className="first_line comparsion_gridtable_entry">
                <TeamLink id={team2LastRanking.teamId} text={team2LastRanking.teamName} forceRefresh />
            </div>
            <div className="comparsion_gridtable_entry comparsion_parameter_name">
                {this.props.rankingParameters.title}
            </div>
            <div className="comparsion_gridtable_entry comparsion_data_cell">
                <span className="value_data">
                    {this.props.rankingParameters.formatter(this.props.rankingParameters.valueFunc(team1LastRanking))}
                </span>
                <span className="diff_data">
                    <span className="comparsion_ranking_row_diff">{team1PreviousValueDiff}</span>
                </span>
            </div>
            <div className="comparsion_gridtable_entry comparsion_data_cell">
                <span className="value_data">
                    {this.props.rankingParameters.formatter(this.props.rankingParameters.valueFunc(team2LastRanking))}
                </span>
                <span className="diff_data">
                    <span className="comparsion_ranking_row_diff">{team2PreviousValueDiff}</span>
                </span>
            </div>
            <div className="comparsion_gridtable_entry comparsion_parameter_name">
                <LeagueLink id={this.props.teamLevelDataProps.leagueId()} text={this.props.teamLevelDataProps.levelData.leagueName} />

            </div>
            <div className="comparsion_gridtable_entry comparsion_data_cell">
                <span className="value_data">
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
                <span className="diff_data">
                    <span className="comparsion_ranking_row_diff">{team1PreviousPositionDiff}</span>
                </span>
            </div>
            <div className="comparsion_gridtable_entry comparsion_data_cell">
                <span className="value_data">                  
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
                <span className="diff_data">
                    <span className="comparsion_ranking_row_diff">{team2PreviousPositionDiff}</span>
                </span>
            </div>
        </div>
    </span>
    </div>
    }
}

export default CompareTeamsTable