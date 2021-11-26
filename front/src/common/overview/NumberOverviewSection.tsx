import React from 'react';
import NumberOverview from '../../rest/models/overview/NumberOverview'
import '../../i18n'
import { Translation } from 'react-i18next'
import { commasSeparated } from '../../common/Formatters'
import { teamNumbersChart, playerNumbersChart, getNumberOverview, goalNumbersChart, injuryNumbersChart, redCardNumbersChart, yellowCardNumbersChart, newTeamNumbersChart } from '../../rest/Client'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import LevelData from '../../rest/models/leveldata/LevelData';
import Section from '../sections/Section';
import '../charts/Charts.css'
import ChartLink from '../charts/ChartLink';
import NumbersChart from './charts/NumbersChart'


class NumberOverviewSectionBase<Data extends LevelData> extends OverviewSection<Data, NumberOverview, OverviewSectionProps<Data, NumberOverview>> {

    loadOverviewEntity = getNumberOverview

    renderOverviewSection(data: NumberOverview): JSX.Element {   
        return <Translation>
            {(t, { i18n}) => <table className="statistics_table">
                <tbody>
                <tr>
                    <td>{t('overview.number_of_teams')}</td>
                    <td>
                        <ChartLink chartContent={() => 
                            <NumbersChart title={t('overview.number_of_teams')} 
                                    requestFunc={teamNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                        {commasSeparated(data.numberOfTeams)}
                    </td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_new_teams')}</td>
                    <td>
                        <ChartLink chartContent={() => 
                            <NumbersChart title={t('overview.number_of_new_teams')} 
                                    requestFunc={newTeamNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                        {commasSeparated(data.numberOfNewTeams)}
                    </td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_players')}</td>
                    <td>
                        <ChartLink chartContent={() => 
                            <NumbersChart title={t('overview.number_of_players')} 
                                    requestFunc={playerNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                        {commasSeparated(data.numberOfPlayers)}
                    </td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_goals')}</td>
                    <td>
                        <ChartLink chartContent={() => 
                            <NumbersChart title={t('overview.number_of_goals')} 
                                    requestFunc={goalNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                        {commasSeparated(data.goals)}
                    </td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_injuried')}</td>
                    <td>
                        <ChartLink chartContent={() => 
                            <NumbersChart title={t('overview.number_of_injuried')} 
                                    requestFunc={injuryNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                        {commasSeparated(data.injuried)}
                    </td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_yellow')}</td>
                    <td>
                        <ChartLink chartContent={() => 
                            <NumbersChart title={t('overview.number_of_yellow')} 
                                    requestFunc={yellowCardNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                        {commasSeparated(data.yellowCards)}
                    </td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_red')}</td>
                    <td>
                        <ChartLink chartContent={() => 
                            <NumbersChart title={t('overview.number_of_red')} 
                                    requestFunc={redCardNumbersChart} 
                                    levelRequest={this.props.levelDataProps.createLevelRequest()} />} />
                        {commasSeparated(data.redCards)}
                    </td>
                </tr>
                </tbody>
            </table>
        }
        </Translation>
    }
}

const NumberOverviewSection = Section(NumberOverviewSectionBase, _ => 'overview.numbers')
export default NumberOverviewSection