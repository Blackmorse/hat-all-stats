import OverviewPage, { LeagueId } from '../common/overview/OverviewPage'
import WorldData from '../rest/models/leveldata/WorldData';
import WorldLevelDataProps from './WorldLevelDataProps'
import { PagesEnum } from '../common/enums/PagesEnum';
import LeagueLink from '../common/links/LeagueLink';
import HattidLink from '../common/links/HattidLink';

class WorldOverviewPage extends OverviewPage<WorldData, WorldLevelDataProps> {
    linkProviderFunc<Entity extends LeagueId>(page: PagesEnum, sortingField: string): 
            (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any> {       
        return (text: string | JSX.Element, season: number, round: number, entity: Entity) => {
            return new LeagueLink({
                id: entity.leagueId,
                text: text,
                page: page,
                queryParams: {
                    sortingField: sortingField,
                    season: season,
                    round: round
                }
            })
        }        
    }
}

export default WorldOverviewPage