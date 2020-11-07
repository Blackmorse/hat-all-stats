import MatchTopHatstatsOverview from '../../rest/models/overview/MatchTopHatstatsOverview'
import { getTopMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import { OverviewSectionProps } from './OverviewSection';
import LevelData from '../../rest/models/leveldata/LevelData';

class TopMatchesOverviewSection<Data extends LevelData> extends MatchesOverviewSection<Data> {
    constructor(props: OverviewSectionProps<Data, Array<MatchTopHatstatsOverview>>) {
        super(props, 'overview.top_matches')
    }

    loadOverviewEntity = getTopMatchesOverview
}

export default TopMatchesOverviewSection