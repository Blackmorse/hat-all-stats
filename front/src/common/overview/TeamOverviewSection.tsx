import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData';
import OverviewTableSection, { OverviewTableSectionProps } from './OverviewTableSection'

abstract class TeamOverviewSection<Data extends LevelData> extends OverviewTableSection<Data, TeamStatOverview> {
    valueTitle: string
    
    constructor(props: OverviewTableSectionProps<Data, TeamStatOverview>, 
            valueTitle: string) {
        super(props)
        this.valueTitle = valueTitle
    }

    tableheader(): JSX.Element {
        return <Translation>
        {(t, { i18n}) =>  <tr>
                <th>{t('table.team')}</th>
                {(this.isWorldData) ? <th className="value"></th> : <></>}
                <th className="value">{t('table.league')}</th>
                <th className="value">{this.valueTitle}</th>
            </tr>
            }
        </Translation>
    }

    tableRow(teamStat: TeamStatOverview, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <tr key={'team_overview_section_' + this.constructor.name + '_' + teamStat.teamSortingKey.teamId}>
            <td className="to_left">
                <TeamLink text={teamStat.teamSortingKey.teamName} id={teamStat.teamSortingKey.teamId}/>
            </td>
            {leagueNameFunc(teamStat.leagueId)}
            <td className="value">
                <LeagueUnitLink id={teamStat.teamSortingKey.leagueUnitId} text={teamStat.teamSortingKey.leagueUnitName} />
            </td>
            <td className="value">
                {this.props.linkProvider(this.valueFormatter(teamStat.value), this.state.dataRequest.season, this.state.dataRequest.round, teamStat).render()}
            </td>
        </tr>
    }
}

export default TeamOverviewSection