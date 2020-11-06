import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopSalaryPlayersOverview } from '../../rest/Client'
import WorldData from '../../rest/models/leveldata/WorldData';
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewSectionProps } from './OverviewSection';
import '../../i18n'
import i18n from '../../i18n';
import { commasSeparated } from '../../common/Formatters'

class SalaryPlayerOverviewSection extends PlayerOverviewSection {
    constructor(props: OverviewSectionProps<WorldData, Array<PlayerStatOverview>>) {
        super(props, 'overview.top_salary_players',
        i18n.t('table.salary') + ',' + props.modelTableProps.levelData.currency)
    }

    loadOverviewEntity = getTopSalaryPlayersOverview

    valueFormatter(value: number): JSX.Element {
        return commasSeparated(value)
    }
}

export default SalaryPlayerOverviewSection