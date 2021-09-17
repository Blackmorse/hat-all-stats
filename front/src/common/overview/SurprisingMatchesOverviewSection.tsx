import { getSurprisingMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import LevelData from '../../rest/models/leveldata/LevelData';
import Section from '../sections/Section';

class SurprisingMatchesOverviewSectionBase<Data extends LevelData> extends MatchesOverviewSection<Data> {

    valueFormatter(value: number): JSX.Element {
        throw new Error("Method not implemented.");
    }

    loadOverviewEntity = getSurprisingMatchesOverview
}

const SurprisingMatchesOverviewSection = Section(SurprisingMatchesOverviewSectionBase, _ => 'overview.surprising_matches')
export default SurprisingMatchesOverviewSection