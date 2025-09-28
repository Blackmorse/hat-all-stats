import { type JSX } from 'react';
import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopRatingPlayersOverview } from '../../rest/Client'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewTableSectionProps } from './OverviewTableSection';
import '../../i18n'
import i18n from '../../i18n';
import { ratingFormatter } from '../../common/Formatters'
import Section from '../sections/Section';

class RatingPlayerOverviewSectionBase extends PlayerOverviewSection {
    constructor(props: OverviewTableSectionProps<PlayerStatOverview>) {
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
