import React from 'react';
import TotalOverview from "../../rest/models/overview/TotalOverview";
import { getTotalOverview } from '../../rest/Client'
import LevelDataProps from '../LevelDataProps';
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
import ExecutableComponent from '../sections/ExecutableComponent';
import { SectionState } from '../sections/Section';
import { Col, Row } from 'react-bootstrap';


interface State {
    totalOverview?: TotalOverview
}

interface OverviewPageProps<Data extends LevelData, LevelProps extends LevelDataProps<Data>> {
    levelDataProps: LevelProps
}

export interface LeagueId {
    leagueId: number
}

abstract class OverviewPage<Data extends LevelData, LevelProps extends LevelDataProps<Data>> extends
        ExecutableComponent<OverviewPageProps<Data, LevelProps>, State & SectionState, TotalOverview, {}> {
    constructor(props: OverviewPageProps<Data, LevelProps>) {
        super(props)
        
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {},
            collapsed: false
        }
    }

    executeDataRequest(_dataRequest: {}, callback: (loadingState: LoadingEnum, result?: TotalOverview) => void) {
        let request = this.props.levelDataProps.createOverviewRequest()
        getTotalOverview(request, callback)
    }

    stateFromResult(result?: TotalOverview): State & SectionState {
        return {
            totalOverview: (result) ? result : this.state.totalOverview,
            collapsed: this.state.collapsed
        }
    }

    abstract linkProviderFunc<Entity extends LeagueId>(page: PagesEnum, sortingField: string): (text: string | JSX.Element, season: number, round: number, entity: Entity) => HattidLink<any>

    renderSection(): JSX.Element {
        if (!this.state.totalOverview) {
            return <></>
        } else {
            return <>
            <Row className="mb-2"> 
                <Col lg={4}>
                    <NumberOverviewSection<Data> 
                        initialData={this.state.totalOverview?.numberOverview} 
                        levelDataProps={this.props.levelDataProps}
                    />
                </Col>
                <Col lg={4}>
                    <FormationsOverviewSection<Data> 
                        initialData={this.state.totalOverview?.formations} 
                        levelDataProps={this.props.levelDataProps}
                    />
                </Col>
                <Col lg={4}>
                    <AveragesOverviewSection<Data>  
                        initialData={this.state.totalOverview?.averageOverview} 
                        levelDataProps={this.props.levelDataProps}
                    />
                </Col>
            </Row>
            <Row className="mb-2"> 
                <Col>
                    <SurprisingMatchesOverviewSection<Data>  
                        initialData={this.state.totalOverview?.surprisingMatches} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.MATCH_SURPRISING, 'abs_hatstats_difference')} 
                    />
                </Col>
            </Row>
            <Row className="mb-2"> 
                <Col lg={6} >
                    <HatstatsTeamOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topHatstatsTeams} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.TEAM_HATSTATS, 'hatstats')} 
                    />
                 </Col>
               <Col lg={6}>
                    <SalaryTeamOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topSalaryTeams} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.TEAM_SALARY_TSI, 'salary')}
                    />
                </Col>
            </Row>
            <Row className="mb-2"> 
                <Col>
                    <TopMatchesOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topMatches} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.MATCH_TOP_HATSTATS, 'sum_hatstats')} 
                    />
                </Col>
            </Row>
             <Row className="mb-2"> 
                <Col lg={6}>
                    <SalaryPlayerOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topSalaryPlayers} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.PLAYER_SALARY_TSI, 'salary')}
                    />
                </Col>
                <Col lg={6}>
                    <RatingPlayerOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topRatingPlayers} 
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.PLAYER_RATINGS, 'rating')}        
                    />
                </Col> 
            </Row>
            <Row className="mb-2">
                <Col>
                    <MatchAttendanceOverviewSection
                        initialData={this.state.totalOverview?.topMatchAttendance}
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.MATCH_SPECTATORS, 'sold_total')}
                    />
                </Col>
            </Row>
            <Row className="mb-2">
                <Col lg={6}>
                    <VictoriesTeamOverviewSection
                        initialData={this.state.totalOverview?.topTeamVictories}
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.TEAM_STREAK_TROPHIES, 'number_of_victories')}
                    />
                </Col>
                <Col lg={6}>
                    <SeasonScorersOverviewSection
                        initialData={this.state.totalOverview?.topSeasonScorers}
                        levelDataProps={this.props.levelDataProps}
                        linkProvider={this.linkProviderFunc(PagesEnum.PLAYER_GOAL_GAMES, 'scored')}
                    />
                </Col>
            </Row>
        </>
        }
        
    }
}

export default OverviewPage