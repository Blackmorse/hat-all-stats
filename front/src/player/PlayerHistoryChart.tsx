import {PlayerChartEntry} from "../rest/models/player/PlayerDetails"
import PlotlyChart from 'react-plotly.js'
import {stringAgeFormatter} from "../common/Formatters"

interface ChartLine {
    valueFunc: (p: PlayerChartEntry) => number,
    formatter: (n: number) => string,
    title: string
    color: string
}

interface Props {
    history?: Array<PlayerChartEntry>,
    chartLines: Array<ChartLine>,
    title: string
}

const PlayerHistoryChart = (props: Props) => {
    if (props.history === undefined) return <></>

    const x = props.history.map(h => h.age).map(age => stringAgeFormatter(age))

    const chartData = props.chartLines.map(chartLine => { return {
        type: 'scatter',
        name: chartLine.title,
        line: {
            color: chartLine.color
        },
        x: x,
        y: props.history!.map(h => chartLine.formatter(chartLine.valueFunc(h)))
    }})


    const layout = {
          autosize: false,
          width: 500,
        title: {
            text: props.title
        },
        showlegend: false,
        xaxis: {
            dtick: '10',
            title: {
                text: props.title
            }, 
            tickangle: '30'
        },
        yaxis: {
            title: {
                text: props.title
            }
        }
    }

    return <PlotlyChart data={chartData} layout={layout} />
}

export default PlayerHistoryChart
