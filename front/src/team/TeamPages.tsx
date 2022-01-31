import React from 'react';
import {PagesEnum} from '../common/enums/PagesEnum';
import PromotionsTable from '../common/pages/PromotionsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage';
import QueryParams from '../common/QueryParams';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerGoalsGamesTable from '../common/tables/player/PlayerGoalsGamesTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import TeamData from '../rest/models/leveldata/TeamData';
import OpponentAnalyzerSection from './analyzer/OpponentAnalyzerSection';
import CompareSearchSection from './compare/CompareSearchSection';
import CompareTeamsPage from './compare/CompareTeamsPage';
import PlayedAndUpcomingMatchesTable from './matches/PlayedAndUpcomingMatchesTable';
import TeamLevelDataProps from './TeamLevelDataProps';
import TeamMatches from './TeamMatches';
import TeamRankingsTable from './TeamRankingsTable';
import TeamSamePeriodTeams from './sameperiod/TeamSamePeriodTeam';

export default function pages(): Map<PagesEnum, (props: TeamLevelDataProps, queryParams: QueryParams) => JSX.Element> {
    const pagesMap = new Map<PagesEnum, (props: TeamLevelDataProps, queryParams: QueryParams) => JSX.Element>()

    pagesMap.set(PagesEnum.TEAM_OVERVIEW, (props, queryParams) => <>
                <CompareSearchSection levelDataProps={props}/>
                <OpponentAnalyzerSection props={props}/>
                <TeamRankingsTable levelDataProps={props} queryParams={queryParams}/>
            </>)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, 
        (props, queryParams) => <PlayerGoalsGamesTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_CARDS, 
        (props, queryParams) => <PlayerCardsTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, 
        (props, queryParams) => <PlayerSalaryTsiTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_RATINGS,
        (props, queryParams) => <PlayerRatingsTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES, 
        (props, queryParams) => <PlayerInjuriesTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_MATCHES,
        (props, queryParams) => <>
            <PlayedAndUpcomingMatchesTable teamId={props.teamId()}/> 
            <TeamMatches levelDataProps={props} queryParams={queryParams} />
        </>)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, 
        (props, queryParams) => <MatchTopHatstatsTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, 
        (props, queryParams) => <MatchSurprisingTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS, 
        (props, queryParams) => <MatchSpectatorsTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.CREATED_SAME_TIME_TEAMS,
        (props, queryParams) => <TeamSamePeriodTeams levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.TEAM_COMPARSION,
        (props, queryParams) => <CompareTeamsPage levelDataProps={props} queryParams={queryParams}/>)


    pagesMap.set(PagesEnum.PROMOTIONS,
        (props, queryParams) => <PromotionsTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, (_props, _queryParams) => <TeamSearchPage />)

    return pagesMap
}
