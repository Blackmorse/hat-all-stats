import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopRatingPlayersOverview } from '../../rest/Client'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewTableSectionProps } from './OverviewTableSection';
import '../../i18n'
import i18n from '../../i18n';
import { ratingFormatter } from '../../common/Formatters'
import LevelData from '../../rest/models/leveldata/LevelData';
import Section from '../sections/Section';

class RatingPlayerOverviewSectionBase<Data extends LevelData> extends PlayerOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, PlayerStatOverview>) {
        super(props, 
        i18n.t('table.rating'))
    }

    loadOverviewEntity = getTopRatingPlayersOverview

    valueFormatter(value: number): JSX.Element {
        return ratingFormatter(value)
    }
}

const RatingPlayerOverviewSection = Section(RatingPlayerOverviewSectionBase, _ => 'overview.top_rating_players')
export default RatingPlayerOverviewSection