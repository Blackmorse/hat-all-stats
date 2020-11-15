import React from 'react'
import './ChartWindow.css'
import TeamRanking from '../../rest/models/team/TeamRanking'
import ValueRankingsChart from './ValueRankingsChart'
import PositionRankingChart from './PositionRankingChart'
import TeamLevelDataProps from '../TeamLevelDataProps'

interface Props {
    divisionLevelRankings: Array<TeamRanking>,
    leagueRankings: Array<TeamRanking>,
    valueFunc: (teamRanking: TeamRanking) => number,
    positionFunc: (teamRanking: TeamRanking) => number,
    title: string,
    teamLevelDataProps: TeamLevelDataProps,
    callback: () => void
}

class ChartWindow extends React.Component<Props> {
    render() {
        return <div className="window">
            <span className="close" onClick={() => {
                this.props.callback()
            }}></span>
            <div className="window_content">
                <ValueRankingsChart 
                    leagueRankings={this.props.leagueRankings}
                    valueFunc={this.props.valueFunc}
                    title={this.props.title}
                    />
                <PositionRankingChart 
                    leagueRankings={this.props.leagueRankings}
                    divisionLevelRankings={this.props.divisionLevelRankings}
                    positionFunc={this.props.positionFunc}
                    teamLevelDataProps={this.props.teamLevelDataProps}
                />
            </div>
        </div>
    }
}

export default ChartWindow