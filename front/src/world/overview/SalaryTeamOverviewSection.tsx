import TeamOverviewSection from './TeamOverviewSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import WorldData from '../../rest/models/leveldata/WorldData'
import { OverviewSectionProps } from './OverviewSection'
import { getTopSalaryTeamsOverview } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'
import { commasSeparated } from '../../common/Formatters'

class SalaryTeamOverviewSection extends TeamOverviewSection {
    constructor(props: OverviewSectionProps<WorldData, Array<TeamStatOverview>>) {
        super(props, 'overview.top_salary_teams', 
            i18n.t('table.salary') + ',' + props.modelTableProps.currency())
    }

    loadOverviewEntity = getTopSalaryTeamsOverview
    
    valueFormatter(value: number): JSX.Element {
        return commasSeparated(value)
    }
}

export default SalaryTeamOverviewSection