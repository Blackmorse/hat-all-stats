import React from 'react'
import TeamOverviewSection from './TeamOverviewSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import { OverviewTableSectionProps } from './OverviewTableSection'
import { getTopTeamVictories } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'
import LevelData from '../../rest/models/leveldata/LevelData'

class VictoriesTeamOverviewSection<Data extends LevelData> extends TeamOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, TeamStatOverview>) {
        super(props, 'overview.winning_streak', 
            i18n.t('table.victories'))
    }

    loadOverviewEntity = getTopTeamVictories
    
    valueFormatter(value: number): JSX.Element {
        return <>{value}</>
    }
}

export default VictoriesTeamOverviewSection