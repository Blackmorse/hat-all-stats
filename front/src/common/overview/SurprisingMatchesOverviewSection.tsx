import { getSurprisingMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import Section from '../sections/Section';

class SurprisingMatchesOverviewSectionBase extends MatchesOverviewSection {

    valueFormatter(_value: number): JSX.Element {
        throw new Error("Method not implemented.");
    }

    loadOverviewEntity = getSurprisingMatchesOverview
}

const SurprisingMatchesOverviewSection = Section(SurprisingMatchesOverviewSectionBase, _ => 'overview.surprising_matches')
export default SurprisingMatchesOverviewSection
