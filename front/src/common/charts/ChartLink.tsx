import React from 'react'
import './Charts.css'

interface Props {
    chartContent: () => JSX.Element
}

interface State {
    chart: boolean
}

class ChartOpenerLink extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state = {chart: false}
        this.closeWindow=this.closeWindow.bind(this)
        this.openWindow=this.openWindow.bind(this)
    }

    closeWindow() {
        this.setState({
            chart: false
        })
    }

    openWindow() {
        this.setState({chart: true})
    }

    private chartWindow(): JSX.Element {
        return <div className="window">
            <span className="close" onClick={() => this.closeWindow()}></span>
            <div className="window_content">
                {this.props.chartContent()}
            </div>
        </div>
    }

    render() {
        return <>
            <img className="chart_img" src='/chart.svg' onClick={this.openWindow} alt="chart"/>
            {(this.state.chart) ? this.chartWindow() : <></>}
        </>
    }
}

export default ChartOpenerLink