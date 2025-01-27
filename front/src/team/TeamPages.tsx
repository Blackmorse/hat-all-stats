import React from 'react';
import {PagesEnum} from '../common/enums/PagesEnum';
import PromotionsTable from '../common/pages/PromotionsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerGoalsGamesTable from '../common/tables/player/PlayerGoalsGamesTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import OpponentAnalyzerSection from './analyzer/OpponentAnalyzerSection';
import CompareSearchSection from './compare/CompareSearchSection';
import CompareTeamsPage from './compare/HookCompareTeamsPage';
import PlayedAndUpcomingMatchesTable from './matches/PlayedAndUpcomingMatchesTable';
import TeamLevelDataProps from './TeamLevelDataProps';
import TeamMatches from './TeamMatches';
import TeamRankingsTable from './TeamRankingsTable';
import TeamSamePeriodTeams from './sameperiod/TeamSamePeriodTeam';
import { Card } from 'react-bootstrap'
import '../i18n'
import {Translation, useTranslation} from 'react-i18next';
import Section from "../common/sections/HookSection";

export default function pages(): Map<PagesEnum, (props: TeamLevelDataProps) => JSX.Element> {
    const pagesMap = new Map<PagesEnum, (props: TeamLevelDataProps) => JSX.Element>()

    pagesMap.set(PagesEnum.TEAM_OVERVIEW, props => <Translation>{ (t, { i18n }) => <Card className='mt-3 shadow'>
            <Card.Header className='lead'>{t(PagesEnum.TEAM_OVERVIEW)}</Card.Header>
            <Card.Body>
                <CompareSearchSection levelDataProps={props}/>
                <OpponentAnalyzerSection props={props}/>
                <Section
                    element={<TeamRankingsTable props={props} />}
                    title={i18n.t('menu.team_rankings')}
                />
            </Card.Body>
        </Card>}
    </Translation>)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, props => <PlayerGoalsGamesTable<TeamLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_CARDS, props => <PlayerCardsTable<TeamLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, props => <PlayerSalaryTsiTable<TeamLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, props => <PlayerRatingsTable<TeamLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES, props => <PlayerInjuriesTable<TeamLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_MATCHES, props => <>
            <PlayedAndUpcomingMatchesTable teamId={props.teamId()}/> 
            <TeamMatches levelDataProps={props} />
        </>)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, props => <MatchTopHatstatsTable<TeamLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, props => <MatchSurprisingTable<TeamLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS, props => <MatchSpectatorsTable<TeamLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.CREATED_SAME_TIME_TEAMS, props => <TeamSamePeriodTeams levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_COMPARSION, props => <CompareTeamsPage levelDataProps={props} />)


    pagesMap.set(PagesEnum.PROMOTIONS, props => <PromotionsTable<TeamLevelDataProps> levelDataProps={props} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, _props => <TeamSearchPage />)

    return pagesMap
}
