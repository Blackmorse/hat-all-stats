import React from 'react'
import TeamRanking from '../../rest/models/team/TeamRanking'
import PlotlyChart from 'react-plotlyjs-ts';
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
        let x = this.props.leagueRankings.map(l => l.round)

        let leagueY = this.props.leagueRankings.map(v => this.props.positionFunc(v) + 1)
        let divisionLevelY = this.props.divisionLevelRankings.map(v => this.props.positionFunc(v) + 1)

        let chartData = [
            {
                type: 'scatter',
                name: this.props.teamLevelDataProps.levelData.leagueName,
                x: x,
                y: leagueY,
                marker: {
                    color: 'orange'
                }
            },
            {
                type: 'scatter',
                name: toRoman(this.props.divisionLevelRankings[0].divisionLevel),
                x: x,
                y: divisionLevelY,
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

        return <PlotlyChart data={chartData} layout={layout} />
    }
}

export default PositionRankingChart