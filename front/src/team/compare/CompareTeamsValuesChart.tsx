import React from 'react';
import PlotlyChart from 'react-plotlyjs-ts';
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import TeamRanking from '../../rest/models/team/TeamRanking';

interface Props {
    teamComparsion: TeamComparsion,
    valueFunc: (teamRanking: TeamRanking) => number,
    title: string
}

class CompareTeamsValuesChart extends React.Component<Props> {
    render() {
        let team1Rankings = this.props.teamComparsion.team1Rankings
        let team2Rankings = this.props.teamComparsion.team2Rankings

        let x = team1Rankings.map(ranking => 'Season ' + ranking.season + ' round ' + ranking.round)
        let team1Y = team1Rankings.map(ranking => this.props.valueFunc(ranking))
        let team2Y = team2Rankings.map(ranking => this.props.valueFunc(ranking))

        let chartData = [
            {
                type: 'scatter',
                name: team1Rankings[team1Rankings.length - 1].teamName,
                x: x,
                y: team1Y,
                marker: {
                    color: 'orange'
                },
                
            },
            {
                type: 'scatter',
                name: team2Rankings[team2Rankings.length - 1].teamName,
                x: x,
                y: team2Y,
                marker: {
                    color: 'green'
                },
                
            }
        ]

        let layout = {
            title: {
                text: this.props.title
            },
            xaxis: {
                tickangle: '30',
                tickfont: {
                    size: 10
                },
              },
            yaxis: {
                autorange: "reversed",
                title: {
                    text: this.props.title
                }
            }
        }

        return <PlotlyChart data={chartData} layout={layout} />        
    }
}

export default CompareTeamsValuesChart