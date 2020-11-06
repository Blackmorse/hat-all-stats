import React from 'react'
import TeamOverviewSection from './TeamOverviewSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import WorldData from '../../rest/models/leveldata/WorldData'
import { OverviewSectionProps } from './OverviewSection'
import { getTopHatstatsTeamsOverview } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'

class HatstatsTeamOverviewSection extends TeamOverviewSection {
    constructor(props: OverviewSectionProps<WorldData, Array<TeamStatOverview>>) {
        super(props, 'overview.top_teams', i18n.t('table.hatstats'))
    }

    valueFormatter(value: number): JSX.Element {
        return <>{value}</>
    }

    loadOverviewEntity = getTopHatstatsTeamsOverview
}

export default HatstatsTeamOverviewSection