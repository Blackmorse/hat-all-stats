import MatchTopHatstats from '../../rest/models/match/MatchTopHatstats'
import { getTopMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import { OverviewTableSectionProps } from './OverviewTableSection';
import LevelData from '../../rest/models/leveldata/LevelData';

class TopMatchesOverviewSection<Data extends LevelData> extends MatchesOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, MatchTopHatstats>) {
        super(props, 'overview.top_matches')
    }

    valueFormatter(value: number): JSX.Element {
        throw new Error("Method not implemented.");
    }

    loadOverviewEntity = getTopMatchesOverview
}

export default TopMatchesOverviewSection