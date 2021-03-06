import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopSalaryPlayersOverview } from '../../rest/Client'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewTableSectionProps } from './OverviewTableSection';
import '../../i18n'
import i18n from '../../i18n';
import { salaryFormatter } from '../../common/Formatters'
import LevelData from '../../rest/models/leveldata/LevelData';

class SalaryPlayerOverviewSection<Data extends LevelData> extends PlayerOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, PlayerStatOverview>) {
        super(props, 'overview.top_salary_players',
        i18n.t('table.salary') + ',' + props.levelDataProps.levelData.currency)
    }

    loadOverviewEntity = getTopSalaryPlayersOverview

    valueFormatter(value: number): JSX.Element {
        return salaryFormatter(value, this.props.levelDataProps.levelData.currencyRate)
    }
}

export default SalaryPlayerOverviewSection