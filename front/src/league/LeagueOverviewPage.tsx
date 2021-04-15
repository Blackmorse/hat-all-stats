import OverviewPage, { LeagueId } from '../common/overview/OverviewPage'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import { PagesEnum } from '../common/enums/PagesEnum';
import LeagueLink from '../common/links/LeagueLink';
import HattidLink from '../common/links/HattidLink';
import LeagueData from '../rest/models/leveldata/LeagueData';


class LeagueOverviewPage extends OverviewPage<LeagueData, LeagueLevelDataProps> {
    linkProviderFunc<Entity extends LeagueId>(page: PagesEnum, sortingField: string): 
            (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any> {       
        return (text: string | JSX.Element, season: number, round: number, entity: Entity) => {
            return new LeagueLink({
                id: this.props.levelDataProps.leagueId(),
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

export default LeagueOverviewPage