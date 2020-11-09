import TeamOverviewSection from './TeamOverviewSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import { OverviewTableSectionProps } from './OverviewTableSection'
import { getTopSalaryTeamsOverview } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'
import { commasSeparated } from '../../common/Formatters'
import LevelData from '../../rest/models/leveldata/LevelData'

class SalaryTeamOverviewSection<Data extends LevelData> extends TeamOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, TeamStatOverview>) {
        super(props, 'overview.top_salary_teams', 
            i18n.t('table.salary') + ',' + props.levelDataProps.currency())
    }

    loadOverviewEntity = getTopSalaryTeamsOverview
    
    valueFormatter(value: number): JSX.Element {
        return commasSeparated(value)
    }
}

export default SalaryTeamOverviewSection