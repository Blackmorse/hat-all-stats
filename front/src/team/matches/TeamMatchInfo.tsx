import React from 'react'
import TeamMatch from '../../rest/models/match/TeamMatch'
import TeamMatchRatingsTable from './TeamMatchRatingsTable'
import TeamLink from '../../common/links/TeamLink'
import './TeamMatchInfo.css'
import StatisticsSection from '../../common/sections/StatisticsSection'
import { LoadingEnum } from '../../common/enums/LoadingEnum'
import { Translation } from 'react-i18next'
import '../../i18n'
import i18n from '../../i18n'
import TeamMatchMetaInfo from './TeamMatchMetaInfo'
import ExternalMatchLink from '../../common/links/ExternalMatchLink'

interface Props {
    teamMatch: TeamMatch
}

class TeamMatchInfo extends StatisticsSection<Props> {
    
    constructor(props: Props) {
        super(props, <>{i18n.t('filter.round')} {props.teamMatch.round}</>)
        this.state={collapsed: false}
    }

    executeDataRequest(dataRequest: {}, callback: (loadingState: LoadingEnum, result?: {} | undefined) => void): void {
        callback(LoadingEnum.OK, {})
    }

    stateFromResult(result?: {} | undefined): {} {
        return {}
    }

    renderContent() {
        let teamMatch = this.props.teamMatch
        return  <Translation>{
            (t, { i18n }) => <div className="match">
            <div className="top_match_info">
                <span className="top_match_info_left_link">
                    <TeamLink id={teamMatch.homeTeam.teamId} text={teamMatch.homeTeam.teamName} forceRefresh={true}/>
                </span>
                <span>{teamMatch.homegoals} - {teamMatch.awayGoals} <ExternalMatchLink id={teamMatch.matchId} /></span> 
                <span className="top_match_info_right_link">
                    <TeamLink id={teamMatch.awayTeam.teamId} text={teamMatch.awayTeam.teamName} forceRefresh={true}/>
                </span>
            </div>
            <div className="bottom_match_info">
                <div className="left_team_match_info">
                    <TeamMatchMetaInfo matchRatings={teamMatch.homeMatchRatings}/>
                    
                </div>
                <div className="middle_team_match_info">
                    <TeamMatchRatingsTable teamMatch={teamMatch} />
                </div>
                <div className="right_team_match_info">
                    <TeamMatchMetaInfo matchRatings={teamMatch.awayMatchRatings}/>
                </div>
            </div>
        </div>
        }
        </Translation>
    }
}

export default TeamMatchInfo