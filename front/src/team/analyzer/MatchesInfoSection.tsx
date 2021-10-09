import React from 'react'
import CombinedMatchesInfoSection from './CombinedMatchesInfo'

interface Props {
    firstTeamId: number,
    firstMatchId: number,
    secondTeamId: number,
    secondMatchId: number
}

class MatchesInfoSection extends React.Component<Props> {
    render() {
        return <CombinedMatchesInfoSection firstTeamId={this.props.firstTeamId} secondTeamId={this.props.secondTeamId}
            firstMatchId={this.props.firstMatchId} secondMatchId={this.props.secondMatchId} />
    }
}

export default MatchesInfoSection