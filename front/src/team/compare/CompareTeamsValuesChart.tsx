import React from 'react';
import PlotlyChart from 'react-plotly.js';
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import TeamRanking from '../../rest/models/team/TeamRanking';
import i18n from '../../i18n';

interface Props {
    teamComparsion: TeamComparsion,
    valueFunc: (teamRanking: TeamRanking) => number,
    title: string,
    formatterFunc?: (n: number) => number
}

class CompareTeamsValuesChart extends React.Component<Props> {
    render() {
        const team1Rankings = this.props.teamComparsion.team1Rankings
        const team2Rankings = this.props.teamComparsion.team2Rankings

        const x = team1Rankings.map(ranking => i18n.t('filter.season') + ' ' + ranking.season + ' ' + i18n.t("chart.round") + ' ' + ranking.round)
        let team1Y = team1Rankings.map(ranking => this.props.valueFunc(ranking))
        let team2Y = team2Rankings.map(ranking => this.props.valueFunc(ranking))

        team1Y = (this.props.formatterFunc !== undefined) ? team1Y.map(this.props.formatterFunc) : team1Y
        team2Y = (this.props.formatterFunc !== undefined) ? team2Y.map(this.props.formatterFunc) : team2Y

        const chartData = [
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

        const layout = {
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
                title: {
                    text: this.props.title
                }
            }
        }

        return <div className="plotly_wrapper">
            <PlotlyChart data={chartData} layout={layout} />        
            </div>
    }
}

export default CompareTeamsValuesChart
