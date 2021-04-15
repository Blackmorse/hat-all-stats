import OverviewPage, { LeagueId } from '../common/overview/OverviewPage'
import DivisionLevelDataProps from './DivisionLevelDataProps'
import { PagesEnum } from '../common/enums/PagesEnum';
import DivisionLevelLink from '../common/links/DivisionLevelLink';
import HattidLink from '../common/links/HattidLink';
import DivisionLevelData from '../rest/models/leveldata/DivisionLevelData';


class DivisionLevelOverviewPage extends OverviewPage<DivisionLevelData, DivisionLevelDataProps> {
    linkProviderFunc<Entity extends LeagueId>(page: PagesEnum, sortingField: string): 
            (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any> {       
        return (text: string | JSX.Element, season:number, round: number, entity: Entity) => {
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