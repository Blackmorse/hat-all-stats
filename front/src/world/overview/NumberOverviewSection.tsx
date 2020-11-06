import React from 'react';
import NumberOverview from '../../rest/models/overview/NumberOverview'
import '../../i18n'
import { Translation } from 'react-i18next'
import { commasSeparated } from '../../common/Formatters'
import WorldData from '../../rest/models/leveldata/WorldData';
import { getNumberOverview } from '../../rest/Client'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'


class NumberOverviewSection extends OverviewSection<WorldData, NumberOverview> {
    constructor(props: OverviewSectionProps<WorldData, NumberOverview>) {
        super(props, 'overview.numbers')
    }

    loadOverviewEntity = getNumberOverview

    renderOverviewSection(data: NumberOverview): JSX.Element {   
        return <Translation>
            {(t, { i18n}) => <table className="statistics_table">
                <tbody>
                <tr>
                    <td>{t('overview.number_of_teams')}</td>
                    <td>{commasSeparated(data.numberOfTeams)}</td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_players')}</td>
                    <td>{commasSeparated(data.numberOfPlayers)}</td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_goals')}</td>
                    <td>{commasSeparated(data.goals)}</td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_injuried')}</td>
                    <td>{commasSeparated(data.injuried)}</td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_yellow')}</td>
                    <td>{commasSeparated(data.yellowCards)}</td>
                </tr>
                <tr>
                    <td>{t('overview.number_of_red')}</td>
                    <td>{commasSeparated(data.redCards)}</td>
                </tr>
                </tbody>
            </table>
        }
        </Translation>
    }
}

export default NumberOverviewSection