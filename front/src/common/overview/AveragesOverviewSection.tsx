import React from 'react';
import '../../i18n'
import { Translation } from 'react-i18next'
import { commasSeparated, ageFormatter, ratingFormatter } from '../../common/Formatters'
import { getAveragesOverview } from '../../rest/Client'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import AveragesOverview from '../../rest/models/overview/AveragesOverview'
import Section from '../sections/Section';
import ChartLink from '../charts/ChartLink';
import NumbersChart from './charts/NumbersChart';
import { averageHatstatNumbersChart, averageSpectatorNumbersChart, averageGoalNumbersChart } from '../../rest/Client'

class AveragesOverviewSectionBase extends OverviewSection<AveragesOverview, OverviewSectionProps<AveragesOverview>> {

    loadOverviewEntity = getAveragesOverview

    renderOverviewSection(averageOverview: AveragesOverview): JSX.Element {
        return <Translation>
        {t =>
            <table className="table table-striped table-rounded table-sm small text-center">
                <tbody>
                    <tr>
                        <td>{t('overview.average_hatstats')}</td>
                        <td>
                            <ChartLink chartContent={() => 
                                <NumbersChart title={t('overview.average_hatstats')} 
                                    requestFunc={averageHatstatNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                       
                            {averageOverview.matchAverages.hatstats}
                        </td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_spectators')}</td>
                        <td>
                            <ChartLink chartContent={() => 
                                <NumbersChart title={t('overview.average_spectators')} 
                                    requestFunc={averageSpectatorNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                       
                            {commasSeparated(averageOverview.matchAverages.spectators)}
                        </td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_team_goals')}</td>
                        <td>
                            <ChartLink chartContent={() => 
                                <NumbersChart title={t('overview.average_team_goals')} 
                                    requestFunc={averageGoalNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()}
                                    numberFormatter={n => n / 100} />} />
                       
                            {Math.round(averageOverview.matchAverages.goals * 100) / 100}
                        </td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_team_age')}</td>
                        <td className="value">{ageFormatter(averageOverview.teamPlayerAverages.age)}</td>
                    </tr>
                    <tr>
                        <td>{t('overview.average_team_salary')}</td>
                        <td className="value">{commasSeparated(averageOverview.teamPlayerAverages.salary / this.props.levelDataProps.currencyRate())} {this.props.levelDataProps.currency()}</td>
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

const AveragesOverviewSection = Section(AveragesOverviewSectionBase, (_props: OverviewSectionProps<AveragesOverview>, _state) => 'overview.averages' )

export default AveragesOverviewSection
