import { getTopMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import LevelData from '../../rest/models/leveldata/LevelData';
import Section from '../sections/Section';

class TopMatchesOverviewSectionBase<Data extends LevelData> extends MatchesOverviewSection<Data> {

    valueFormatter(value: number): JSX.Element {
        throw new Error("Method not implemented.");
    }

    loadOverviewEntity = getTopMatchesOverview
}

const TopMatchesOverviewSection = Section(TopMatchesOverviewSectionBase, _ => 'overview.top_matches')
export default TopMatchesOverviewSection