import React from 'react'
import { MatchRatings } from '../../rest/models/match/TeamMatch'
import './TeamMatchRatingsTable.css'
import TeamMatchTableRating from './TeamMatchTableRating'

interface Props {
    homeMatchRatings: MatchRatings,
    awayMatchRatings: MatchRatings
}

class TeamMatchRatingsTable extends React.Component<Props> {
    render() {
        return <div className="team_match_ratings_table">
            <div className="defense_attack"> 
                <TeamMatchTableRating homeRating={this.props.homeMatchRatings.ratingLeftDef} 
                    awayRating={this.props.awayMatchRatings.ratingRightAtt} />
                <TeamMatchTableRating homeRating={this.props.homeMatchRatings.ratingMidDef} 
                    awayRating={this.props.awayMatchRatings.ratingMidAtt} />
                <TeamMatchTableRating homeRating={this.props.homeMatchRatings.ratingRightDef} 
                    awayRating={this.props.awayMatchRatings.ratingLeftAtt} />
            </div>
            <div className="midfield">
                <TeamMatchTableRating homeRating={this.props.homeMatchRatings.ratingMidfield} 
                    awayRating={this.props.awayMatchRatings.ratingMidfield} />
            </div>
            <div className="defense_attack">
                <TeamMatchTableRating homeRating={this.props.homeMatchRatings.ratingLeftAtt} 
                    awayRating={this.props.awayMatchRatings.ratingRightDef} />
                <TeamMatchTableRating homeRating={this.props.homeMatchRatings.ratingMidAtt} 
                    awayRating={this.props.awayMatchRatings.ratingMidDef} />
                <TeamMatchTableRating homeRating={this.props.homeMatchRatings.ratingRightAtt} 
                    awayRating={this.props.awayMatchRatings.ratingLeftDef} />
            </div>
        </div>
    }
}

export default TeamMatchRatingsTable