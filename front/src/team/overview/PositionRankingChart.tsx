import React from 'react'
import TeamRanking from '../../rest/models/team/TeamRanking'
import PlotlyChart from 'react-plotly.js';
import TeamLevelDataProps from '../TeamLevelDataProps';
import i18n from '../../i18n';
import { toRoman } from "../../common/Utils"

interface Props {
    leagueRankings: Array<TeamRanking>,
    divisionLevelRankings: Array<TeamRanking>,
    positionFunc: (teamRanking: TeamRanking) => number,
    teamLevelDataProps: TeamLevelDataProps
}

class PositionRankingChart extends React.Component<Props, {}> {
    render() {
        const seasons = this.props.leagueRankings.map(l => l.season)
        const uniqSeasons = Array.from(new Set( seasons))
        let x = uniqSeasons.length > 1
            ? this.props.leagueRankings.map((_, index) => index)
            : this.props.leagueRankings.map((l) => l.round)

        let leagueY = this.props.leagueRankings.map(v => this.props.positionFunc(v) + 1)
        let divisionLevelY = this.props.divisionLevelRankings.map(v => this.props.positionFunc(v) + 1)

        const leagueChart = {
            type: 'scatter',
            name: this.props.teamLevelDataProps.leagueName(),
            x: x,
            y: leagueY,
            marker: {
                color: 'orange'
            }
        }

        const divisionLevelChart = {
            type: 'scatter',
            name: toRoman(this.props.divisionLevelRankings[0].divisionLevel),
            x: x,
            y: divisionLevelY,
            marker: {
                color: 'green'
            }
        }

        const chartData = (uniqSeasons.length > 1)
            ? [leagueChart]
            : [leagueChart, divisionLevelChart]

        let layout = {
            title: {
                text: i18n.t('table.position')
            },
            xaxis: {
                title: {
                  text: i18n.t('chart.round')
                }
              },
            yaxis: {
                autorange: "reversed",
                title: {
                    text: i18n.t('table.position')
                }
            }
        }

        return <div className="plotly_wrapper">
            <PlotlyChart data={chartData} layout={layout} />
            </div>
    }
}

export default PositionRankingChart
