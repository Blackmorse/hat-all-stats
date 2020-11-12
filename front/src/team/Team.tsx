import React from 'react'
import { RouteComponentProps } from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import TeamData from '../rest/models/leveldata/TeamData';
import TeamLevelDataProps from './TeamLevelDataProps';
import { getTeamData } from '../rest/Client'
import { PagesEnum } from '../common/enums/PagesEnum';
import TeamTopMenu from './TeamTopMenu'
import TeamRankingsTable from './TeamRankingsTable'
import NearestMatchesTable from './NearestMatchesTable'
import PlayerGoalsGamesTable from '../common/tables/player/PlayerGoalsGamesTable';
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import QueryParams from '../common/QueryParams';

interface MatchParams {
    teamId: string
}

interface Props extends RouteComponentProps<MatchParams> {}

class Team extends CountryLevelLayout<Props, TeamData, TeamLevelDataProps> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: TeamLevelDataProps, queryParams: QueryParams) => JSX.Element>()
        pagesMap.set(PagesEnum.TEAM_OVERVIEW, (props, queryParams) => <>
                    <NearestMatchesTable levelDataProps={props} queryParams={queryParams}/>
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
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, 
            (props, queryParams) => <MatchTopHatstatsTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING, 
            (props, queryParams) => <MatchSurprisingTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
        pagesMap.set(PagesEnum.MATCH_SPECTATORS, 
            (props, queryParams) => <MatchSpectatorsTable<TeamData, TeamLevelDataProps> levelDataProps={props} queryParams={queryParams} />)

        super(props, pagesMap)
    }


    makeModelProps(levelData: TeamData): TeamLevelDataProps {
        return new TeamLevelDataProps(levelData)
    }

    fetchLevelData(props: Props, callback: (data: TeamData) => void): void {
        getTeamData(Number(this.props.match.params.teamId), callback)
    }
    topMenu(): JSX.Element {
        return <TeamTopMenu teamData={this.state.levelData}/>
    }

}

export default Team