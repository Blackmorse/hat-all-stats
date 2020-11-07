import MatchTopHatstatsOverview from '../../rest/models/overview/MatchTopHatstatsOverview'
import { getSurprisingMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import { OverviewSectionProps } from './OverviewSection';
import LevelData from '../../rest/models/leveldata/LevelData';

class SurprisingMatchesOverviewSection<Data extends LevelData> extends MatchesOverviewSection<Data> {
    constructor(props: OverviewSectionProps<Data, Array<MatchTopHatstatsOverview>>) {
        super(props, 'overview.surprising_matches')
    }

    loadOverviewEntity = getSurprisingMatchesOverview
}

export default SurprisingMatchesOverviewSection