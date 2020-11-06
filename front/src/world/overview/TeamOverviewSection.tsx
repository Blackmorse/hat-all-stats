import React from 'react'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import '../../i18n'
import { Translation } from 'react-i18next'
import WorldData from '../../rest/models/leveldata/WorldData';
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import LeagueLink from '../../common/links/LeagueLink'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'

abstract class TeamOverviewSection extends OverviewSection<WorldData, Array<TeamStatOverview>> {
    valueTitle: string
    
    constructor(props: OverviewSectionProps<WorldData, Array<TeamStatOverview>>, 
            title: string,
            valueTitle: string) {
        super(props, title)
        this.valueTitle = valueTitle
    }

    abstract valueFormatter(value: number): JSX.Element

    renderOverviewSection(data: Array<TeamStatOverview>): JSX.Element {
        let map = new Map(this.props.modelTableProps.levelData.countries)
        return <Translation>
        {(t, { i18n}) => <table className="statistics_table">
            <thead>
            <tr>
                <th>{t('table.team')}</th>
                <th className="value">{t('overview.country')}</th>
                <th className="value">{t('table.league')}</th>
                <th className="value">{this.valueTitle}</th>
            </tr>
            </thead>
            <tbody>
                {data.map(teamStat => {
                    return <tr>
                        <td className="to_left">
                            <TeamLink name={teamStat.teamSortingKey.teamName} id={teamStat.teamSortingKey.teamId}/>
                        </td>
                        <td className="value">
                            <LeagueLink tableLink={true} id={teamStat.leagueId} name={map.get(teamStat.leagueId) || ''} />
                        </td>
                        <td className="value">
                            <LeagueUnitLink id={teamStat.teamSortingKey.leagueUnitId} name={teamStat.teamSortingKey.leagueUnitName} />
                        </td>
                        <td className="value">
                            {this.valueFormatter(teamStat.value)}
                        </td>
                    </tr>
                })}
            </tbody>
        </table>
        }
    </Translation>
    }
}

export default TeamOverviewSection