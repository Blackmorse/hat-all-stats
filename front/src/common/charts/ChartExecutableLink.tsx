import { type JSX } from 'react'
import ExecutableComponent  from '../sections/ExecutableComponent'

interface ChartOpenedState {
    chart: boolean
}

interface Props {
    chartContent: () => JSX.Element
}

abstract class ChartExecutableLink<State, DataType, DataRequest> 
    extends ExecutableComponent<Props, State & ChartOpenedState, DataType, DataRequest> {


    closeWindow() {
        this.setState({
            ...this.state,
            chart: false
        })
    }

    openWindow() {
        this.setState({
            ...this.state,
            chart: true
        })
    }

    componentDidMount() {

    }

    private chartWindow(): JSX.Element {
        return <div className="window">
            <span className="close" onClick={() => this.closeWindow()}></span>
            <div className="window_content">
                {this.props.chartContent()}
            </div>
        </div>
    }

    renderSection() {
        return <> 
            <img className="chart_img" src='/chart.svg' onClick={this.openWindow} alt="chart"/>
            {(this.state.chart) ? this.chartWindow() : <></>}
        </>
    }
}

export default ChartExecutableLink
