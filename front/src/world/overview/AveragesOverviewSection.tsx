import React from 'react';
import '../../i18n'
import { Translation } from 'react-i18next'
import { commasSeparated, ageFormatter, ratingFormatter } from '../../common/Formatters'
import WorldData from '../../rest/models/leveldata/WorldData';
import { getAveragesOverview } from '../../rest/Client'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import AveragesOverview from '../../rest/models/overview/AveragesOverview'

class AveragesOverviewSection extends OverviewSection<WorldData, AveragesOverview> {
    constructor(props: OverviewSectionProps<WorldData, AveragesOverview>) {
        super(props, 'overview.averages')
    }

    loadOverviewEntity = getAveragesOverview

    renderOverviewSection(averageOverview: AveragesOverview): JSX.Element {
        return <Translation>
        {(t, { i18n}) =>
            <table className="statistics_table">
                <tbody>
                    <tr>
                        <td className="value">{t('overview.average_hatstats')}</td>
                        <td>{averageOverview.matchAverages.hatstats}</td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_spectators')}</td>
                        <td>{commasSeparated(averageOverview.matchAverages.spectators)}</td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_team_goals')}</td>
                        <td>{Math.round(averageOverview.matchAverages.goals * 100) / 100}</td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_team_age')}</td>
                        <td>{ageFormatter(averageOverview.teamPlayerAverages.age)}</td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_team_salary')}</td>
                        <td>{commasSeparated(averageOverview.teamPlayerAverages.salary / this.props.modelTableProps.currencyRate())} {this.props.modelTableProps.currency()}</td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_team_stars')}</td>
                        <td className="value">{ratingFormatter(averageOverview.teamPlayerAverages.rating)}</td>
                    </tr>
                </tbody>
            </table>
        }
        </Translation>
    }
}

export default AveragesOverviewSection