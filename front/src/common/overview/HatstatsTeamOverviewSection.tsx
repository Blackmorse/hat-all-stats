import React from 'react'
import TeamOverviewSection from './TeamOverviewSection'
import { OverviewTableSectionProps } from './OverviewTableSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import { getTopHatstatsTeamsOverview } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'
import LevelData from '../../rest/models/leveldata/LevelData'
import Section from '../sections/Section'

class HatstatsTeamOverviewSectionBase<Data extends LevelData> extends TeamOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, TeamStatOverview>) {
        super(props, i18n.t('table.hatstats'))
    }

    valueFormatter(value: number): JSX.Element {
        return <>{value}</>
    }

    loadOverviewEntity = getTopHatstatsTeamsOverview
}

const HatstatsTeamOverviewSection = Section(HatstatsTeamOverviewSectionBase, _ => 'overview.top_teams')
export default HatstatsTeamOverviewSection