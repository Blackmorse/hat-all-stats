import React from 'react'
import TeamMatchRatingsTable from './TeamMatchRatingsTable'
import TeamLink from '../../common/links/TeamLink'
import './TeamMatchInfo.css'
import { Translation } from 'react-i18next'
import '../../i18n'
import TeamMatchMetaInfo from './TeamMatchMetaInfo'
import ExternalMatchLink from '../../common/links/ExternalMatchLink'
import SingleMatch from '../../rest/models/match/SingleMatch'
import MatchSimulatorInfo from './MatchSimulatorInfo'

interface Props {
    singleMatch: SingleMatch
}

class TeamMatchInfo extends React.Component<Props> {

    render() {
        let singleMatch = this.props.singleMatch
        
        return  <Translation>{
            (t, { i18n }) => <div className="match">
            <div className="top_match_info">
                <span className="top_match_info_left_link">
                    <TeamLink id={singleMatch.homeTeamId} text={singleMatch.homeTeamName} forceRefresh={true}/>
                </span>
                <span>{singleMatch.homeGoals} - {singleMatch.awayGoals} <ExternalMatchLink id={singleMatch.matchId} /></span> 
                <span className="top_match_info_right_link">
                    <TeamLink id={singleMatch.awayTeamId} text={singleMatch.awayTeamName} forceRefresh={true}/>
                </span>
            </div>
            <div className="predict_match_info">
                
                <MatchSimulatorInfo matchId={singleMatch.matchId}/>
            </div>
            <div className="bottom_match_info">
                <div className="left_team_match_info">
                    <TeamMatchMetaInfo matchRatings={singleMatch.homeMatchRatings}/>
                    
                </div>
                <div className="middle_team_match_info">
                    <TeamMatchRatingsTable homeMatchRatings={singleMatch.homeMatchRatings} awayMatchRatings={singleMatch.awayMatchRatings} />
                </div>
                <div className="right_team_match_info">
                    <TeamMatchMetaInfo matchRatings={singleMatch.awayMatchRatings}/>
                </div>
            </div>
        </div>
        }
        </Translation>
    }
}

export default TeamMatchInfo