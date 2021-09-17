import React from 'react'
import TeamOverviewSection from './TeamOverviewSection'
import TeamStatOverview from '../../rest/models/overview/TeamStatOverview'
import { OverviewTableSectionProps } from './OverviewTableSection'
import { getTopTeamVictories } from '../../rest/Client'
import '../../i18n'
import i18n from '../../i18n'
import LevelData from '../../rest/models/leveldata/LevelData'
import Section from '../sections/Section'

class VictoriesTeamOverviewSectionBase<Data extends LevelData> extends TeamOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, TeamStatOverview>) {
        super(props, i18n.t('table.victories'))
    }

    loadOverviewEntity = getTopTeamVictories
    
    valueFormatter(value: number): JSX.Element {
        return <>{value}</>
    }
}

const VictoriesTeamOverviewSection = Section(VictoriesTeamOverviewSectionBase, _ => 'overview.winning_streak')
export default VictoriesTeamOverviewSection