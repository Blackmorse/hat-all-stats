import React from 'react'
import MatchTopHatstats from '../../../../rest/models/match/MatchTopHatstats';
import { loddarStats } from '../../../Formatters';
import ExternalMatchLink from '../../../links/ExternalMatchLink';
import HattidLink from '../../../links/HattidLink';
import LeagueUnitLink from '../../../links/LeagueUnitLink';
import TeamLink from '../../../links/TeamLink';
import MatchRow, { TableRowProps } from './MatchRow';

interface Props extends TableRowProps<MatchTopHatstats> {
    request: {round: number, season: number}
    linkProvider: (text: string | JSX.Element, season: number, round: number, entity: MatchTopHatstats) => HattidLink<any>,
    leagueNameFunc: (id: number) => JSX.Element | undefined
}


class MatchOverviewRow extends MatchRow<MatchTopHatstats, Props> {
    columns(rowIndex: number, matc: MatchTopHatstats): JSX.Element[] {
        let country = this.props.leagueNameFunc(matc.homeTeam.leagueId)
        return [
            (country === undefined ) ? <></> : country ,
            <td className="text-center">
                <LeagueUnitLink id={matc.homeTeam.leagueUnitId} text={matc.homeTeam.leagueUnitName} />
            </td>,
            <td className="text-center">
                <TeamLink id={matc.homeTeam.teamId} text={matc.homeTeam.teamName}/>
                </td>,
            <td className="text-center">
                {this.props.linkProvider(loddarStats(matc.homeLoddarStats), this.props.request.season, this.props.request.round, matc).render()}
            </td>,
            <td className="text-center">
                {this.props.linkProvider(matc.homeHatstats.toString(), this.props.request.season, this.props.request.round, matc).render()}
            </td>,
            <td className="text-center">
                {matc.homeGoals} : {matc.awayGoals} <ExternalMatchLink id={matc.matchId}/>
            </td>,
            <td className="text-center">
                {this.props.linkProvider(matc.awayHatstats.toString(), this.props.request.season, this.props.request.round, matc).render()}
            </td>,
            <td className="text-center">
                {this.props.linkProvider(loddarStats(matc.awayLoddarStats), this.props.request.season, this.props.request.round, matc).render()}
            </td>,
            <td className="text-center"><TeamLink id={matc.awayTeam.teamId} text={matc.awayTeam.teamName}/></td>
        ]
    }

}

export default MatchOverviewRow