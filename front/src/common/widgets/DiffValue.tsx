import React from 'react'
import TeamRanking from '../../rest/models/team/TeamRanking'

interface Props {
    valueFunc: (teamRanking: TeamRanking) => number,
    formatter: (value: number) => JSX.Element,
    previousRanking: TeamRanking,
    lastRanking: TeamRanking
}

class DiffValue extends React.Component<Props> {
    render() {
        let valueFunc = this.props.valueFunc
        let formatter = this.props.formatter
        if(valueFunc(this.props.previousRanking) > valueFunc(this.props.lastRanking)) {
            return <>
                <img className='trend trend_down' src="/trend-red.png" alt="down" />
                -{formatter((valueFunc(this.props.previousRanking) - valueFunc(this.props.lastRanking)))}
            </>
        } else if(valueFunc(this.props.lastRanking) > valueFunc(this.props.previousRanking)) {
            return <>
                <img className='trend trend_up' src="/trend-green.png" alt="up" />
                +{formatter(valueFunc(this.props.lastRanking) - valueFunc(this.props.previousRanking))}
            </>
        } else {
            return <>
                <img className='trend' src="/trend-gray.png" alt="same" />
                +0
            </>
        }
    }
}

export default DiffValue