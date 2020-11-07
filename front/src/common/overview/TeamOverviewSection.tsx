import React from 'react'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import '../../i18n'
import { Translation } from 'react-i18next'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData';

abstract class TeamOverviewSection<Data extends LevelData> extends OverviewSection<Data, Array<TeamStatOverview>> {
    valueTitle: string
    
    constructor(props: OverviewSectionProps<Data, Array<TeamStatOverview>>, 
            title: string,
            valueTitle: string) {
        super(props, title)
        this.valueTitle = valueTitle
    }

    abstract valueFormatter(value: number): JSX.Element

    renderOverviewSection(data: Array<TeamStatOverview>, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <Translation>
        {(t, { i18n}) => <table className="statistics_table">
            <thead>
            <tr>
                <th>{t('table.team')}</th>
                {(this.isWorldData) ? <th className="value">{t('overview.country')}</th> : <></>}
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
                        {leagueNameFunc(teamStat.leagueId)}
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