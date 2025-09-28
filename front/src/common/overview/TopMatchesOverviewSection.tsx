import { type JSX } from 'react'
import { getTopMatchesOverview } from '../../rest/Client'
import MatchesOverviewSection from './MatchesOverviewSection'
import Section from '../sections/Section';

class TopMatchesOverviewSectionBase extends MatchesOverviewSection {

    valueFormatter(_value: number): JSX.Element {
        throw new Error("Method not implemented.");
    }

    loadOverviewEntity = getTopMatchesOverview
}

const TopMatchesOverviewSection = Section(TopMatchesOverviewSectionBase, _ => 'overview.top_matches')
export default TopMatchesOverviewSection
