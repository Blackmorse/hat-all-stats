import React from 'react'
import LevelData from '../../rest/models/leveldata/LevelData';
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import LeagueLink from '../../common/links/LeagueLink';
import WorldData from '../../rest/models/leveldata/WorldData';
import HattidLink from '../links/HattidLink';

export interface OverviewTableSectionProps<Data extends LevelData, Entity> 
    extends OverviewSectionProps<Data, Array<Entity>> {
    linkProvider: (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any>
}

abstract class OverviewTableSection<Data extends LevelData, Entity> 
    extends OverviewSection<Data, Array<Entity>, OverviewTableSectionProps<Data, Entity>> {

    abstract valueFormatter(value: number): JSX.Element

    abstract tableheader(): JSX.Element

    abstract tableRow(entity: Entity, leagueNameFunc: (id: number) => JSX.Element): JSX.Element

    renderOverviewSection(entities: Array<Entity>) : JSX.Element {
        let leagueNameFunc: (id: number) => JSX.Element

        if (this.isWorldData) {
            let nameMap = new Map(((this.props.levelDataProps.levelData as any) as WorldData).countries)
            leagueNameFunc = (id) => <td className="value">
                    <LeagueLink tableLink={true} id={id} text={nameMap.get(id) || ''}/>
                </td>
        } else {
            leagueNameFunc = (id) => <></>
        }
        
        return <table className="statistics_table">
            <thead>
                {this.tableheader()}
            </thead>
            <tbody>
                {entities.map(entity => this.tableRow(entity, leagueNameFunc))}
            </tbody>
        </table>
    }
}

export default OverviewTableSection