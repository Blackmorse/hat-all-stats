import { type JSX } from 'react';
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import FormationsOverview from '../../rest/models/overview/FormationsOverview'
import { getFormationsOverview } from '../../rest/Client'
import '../../i18n'
import { commasSeparated } from '../../common/Formatters'
import Section from '../sections/Section';
import ChartLink from '../charts/ChartLink';
import FormationsChart from './charts/FormationsChart';

class FormationsOverviewSectionBase extends OverviewSection<Array<FormationsOverview>, OverviewSectionProps<Array<FormationsOverview>>> {
   
    loadOverviewEntity = getFormationsOverview

    renderOverviewSection(data: Array<FormationsOverview>): JSX.Element {
        const totalFormations = data.map(fo => fo.count)
            .reduce((sum, current) => sum + current, 0);

        const top6 = data.slice(0, 6)
        return <table className="table table-striped table-rounded table-sm small text-center">
            <tbody>
                {top6.map(formation => {
                return <tr key={'overview_formation_' + formation.formation}>
                    <td>{formation.formation}</td>
                    <td>{commasSeparated(formation.count)} ({Math.floor((formation.count / totalFormations) * 100)}%)</td>
                </tr>})}
            </tbody>
    </table>
    }

}

const FormationsOverviewSection = Section(FormationsOverviewSectionBase, (props: OverviewSectionProps<Array<FormationsOverview>>, _state) => {
    return {
        header: 'overview.formations',
        additionalElement: <ChartLink chartContent={() => <FormationsChart levelRequest={props.levelDataProps.createLevelRequest()} />} />
    }  } )
export default FormationsOverviewSection
