import React from 'react'
import TeamMatchInfo from './TeamMatchInfo'
import i18n from '../../i18n'
import SingleMatch from '../../rest/models/match/SingleMatch'
import TeamMatch from '../../rest/models/match/TeamMatch'
import Section, { GSection, SectionState } from '../../common/sections/Section'

interface Props {
    teamMatch: TeamMatch
}

class TeamMatchInfoComponent extends React.Component<Props, SectionState> {

    constructor(props: Props) {
        super(props)
        this.state={collapsed: false}
    }

    render(): JSX.Element {
        let teamMatch = this.props.teamMatch
        let singleMatch: SingleMatch = {
            homeTeamId: teamMatch.homeTeam.teamId,
            homeTeamName: teamMatch.homeTeam.teamName,
            homeGoals: teamMatch.homegoals,
            awayTeamId: teamMatch.awayTeam.teamId,
            awayTeamName: teamMatch.awayTeam.teamName,
            awayGoals: teamMatch.awayGoals,
            matchId: teamMatch.matchId,
            homeMatchRatings: teamMatch.homeMatchRatings,
            awayMatchRatings: teamMatch.awayMatchRatings
        }

        return <TeamMatchInfo singleMatch={singleMatch} />
    }

}

const TeamMatchInfoSection = Section<Props, SectionState, GSection<Props, SectionState>>(TeamMatchInfoComponent, 
    (p) => { return {header: <>{i18n.t('filter.round')} {p.teamMatch.round}</> } })

export default TeamMatchInfoSection