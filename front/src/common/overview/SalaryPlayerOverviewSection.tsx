import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopSalaryPlayersOverview } from '../../rest/Client'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewSectionProps } from './OverviewSection';
import '../../i18n'
import i18n from '../../i18n';
import { commasSeparated } from '../../common/Formatters'
import LevelData from '../../rest/models/leveldata/LevelData';

class SalaryPlayerOverviewSection<Data extends LevelData> extends PlayerOverviewSection<Data> {
    constructor(props: OverviewSectionProps<Data, Array<PlayerStatOverview>>) {
        super(props, 'overview.top_salary_players',
        i18n.t('table.salary') + ',' + props.levelDataProps.levelData.currency)
    }

    loadOverviewEntity = getTopSalaryPlayersOverview

    valueFormatter(value: number): JSX.Element {
        return commasSeparated(value)
    }
}

export default SalaryPlayerOverviewSection