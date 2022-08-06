import OverviewPage, { LeagueId } from '../common/overview/OverviewPage'
import DivisionLevelDataProps from './DivisionLevelDataProps'
import { PagesEnum } from '../common/enums/PagesEnum';
import DivisionLevelLink from '../common/links/DivisionLevelLink';
import HattidLink from '../common/links/HattidLink';

class DivisionLevelOverviewPage extends OverviewPage<DivisionLevelDataProps> {
    linkProviderFunc<Entity extends LeagueId>(page: PagesEnum, sortingField: string): 
            (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any> {       
        return (text: string | JSX.Element, season:number, round: number, _entity: Entity) => {
            return new DivisionLevelLink({
                leagueId: this.props.levelDataProps.leagueId(),
                divisionLevel: this.props.levelDataProps.divisionLevel(),
                text: text,
                page: page,
                queryParams: {
                    sortingField: sortingField,
                    season: season,
                    round: round
                },
                forceRefresh: true
            })
        }        
    }
}

export default DivisionLevelOverviewPage
