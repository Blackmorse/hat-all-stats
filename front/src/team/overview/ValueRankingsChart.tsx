import TeamRanking from '../../rest/models/team/TeamRanking'
import React from 'react';
import PlotlyChart from 'react-plotly.js';
import i18n from '../../i18n';

interface Props {
  leagueRankings: Array<TeamRanking>,
  valueFunc: (teamRanking: TeamRanking) => number,
  title: string,
  formatterFunc?: (n: number) => number
}

class RankingsChart extends React.Component<Props, {}> {

    render() {
        const seasons = this.props.leagueRankings.map(l => l.season)
        const uniqSeasons = Array.from(new Set( seasons))

        let x = uniqSeasons.length > 1
            ? this.props.leagueRankings.map((_, index) => index)
            : this.props.leagueRankings.map((l) => l.round)

        let yy = this.props.leagueRankings.map(this.props.valueFunc)
        let y = (this.props.formatterFunc !== undefined) ? yy.map(this.props.formatterFunc) : yy
        let chartData = [{
            type: 'scatter',
            x: x,
            y: y,
            marker: {
              color: 'green'
            }
        }]

        var layout = {
            title: {
              text: this.props.title
            },
            showlegend: false,
            xaxis: {
              dtick: 1,
              title: {
                text: i18n.t('chart.round')
              }
            },
            yaxis: {
              title: {
                text: this.props.title
              }
            }
          };

        return <div className="plotly_wrapper">
          <PlotlyChart data={chartData} layout={layout} />
          </div>
    }
}

export default RankingsChart
