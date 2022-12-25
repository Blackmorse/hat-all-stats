import React from 'react'
import WorldOverviewPage from './WorldOverviewPage'
import WorldLevelDataProps from './WorldLevelDataProps'
import QueryParams from '../common/QueryParams'
import { PagesEnum } from '../common/enums/PagesEnum'
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage'
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable'


export default function pages() {
    const pagesMap = new Map<PagesEnum, (props: WorldLevelDataProps, queryParams: QueryParams) => JSX.Element>()
    pagesMap.set(PagesEnum.OVERVIEW, 
        (props, _queryParams) => <WorldOverviewPage levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_HATSTATS,
        (props, queryParams) => <TeamHatstatsTable<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
        (props, queryParams) => <TeamSalaryTSITable<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS,
        (props, queryParams) => <OldestTeamsTable<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, 
        (props, queryParams) => <PlayerSalaryTsiTable<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, 
        (props, queryParams) => <PlayerRatingsTable<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, 
        (props, queryParams) => <MatchTopHatstatsTable<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, 
        (props, queryParams) => <MatchSurprisingTable<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    pagesMap.set(PagesEnum.DREAM_TEAM, 
        (props, queryParams) => <DreamTeamPage<WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, (_props, _queryParams) => <TeamSearchPage />)
    return pagesMap
}
