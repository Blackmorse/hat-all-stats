import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopRatingPlayersOverview } from '../../rest/Client'
import WorldData from '../../rest/models/leveldata/WorldData';
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewSectionProps } from './OverviewSection';
import '../../i18n'
import i18n from '../../i18n';
import { ratingFormatter } from '../../common/Formatters'

class RatingPlayerOverviewSection extends PlayerOverviewSection {
    constructor(props: OverviewSectionProps<WorldData, Array<PlayerStatOverview>>) {
        super(props, 'overview.top_rating_players',
        i18n.t('table.rating'))
    }

    loadOverviewEntity = getTopRatingPlayersOverview

    valueFormatter(value: number): JSX.Element {
        return ratingFormatter(value)
    }
}

export default RatingPlayerOverviewSection