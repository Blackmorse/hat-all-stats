import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopRatingPlayersOverview } from '../../rest/Client'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewTableSectionProps } from './OverviewTableSection';
import '../../i18n'
import i18n from '../../i18n';
import { ratingFormatter } from '../../common/Formatters'
import LevelData from '../../rest/models/leveldata/LevelData';

class RatingPlayerOverviewSection<Data extends LevelData> extends PlayerOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, PlayerStatOverview>) {
        super(props, 'overview.top_rating_players',
        i18n.t('table.rating'))
    }

    loadOverviewEntity = getTopRatingPlayersOverview

    valueFormatter(value: number): JSX.Element {
        return ratingFormatter(value)
    }
}

export default RatingPlayerOverviewSection