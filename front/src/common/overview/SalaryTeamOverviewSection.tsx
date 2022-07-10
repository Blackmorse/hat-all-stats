import TeamOverviewSection from './TeamOverviewSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import { OverviewTableSectionProps } from './OverviewTableSection'
import { getTopSalaryTeamsOverview } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'
import { commasSeparated } from '../../common/Formatters'
import Section from '../sections/Section'

class SalaryTeamOverviewSectionBase extends TeamOverviewSection {
    constructor(props: OverviewTableSectionProps<TeamStatOverview>) {
        super(props, i18n.t('table.salary') + ',' + props.levelDataProps.currency())
    }

    loadOverviewEntity = getTopSalaryTeamsOverview
    
    valueFormatter(value: number): JSX.Element {
        return commasSeparated(Math.floor(value / this.props.levelDataProps.currencyRate()))
    }
}

const SalaryTeamOverviewSection = Section(SalaryTeamOverviewSectionBase, _ => 'overview.top_salary_teams')
export default SalaryTeamOverviewSection
