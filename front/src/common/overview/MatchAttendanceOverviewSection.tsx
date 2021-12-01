import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import OverviewTableSection from './OverviewTableSection'
import LeagueUnitLink from '../links/LeagueUnitLink'
import TeamLink from '../links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData';
import MatchAttendanceOverview from '../../rest/models/overview/MatchAttendanceOverview'
import { getTopMatchAttendance } from '../../rest/Client'
import ExternalMatchLink from '../links/ExternalMatchLink'
import { commasSeparated } from '../Formatters'
import Section from '../sections/Section'

class MatchAttendanceOverviewSectionBase<Data extends LevelData> extends OverviewTableSection<Data, MatchAttendanceOverview> {
    
    loadOverviewEntity = getTopMatchAttendance

    valueFormatter(value: number): JSX.Element {
        return commasSeparated(value)
    }


    tableheader(): JSX.Element {
        return <Translation>
        {(t, { i18n}) =>  <tr>
                    {(this.isWorldData)  ? <th>{t('overview.country')}</th> : <></>}
                    <th>{t('table.league')}</th>
                    <th>{t('matches.spectatos')}</th>
                    <th>{t('table.team')}</th>
                    <th></th>
                    <th>{t('table.team')}</th>
                </tr>
        }
        </Translation>
    }

    tableRow(matc: MatchAttendanceOverview, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <tr key={'match_attendance_overview_section_' + matc.matchId}>
            {leagueNameFunc(matc.leagueId)}
            <td>
                <LeagueUnitLink id={matc.homeTeams.leagueUnitId} text={matc.homeTeams.leagueUnitName} />
            </td>
            <td>
                {this.props.linkProvider(commasSeparated(matc.spectators), this.state.dataRequest.season, this.state.dataRequest.round, matc).render()}
            </td>
            <td>
                <TeamLink id={matc.homeTeams.teamId} text={matc.homeTeams.teamName}/>
                </td>
            <td>
                {matc.homeGoals} : {matc.awayGoals} <ExternalMatchLink id={matc.matchId}/>
            </td>
            <td><TeamLink id={matc.awayTeam.teamId} text={matc.awayTeam.teamName}/></td>
        </tr>
    }
}

const MatchAttendanceOverviewSection = Section(MatchAttendanceOverviewSectionBase, _ => 'overview.attendance')
export default MatchAttendanceOverviewSection