import React from 'react';
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import WorldData from '../../rest/models/leveldata/WorldData';
import FormationsOverview from '../../rest/models/overview/FormationsOverview'
import { getFormationsOverview } from '../../rest/Client'
import '../../i18n'
import { Translation } from 'react-i18next'
import { commasSeparated } from '../../common/Formatters'

class FormationsOverviewSection extends OverviewSection<WorldData, Array<FormationsOverview>> {
    constructor(props: OverviewSectionProps<WorldData, Array<FormationsOverview>>) {
        super(props, 'overview.formations')
    }
    
    loadOverviewEntity = getFormationsOverview

    renderOverviewSection(data: Array<FormationsOverview>): JSX.Element {
        let totalFormations = data.map(fo => fo.count)
            .reduce((sum, current) => sum + current, 0);

        let top6 = data.slice(0, 6)
        return <Translation>
        {(t, { i18n}) => <table className="statistics_table">
            <tbody>
                {top6.map(formation => {
                return <tr>
                    <td>{formation.formation}</td>
                    <td>{commasSeparated(formation.count)} ({Math.floor((formation.count / totalFormations) * 100)}%)</td>
                </tr>})}
            </tbody>
    </table>
    }
    </Translation>
    }

}

export default FormationsOverviewSection