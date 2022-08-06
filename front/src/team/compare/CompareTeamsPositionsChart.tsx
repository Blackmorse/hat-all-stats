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
        let team1Rankings = this.props.teamComparsion.team1Rankings
        let team2Rankings = this.props.teamComparsion.team2Rankings

        let x = team1Rankings.map(ranking => i18n.t('filter.season') + ' ' + ranking.season + ' ' + i18n.t("chart.round") + ' ' + ranking.round)
        let team1Y = team1Rankings.map(ranking => this.props.positionFunc(ranking))
        let team2Y = team2Rankings.map(ranking => this.props.positionFunc(ranking))
    
        let chartData = [
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

        let layout = {
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
