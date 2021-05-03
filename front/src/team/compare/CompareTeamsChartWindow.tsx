import React from 'react'
import TeamComparsion from '../../rest/models/team/TeamComparsion'
import TeamRanking from '../../rest/models/team/TeamRanking'
import '../overview/ChartWindow.css'
import CompareTeamsValuesChart from './CompareTeamsValuesChart'
import CompareTeamsPositionsChart from './CompareTeamsPositionsChart'
import TeamLevelDataProps from '../TeamLevelDataProps'

interface Props {
    teamComparsion: TeamComparsion,
    callback: () => void,
    valueFunc: (ranking: TeamRanking) => number,
    positionsFunc: (ranking: TeamRanking) => number,
    title: string,
    teamLevelDataProps: TeamLevelDataProps,
    yAxisFunc?: (y: number) => number
}

class CompareTeamsChartWindow extends React.Component<Props> {
    render() {
        return <div className="window">
            <span className="close" onClick={() => {
                this.props.callback()
            }}></span>
            <div className="window_content">
                <CompareTeamsValuesChart 
                    teamComparsion={this.props.teamComparsion}
                    valueFunc={this.props.valueFunc}
                    title={this.props.title}
                    formatterFunc={this.props.yAxisFunc}
                />
                <CompareTeamsPositionsChart 
                    teamComparsion={this.props.teamComparsion}
                    positionFunc={this.props.positionsFunc}
                    teamLevelDataProps={this.props.teamLevelDataProps}
                />
            </div>
        </div>
    }
}

export default CompareTeamsChartWindow