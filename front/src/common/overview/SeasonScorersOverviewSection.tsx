import React from 'react'
import PlayerOverviewSection from './PlayerOverviewSection'
import { getTopSeasonScorers } from '../../rest/Client'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview';
import { OverviewTableSectionProps } from './OverviewTableSection';
import '../../i18n'
import i18n from '../../i18n';
import LevelData from '../../rest/models/leveldata/LevelData';
import Section from '../sections/Section';

class SeasonScorersOverviewSectionBase<Data extends LevelData> extends PlayerOverviewSection<Data> {
    constructor(props: OverviewTableSectionProps<Data, PlayerStatOverview>) {
        super(props, i18n.t('overview.goals'))
    }

    loadOverviewEntity = getTopSeasonScorers

    valueFormatter(value: number): JSX.Element {
        return <>{value}</>
    }
}

const SeasonScorersOverviewSection = Section(SeasonScorersOverviewSectionBase, _ => 'overview.scorers')
export default SeasonScorersOverviewSection