import React from 'react'
import ExecutableComponent from "../../sections/ExecutableComponent";
import NumbersChartModel from '../../../rest/models/overview/NumbersChartModel'
import { LoadingEnum } from '../../enums/LoadingEnum';
import LevelRequest from '../../../rest/models/request/LevelRequest';
import i18n from '../../../i18n';
import PlotlyChart from 'react-plotlyjs-ts';
import '../../charts/Charts.css'

interface Props {
    levelRequest: LevelRequest
    title: string
    requestFunc: (dataRequest: LevelRequest, callback: (loadingState: LoadingEnum, result?: Array<NumbersChartModel>) => void) => void
    numberFormatter?: (n: number) => number
}

interface State {
    numbers?: Array<NumbersChartModel>
}

class NumbersChart extends ExecutableComponent<Props, State, Array<NumbersChartModel>, LevelRequest> {
    
    constructor(props: Props) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: props.levelRequest
        }       
    }
    
    executeDataRequest(dataRequest: LevelRequest, callback: (loadingState: LoadingEnum, result?: NumbersChartModel[]) => void): void {
        this.props.requestFunc(dataRequest, callback)
    }

    renderSection(): JSX.Element {
        if(this.state.numbers === undefined) {
            return <></>
        }
        let formatter = this.props.numberFormatter === undefined ? (n: number) => n : this.props.numberFormatter

        let x = this.state.numbers.map(numb => i18n.t('filter.season') + ' ' + numb.season + ' ' + i18n.t("chart.round") + ' ' + numb.round)
        let y = this.state.numbers.map(numb => numb.count)

        let chartData = [
            {
                type: 'scatter',
                x: x,
                y: y.map(formatter)
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
              }
        }

        return <div className="plotly_wrapper">
            <PlotlyChart data={chartData} layout={layout}/>
            </div>
    }

    stateFromResult(result?: Array<NumbersChartModel>): State {
        return {
            numbers: result
        }
    }
}

export default NumbersChart