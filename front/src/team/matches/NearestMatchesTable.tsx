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

        return <tr key={'nearest_match_' + nearestMatch.matchId}>
            <td>{dateFormatter(nearestMatch.matchDate)}</td>
            <td><TeamLink text={nearestMatch.homeTeamName} id={nearestMatch.homeTeamId} forceRefresh={true}/></td>
            <td>{result} <ExternalMatchLink id={nearestMatch.matchId} /></td>
            <td><TeamLink text={nearestMatch.awayTeamName} id={nearestMatch.awayTeamId} forceRefresh={true}/></td>
        </tr>
    }

    render(): JSX.Element {
        return  <table className='table table-striped table-rounded table-sm small text-center'>
                    <tbody>{this.props.nearestMatches.map(this.matchTableRow)}</tbody>
            </table>
           
    }
}

export default NearestMatchesTable