import { type JSX } from 'react'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import LeagueLink from '../../common/links/LeagueLink';
import HattidLink from '../links/HattidLink';
import CountryImage from '../elements/CountryImage'

export interface OverviewTableSectionProps<Entity> 
    extends OverviewSectionProps<Array<Entity>> {
    linkProvider: (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any>
}

abstract class OverviewTableSection<Entity> 
    extends OverviewSection<Array<Entity>, OverviewTableSectionProps<Entity>> {

    abstract valueFormatter(value: number): JSX.Element

    abstract tableheader(): JSX.Element

    abstract tableRow(entity: Entity, leagueNameFunc: (id: number) => (JSX.Element | undefined)): JSX.Element

    renderOverviewSection(entities: Array<Entity>) : JSX.Element {
        let leagueNameFunc: (id: number) => JSX.Element | undefined

        if (this.isWorldData) {
            const nameMap = this.props.levelDataProps.countriesMap()
            leagueNameFunc = (id) => <td key={'match_row_' + id + '_' + Math.random()} className="value">
                    <LeagueLink id={id} text={<CountryImage countryId={id} text={nameMap.get(id)} />}/>
                    
                </td>
        } else {
            leagueNameFunc = (_id) => undefined
        }
        
        return <div className='table-responsive'>
            <table className="table table-striped table-rounded table-sm small text-center">
            <thead>
                {this.tableheader()}
            </thead>
            <tbody>
                {entities.map(entity => this.tableRow(entity, leagueNameFunc))}
            </tbody>
        </table>
    </div>
    }
}

export default OverviewTableSection
