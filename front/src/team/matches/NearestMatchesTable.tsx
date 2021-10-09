import React from 'react'
import NearestMatch from '../../rest/models/match/NearestMatch'
import TeamLink from '../../common/links/TeamLink'
import ExternalMatchLink from '../../common/links/ExternalMatchLink';
import { dateFormatter } from '../../common/Formatters';
import { SectionState } from '../../common/sections/Section';

interface Props {
    nearestMatches: Array<NearestMatch>
}

class NearestMatchesTable extends React.Component<Props, SectionState> {
    constructor(props: Props) {
        super(props)
        this.state = {collapsed: false}
    }

    matchTableRow(nearestMatch: NearestMatch): JSX.Element {
        let result: string
        if(nearestMatch.status === "FINISHED") {
            result = nearestMatch.homeGoals + " : " + nearestMatch.awayGoals 
        } else {
            result = "-:-"
        }

        return <tr key={"nearest_match_" + nearestMatch.matchId}>
            <td className="matches_date">{dateFormatter(nearestMatch.matchDate)}</td>
            <td className="matches_team"><TeamLink text={nearestMatch.homeTeamName} id={nearestMatch.homeTeamId} forceRefresh={true}/></td>
            <td className="matches_result">{result} <ExternalMatchLink id={nearestMatch.matchId} /></td>
            <td className="matches_team"><TeamLink text={nearestMatch.awayTeamName} id={nearestMatch.awayTeamId} forceRefresh={true}/></td>
        </tr>
    }

    render(): JSX.Element {
        return (
            <div className="statistics_section_inner">
                <table className="statistics_table nearest_matches_table">
                    <tbody>{this.props.nearestMatches.map(this.matchTableRow)}</tbody>
                </table>
            </div>)
    }
}

export default NearestMatchesTable