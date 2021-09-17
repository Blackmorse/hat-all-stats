import React from 'react';
import { LeagueUnitTeamStatHistoryInfo } from '../rest/models/team/LeagueUnitTeamStat';
import i18n from '../i18n';
import PlotlyChart from 'react-plotlyjs-ts';
import Section, { SectionState } from '../common/sections/Section';

interface Props {
    leagueUnitTeamStatHistoryInfo?: LeagueUnitTeamStatHistoryInfo
}

class TeamPositionsChartBase extends React.Component<Props, SectionState> {
    constructor(props: Props) {
        super(props)
        this.state={collapsed: false}
    }

    render(): JSX.Element {
        if (this.props.leagueUnitTeamStatHistoryInfo === undefined) {
            return <></>
        }
        let currentRound = this.props.leagueUnitTeamStatHistoryInfo.positionsHistory.reduce((a, b) => a.round > b.round ? a : b).round
        let x = Array.from({length: currentRound}, (_, i) => i + 1)

        let map: Map<string, Array<number>> = new Map()
         
        this.props.leagueUnitTeamStatHistoryInfo.positionsHistory
            .sort((a ,b) => (a.round < b.round) ? -1 : 1)
            .forEach (teamStat => {
                if (map.get(teamStat.teamName) === undefined) {
                    map.set(teamStat.teamName, new Array<number>())
                }
                map.get(teamStat.teamName)?.push(teamStat.position)
            })
        let chartData = Array.from(map.keys()).map(key => {
            return {
                type: 'scatter',
                name: key,
                x: x,
                y: map.get(key)
            }
        })

        let layout = {
            autosize: false,
            width: '100%',
            height: 300,
            margin: {
                t: 20,
                r: 0
            },
            legend: {
                orientation: 'h'
            },
            yaxis: {
                autorange: "reversed",
                nticks: 8,
                dtick: 1,
                title: {
                    text: i18n.t('table.position')
                }
            }
        }
        
        return <PlotlyChart data={chartData} layout={layout} />
    }
}

const TeamPositionsChart = Section(TeamPositionsChartBase, _ => 'table.position')

export default TeamPositionsChart