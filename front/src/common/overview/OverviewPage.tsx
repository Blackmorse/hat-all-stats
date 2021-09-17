import React from 'react';
import TotalOverview from "../../rest/models/overview/TotalOverview";
import { getTotalOverview } from '../../rest/Client'
import LevelDataProps from '../LevelDataProps';
import './OverviewPage.css'
import '../sections/StatisticsSection.css'
import NumberOverviewSection from './NumberOverviewSection'
import FormationsOverviewSection from './FormationsOverviewSection'
import AveragesOverviewSection from './AveragesOverviewSection'
import SurprisingMatchesOverviewSection from './SurprisingMatchesOverviewSection'
import HatstatsTeamOverviewSection from './HatstatsTeamOverviewSection'
import SalaryTeamOverviewSection from './SalaryTeamOverviewSection'
import TopMatchesOverviewSection from './TopMatchesOverviewSection'
import SalaryPlayerOverviewSection from './SalaryPlayerOverviewSection'
import RatingPlayerOverviewSection from './RatingPlayerOverviewSection'
import MatchAttendanceOverviewSection from './MatchAttendanceOverviewSection'
import VictoriesTeamOverviewSection from './VictoriesTeamOverviewSection'
import SeasonScorersOverviewSection from './SeasonScorersOverviewSection'
import LevelData from '../../rest/models/leveldata/LevelData';
import { PagesEnum } from '../enums/PagesEnum';
import HattidLink from '../links/HattidLink';
import { LoadingEnum } from '../enums/LoadingEnum';
import ExecutableComponent, { LoadableState } from '../sections/ExecutableComponent';
import { SectionState } from '../sections/Section';


interface State {
    totalOverview?: TotalOverview
}

interface OverviewPageProps<Data extends LevelData, LevelProps extends LevelDataProps<Data>> {
    levelDataProps: LevelProps
}

export interface LeagueId {
    leagueId: number
}

type OverviewState = LoadableState<State, {}> & SectionState

abstract class OverviewPage<Data extends LevelData, LevelProps extends LevelDataProps<Data>> extends
        ExecutableComponent<OverviewPageProps<Data, LevelProps>, State, TotalOverview, {}, OverviewState> {
    constructor(props: OverviewPageProps<Data, LevelProps>) {
        super(props)
        
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {},
            state: {},
            collapsed: false
        }
    }

    executeDataRequest(_dataRequest: {}, callback: (loadingState: LoadingEnum, result?: TotalOverview) => void) {
        let request = this.props.levelDataProps.createOverviewRequest()
        getTotalOverview(request, callback)
    }

    stateFromResult(result?: TotalOverview): State {
        return {
            totalOverview: (result) ? result : this.state.state.totalOverview
        }
    }

    abstract linkProviderFunc<Entity extends LeagueId>(page: PagesEnum, sortingField: string): (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any>

    renderSection(): JSX.Element {
        if (!this.state.state.totalOverview) {
            return <></>
        } else {
            return <>
            <div className="section_row"> 
                <div className="section_row_one_third_element">
                    <NumberOverviewSection<Data> 
                        initialData={this.state.state.totalOverview?.numberOverview} 
                        levelDataProps={this.props.levelDataProps}
                    />
                </div>
                <div className="section_row_one_third_element">
                    <FormationsOverviewSection<Data> 
                        initialData={this.state.state.totalOverview?.formations} 
                        levelDataProps={this.props.levelDataProps}
                    />
                </div>
                <div className="section_row_one_third_element">
                    <AveragesOverviewSection<Data>  
                        initialData={this.state.state.totalOverview?.averageOverview} 
                        levelDataProps={this.props.levelDataProps}
                    />
                </div>
            </div>
            <div className="section_row"> 
                <SurprisingMatchesOverviewSection<Data>  
                    initialData={this.state.state.totalOverview?.surprisingMatches} 
                    levelDataProps={this.props.levelDataProps}
                       linkProvider={this.linkProviderFunc(PagesEnum.MATCH_SURPRISING, 'abs_hatstats_difference')} 
                    />
            </div>
            <div className="section_row"> 
                <div className="section_row_half_element">
                    <HatstatsTeamOverviewSection<Data>  
                        initialData={this.state.state.totalOverview?.topHatstatsTeams} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.TEAM_HATSTATS, 'hatstats')} 
                    />
                 </div>
               <div className="section_row_half_element">
                    <SalaryTeamOverviewSection<Data>  
                        initialData={this.state.state.totalOverview?.topSalaryTeams} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.TEAM_SALARY_TSI, 'salary')}
                    />
                </div>
            </div>
            <div className="section_row"> 
                <TopMatchesOverviewSection<Data>  
                    initialData={this.state.state.totalOverview?.topMatches} 
                    levelDataProps={this.props.levelDataProps}
                    linkProvider={this.linkProviderFunc(PagesEnum.MATCH_TOP_HATSTATS, 'sum_hatstats')} 
                />
            </div>
             <div className="section_row"> 
                <div className="section_row_half_element">
                    <SalaryPlayerOverviewSection<Data>  
                        initialData={this.state.state.totalOverview?.topSalaryPlayers} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.PLAYER_SALARY_TSI, 'salary')}
                    />
                </div>
                <div className="section_row_half_element">
                    <RatingPlayerOverviewSection<Data>  
                        initialData={this.state.state.totalOverview?.topRatingPlayers} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.PLAYER_RATINGS, 'rating')}        
                    />
                </div> 
            </div>
            <div className="section_row">
                <MatchAttendanceOverviewSection
                    initialData={this.state.state.totalOverview?.topMatchAttendance}
                    levelDataProps={this.props.levelDataProps}
                    linkProvider={this.linkProviderFunc(PagesEnum.MATCH_SPECTATORS, 'sold_total')}
                />
            </div>
            <div className="section_row">
                <div className="section_row_half_element">
                    <VictoriesTeamOverviewSection
                        initialData={this.state.state.totalOverview?.topTeamVictories}
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.TEAM_STREAK_TROPHIES, 'number_of_victories')}
                    />
                </div>
                <div className="section_row_half_element">
                    <SeasonScorersOverviewSection
                        initialData={this.state.state.totalOverview?.topSeasonScorers}
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.PLAYER_GOAL_GAMES, 'scored')}
                    />
                </div>
            </div>
        </>
        }
        
    }
}

export default OverviewPage