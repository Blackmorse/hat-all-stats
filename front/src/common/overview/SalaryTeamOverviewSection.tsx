import TeamOverviewSection from './TeamOverviewSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import { OverviewTableSectionProps } from './OverviewTableSection'
import { getTopSalaryTeamsOverview } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'
import { commasSeparated } from '../../common/Formatters'
import LevelData from '../../rest/models/leveldata/LevelData'
import Section from '../sections/Section'

class SalaryTeamOverviewSectionBase<Data extends LevelData> extends TeamOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, TeamStatOverview>) {
        super(props, i18n.t('table.salary') + ',' + props.levelDataProps.currency())
    }

    loadOverviewEntity = getTopSalaryTeamsOverview
    
    valueFormatter(value: number): JSX.Element {
        return commasSeparated(Math.floor(value / this.props.levelDataProps.currencyRate()))
    }
}

const SalaryTeamOverviewSection = Section(SalaryTeamOverviewSectionBase, _ => 'overview.top_salary_teams')
export default SalaryTeamOverviewSection