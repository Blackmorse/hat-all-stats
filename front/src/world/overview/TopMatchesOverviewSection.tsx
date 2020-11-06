import MatchTopHatstatsOverview from '../../rest/models/overview/MatchTopHatstatsOverview'
import WorldData from '../../rest/models/leveldata/WorldData';
import { getTopMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import { OverviewSectionProps } from './OverviewSection';

class TopMatchesOverviewSection extends MatchesOverviewSection {
    constructor(props: OverviewSectionProps<WorldData, Array<MatchTopHatstatsOverview>>) {
        super(props, 'overview.top_matches')
    }

    loadOverviewEntity = getTopMatchesOverview
}

export default TopMatchesOverviewSection