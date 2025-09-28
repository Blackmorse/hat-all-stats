import { type JSX } from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import OverviewTableSection, { OverviewTableSectionProps } from './OverviewTableSection'

abstract class TeamOverviewSection extends OverviewTableSection<TeamStatOverview> {
    valueTitle: string
    
    constructor(props: OverviewTableSectionProps<TeamStatOverview>, 
            valueTitle: string) {
        super(props)
        this.valueTitle = valueTitle
    }

    tableheader(): JSX.Element {
        return <Translation>
        {t =>  <tr>
                <th>{t('table.team')}</th>
                {(this.isWorldData) ? <th></th> : <></>}
                <th>{t('table.league')}</th>
                <th>{this.valueTitle}</th>
            </tr>
            }
        </Translation>
    }

    tableRow(teamStat: TeamStatOverview, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <tr key={'team_overview_section_' + this.constructor.name + '_' + teamStat.teamSortingKey.teamId}>
            <td>
                <TeamLink text={teamStat.teamSortingKey.teamName} id={teamStat.teamSortingKey.teamId}/>
            </td>
            {leagueNameFunc(teamStat.leagueId)}
            <td>
                <LeagueUnitLink id={teamStat.teamSortingKey.leagueUnitId} text={teamStat.teamSortingKey.leagueUnitName} />
            </td>
            <td>
                {this.props.linkProvider(this.valueFormatter(teamStat.value), this.state.dataRequest.season, this.state.dataRequest.round, teamStat).render()}
            </td>
        </tr>
    }
}

export default TeamOverviewSection
