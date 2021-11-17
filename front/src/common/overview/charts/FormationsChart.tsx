import React from 'react'
import ExecutableComponent from "../../sections/ExecutableComponent";
import FormationChartModel from '../../../rest/models/overview/FormationChartModel'
import LevelRequest from '../../../rest/models/request/LevelRequest';
import { LoadingEnum } from '../../enums/LoadingEnum';
import { formationsChart } from '../../../rest/Client'
import i18n from '../../../i18n';
import PlotlyChart from 'react-plotlyjs-ts';
import '../../charts/Charts.css'

interface Props {
    levelRequest: LevelRequest
}

interface State {
    formations?: Array<FormationChartModel>
}

class FormationsChart extends ExecutableComponent<Props, State, Array<FormationChartModel>, LevelRequest> {
    constructor(props: Props) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: props.levelRequest
        }       
    }

    executeDataRequest(dataRequest: LevelRequest, callback: (loadingState: LoadingEnum, result?: Array<FormationChartModel>) => void): void {
        formationsChart(dataRequest, callback)
    }

    stateFromResult(result?: Array<FormationChartModel>): State {
        return {
            formations: result
        }
    }

    formName(sr: {season: number, round: number}): string {
        return i18n.t('filter.season') + ' ' + sr.season + ' ' + i18n.t("chart.round") + ' ' + sr.round
    }

    renderSection(): JSX.Element {
        if(this.state.formations === undefined) {
            return <></>
        }

        let x = Array.from(new Set(this.state.formations.map(this.formName)))

        let allFormations = Array.from(new Set(this.state.formations.map(f => f.formation)))

        let chartData = allFormations.map(formation => {
            let formationsArray = this.state.formations?.filter(f => f.formation === formation)
            return {
                type: 'scatter',
                name: formation,
                x: x,
                //filling gaps for (season, round)
                y: x.map(xx => {
                    let xxIndex = formationsArray!.map(f => this.formName({season: f.season, round: f.round})).indexOf(xx)
                    return xxIndex > -1 ? formationsArray![xxIndex].count  : 0 
                    })
            }
        })

        let layout = {
            title: {
                text: i18n.t('overview.formations')
            },
            xaxis: {
                tickangle: '30',
                tickfont: {
                    size: 10
                },
            }
        }

        return <div className="plotly_wrapper">
            <PlotlyChart data={chartData} layout={layout} />
        </div>
    }
}

export default FormationsChart