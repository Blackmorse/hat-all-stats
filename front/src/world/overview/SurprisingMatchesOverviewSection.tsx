import MatchTopHatstatsOverview from '../../rest/models/overview/MatchTopHatstatsOverview'
import WorldData from '../../rest/models/leveldata/WorldData';
import { getSurprisingMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import { OverviewSectionProps } from './OverviewSection';

class SurprisingMatchesOverviewSection extends MatchesOverviewSection {
    constructor(props: OverviewSectionProps<WorldData, Array<MatchTopHatstatsOverview>>) {
        super(props, 'overview.surprising_matches')
    }

    loadOverviewEntity = getSurprisingMatchesOverview
}

export default SurprisingMatchesOverviewSection