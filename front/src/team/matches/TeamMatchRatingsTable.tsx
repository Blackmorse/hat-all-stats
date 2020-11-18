import React from 'react'
import TeamMatch from '../../rest/models/match/TeamMatch'
import './TeamMatchRatingsTable.css'
import TeamMatchTableRating from './TeamMatchTableRating'

interface Props {
    teamMatch: TeamMatch
}

class TeamMatchRatingsTable extends React.Component<Props> {
    render() {
        let teamMatch = this.props.teamMatch

        return <div className="team_match_ratings_table">
            <div className="defense_attack"> 
                <TeamMatchTableRating homeRating={teamMatch.homeMatchRatings.ratingLeftDef} 
                    awayRating={teamMatch.awayMatchRatings.ratingRightAtt} />
                <TeamMatchTableRating homeRating={teamMatch.homeMatchRatings.ratingMidDef} 
                    awayRating={teamMatch.awayMatchRatings.ratingMidAtt} />
                <TeamMatchTableRating homeRating={teamMatch.homeMatchRatings.ratingRightDef} 
                    awayRating={teamMatch.awayMatchRatings.ratingLeftAtt} />
            </div>
            <div className="midfield">
                <TeamMatchTableRating homeRating={teamMatch.homeMatchRatings.ratingMidfield} 
                    awayRating={teamMatch.awayMatchRatings.ratingMidfield} />
            </div>
            <div className="defense_attack">
                <TeamMatchTableRating homeRating={teamMatch.homeMatchRatings.ratingLeftAtt} 
                    awayRating={teamMatch.awayMatchRatings.ratingRightDef} />
                <TeamMatchTableRating homeRating={teamMatch.homeMatchRatings.ratingMidAtt} 
                    awayRating={teamMatch.awayMatchRatings.ratingMidDef} />
                <TeamMatchTableRating homeRating={teamMatch.homeMatchRatings.ratingRightAtt} 
                    awayRating={teamMatch.awayMatchRatings.ratingLeftDef} />
            </div>
        </div>
    }
}

export default TeamMatchRatingsTable