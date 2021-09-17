import React from 'react';
import '../../i18n'
import { Translation } from 'react-i18next'
import { commasSeparated, ageFormatter, ratingFormatter } from '../../common/Formatters'
import { getAveragesOverview } from '../../rest/Client'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import AveragesOverview from '../../rest/models/overview/AveragesOverview'
import LevelData from '../../rest/models/leveldata/LevelData';
import Section from '../sections/Section';

class AveragesOverviewSectionBase<Data extends LevelData> extends OverviewSection<Data, AveragesOverview, OverviewSectionProps<Data, AveragesOverview>> {

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
                        <td>{commasSeparated(averageOverview.teamPlayerAverages.salary / this.props.levelDataProps.currencyRate())} {this.props.levelDataProps.currency()}</td>
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

const AveragesOverviewSection = Section(AveragesOverviewSectionBase, _ => 'overview.averages')

export default AveragesOverviewSection