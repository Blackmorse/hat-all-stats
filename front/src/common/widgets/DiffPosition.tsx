import React from 'react'
import TeamRanking from '../../rest/models/team/TeamRanking'

interface Props {
    positionFunc: (teamRanking: TeamRanking) => number,
    previousRanking: TeamRanking,
    lastRanking: TeamRanking
}

class DiffPosition extends React.Component<Props> {
    render() {
        const positionFunc = this.props.positionFunc
        if(positionFunc(this.props.previousRanking) > positionFunc(this.props.lastRanking)) {
            return <>
                <img className="trend_up" src="/trend-green.png" alt="up"/>
                {(positionFunc(this.props.lastRanking) - positionFunc(this.props.previousRanking))}
            </>
        } else if (positionFunc(this.props.previousRanking) < positionFunc(this.props.lastRanking)) {
            return <>
                <img className="trend_down" src="/trend-red.png" alt="down"/>
                +{positionFunc(this.props.lastRanking) - positionFunc(this.props.previousRanking)}
            </>
        } else {
            return <>
                <img src="/trend-gray.png" alt="same" />+0
            </>
        }
    }
}

export default DiffPosition