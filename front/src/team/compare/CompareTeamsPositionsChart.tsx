import React from 'react';
import PlotlyChart from 'react-plotly.js';
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import TeamRanking from '../../rest/models/team/TeamRanking';
import TeamLevelDataProps from '../TeamLevelDataProps';
import i18n from '../../i18n';

interface Props {
    teamComparsion: TeamComparsion,
    positionFunc: (teamRanking: TeamRanking) => number,
    teamLevelDataProps: TeamLevelDataProps,
}

class CompareTeamsPositionsChart extends React.Component<Props> {
    render() {
        const team1Rankings = this.props.teamComparsion.team1Rankings
        const team2Rankings = this.props.teamComparsion.team2Rankings

        const x = team1Rankings.map(ranking => i18n.t('filter.season') + ' ' + ranking.season + ' ' + i18n.t("chart.round") + ' ' + ranking.round)
        const team1Y = team1Rankings.map(ranking => this.props.positionFunc(ranking))
        const team2Y = team2Rankings.map(ranking => this.props.positionFunc(ranking))
    
        const chartData = [
            {
                type: 'scatter',
                name: team1Rankings[team1Rankings.length - 1].teamName,
                x: x,
                y: team1Y,
                marker: {
                    color: 'orange'
                }
            },
            {
                type: 'scatter',
                name: team2Rankings[team2Rankings.length - 1].teamName,
                x: x,
                y: team2Y,
                marker: {
                    color: 'green'
                }
            }
        ]

        const layout = {
            title: {
                text: i18n.t('table.position')
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
                    text: this.props.teamLevelDataProps.leagueName()
                }
            }
        }

        return <div className="plotly_wrapper">
            <PlotlyChart data={chartData} layout={layout} />
            </div>
    }
}

export default CompareTeamsPositionsChart
