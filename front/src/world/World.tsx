import React from 'react'
import { getWorldData } from '../rest/Client'
import WorldTopMenu from './WorldTopMenu'
import { RouteComponentProps } from 'react-router';
import WorldOverviewPage from './WorldOverviewPage'
import WorldLevelDataProps from './WorldLevelDataProps'
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu'
import LevelLayout from '../common/layouts/LevelLayout'
import WorldData from '../rest/models/leveldata/WorldData'
import LevelDataProps from '../common/LevelDataProps';
import QueryParams from '../common/QueryParams'
import { PagesEnum } from '../common/enums/PagesEnum'
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import DreamTeamPage from '../common/pages/DreamTeamPage';


interface Props extends RouteComponentProps<{}>{}

class World extends LevelLayout<Props, WorldData, LevelDataProps<WorldData>> {
    constructor(props: Props) {
        const pagesMap = new Map<PagesEnum, (props: WorldLevelDataProps, queryParams: QueryParams) => JSX.Element>()
        pagesMap.set(PagesEnum.OVERVIEW, 
            (props, _queryParams) => <WorldOverviewPage levelDataProps={props} title='overview.world_overview'/>)
        pagesMap.set(PagesEnum.TEAM_HATSTATS,
            (props, queryParams) => <TeamHatstatsTable<WorldData, WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
        pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, 
            (props, queryParams) => <PlayerSalaryTsiTable<WorldData, WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
        pagesMap.set(PagesEnum.PLAYER_RATINGS, 
            (props, queryParams) => <PlayerRatingsTable<WorldData, WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
        pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, 
            (props, queryParams) => <MatchTopHatstatsTable<WorldData, WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
        pagesMap.set(PagesEnum.MATCH_SURPRISING, 
            (props, queryParams) => <MatchSurprisingTable<WorldData, WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
        pagesMap.set(PagesEnum.DREAM_TEAM, 
            (props, queryParams) => <DreamTeamPage<WorldData, WorldLevelDataProps> levelDataProps={props} queryParams={queryParams} showCountryFlags={true} />)
    
        super(props, pagesMap)
    
        this.leagueIdSelected=this.leagueIdSelected.bind(this)
    }

    documentTitle(data: WorldData): string {
        return 'World'
    }


    makeModelProps(levelData: WorldData): LevelDataProps<WorldData> {
        return new WorldLevelDataProps(levelData)
    }

    fetchLevelData(props: Props, 
            callback: (data: WorldData) => void,
            onError: () => void): void {
        getWorldData(callback, onError)
    }

    leagueIdSelected(leagueId: number) {
        this.props.history.push('/league/' + leagueId)
    }

    topMenu(): JSX.Element {
        return <WorldTopMenu worldData={this.state.levelData} 
            callback={this.leagueIdSelected}/>
    }

    topLeftMenu(): JSX.Element {
        return <WorldLeftLoadingMenu worldData={this.state.levelData}/>
    }
}

export default World