import React from 'react'
import './TeamMatchTableRating.css'

interface Props {
    homeRating: number,
    awayRating: number
}

class TeamMatchTableRating extends React.Component<Props> {
    render() {

        const leftRightRate = Math.floor(100 * this.props.homeRating / (this.props.homeRating + this.props.awayRating))

        return <span className="ratings_ratio">
            <div className="team_rating home" style={{width: leftRightRate.toString() + '%'}}>
                <span className="team_rating_hatstats">
                    {this.props.homeRating}
                </span>
                <span className="team_rating_percents">
                    {leftRightRate}%
                </span>
            </div>
            <div className="team_rating away" style={{width: (100 - leftRightRate).toString() + '%'}}>
                <span className="team_rating_hatstats">
                    {this.props.awayRating}
                </span>
                <span className="team_rating_percents">
                    {100 - leftRightRate}%
                </span>
            </div>
        </span>
    }
}

export default TeamMatchTableRating