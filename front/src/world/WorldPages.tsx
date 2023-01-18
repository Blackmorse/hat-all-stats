import React from 'react'
import WorldOverviewPage from './WorldOverviewPage'
import WorldLevelDataProps from './WorldLevelDataProps'
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
    const pagesMap = new Map<PagesEnum, (props: WorldLevelDataProps) => JSX.Element>()
    pagesMap.set(PagesEnum.OVERVIEW, props => <WorldOverviewPage levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_HATSTATS, props => <TeamHatstatsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI, props => <TeamSalaryTSITable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS, props => <OldestTeamsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, props => <PlayerSalaryTsiTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, props => <PlayerRatingsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, props => <MatchTopHatstatsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, props => <MatchSurprisingTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.DREAM_TEAM, props => <DreamTeamPage<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, _props => <TeamSearchPage />)
    return pagesMap
}
