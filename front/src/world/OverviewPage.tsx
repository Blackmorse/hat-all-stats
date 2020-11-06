import React from 'react';
import StatisticsSection from "../common/sections/StatisticsSection";
import TotalOverview from "../rest/models/overview/TotalOverview";
import { getTotalOverview } from '../rest/Client'
import WorldData from '../rest/models/leveldata/WorldData';
import OverviewRequest from '../rest/models/request/OverviewRequest';
import WorldLevelDataProps from './WorldLevelDataProps';
import { LevelDataPropsWrapper } from '../common/LevelDataProps';
import './OverviewPage.css'
import '../common/sections/StatisticsSection.css'
import NumberOverviewSection from './overview/NumberOverviewSection'
import FormationsOverviewSection from './overview/FormationsOverviewSection'
import AveragesOverviewSection from './overview/AveragesOverviewSection'
import SurprisingMatchesOverviewSection from './overview/SurprisingMatchesOverviewSection'
import HatstatsTeamOverviewSection from './overview/HatstatsTeamOverviewSection'
import SalaryTeamOverviewSection from './overview/SalaryTeamOverviewSection'
import TopMatchesOverviewSection from './overview/TopMatchesOverviewSection'
import SalaryPlayerOverviewSection from './overview/SalaryPlayerOverviewSection'
import RatingPlayerOverviewSection from './overview/RatingPlayerOverviewSection'

interface State {
    dataLoading: boolean,
    isError: boolean,
    totalOverview?: TotalOverview
}

class OverviewPage extends StatisticsSection<LevelDataPropsWrapper<WorldData, WorldLevelDataProps>, State> {
    constructor(props: LevelDataPropsWrapper<WorldData, WorldLevelDataProps>) {
        super(props, 'overview.world_overview')
        this.state = {
            dataLoading: false,
            isError: false
        }

        this.updateCurrent=this.updateCurrent.bind(this)
    }

    updateCurrent(): void {
        this.componentDidMount()
    }

    componentDidMount() {
        this.setState({
            dataLoading: true,
            isError: false,
            totalOverview: this.state.totalOverview
        })
        let request: OverviewRequest = {
            season: this.props.levelDataProps.currentSeason(),
            round: this.props.levelDataProps.currentRound()
        } 

        getTotalOverview(request, totalOverview => this.setState({
            dataLoading: false,
            isError: false,
            totalOverview: totalOverview
        }), () => this.setState({
            dataLoading: false,
            isError: true,
            totalOverview: this.state.totalOverview
        }))
    }

    renderSection(): JSX.Element {
        if (!this.state.totalOverview) {
            return <></>
        } else {
            return <>
            <div className="section_row"> 
                <div className="section_row_one_third_element">
                    <NumberOverviewSection 
                        initialData={this.state.totalOverview?.numberOverview} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_one_third_element">
                    <FormationsOverviewSection 
                        initialData={this.state.totalOverview?.formations} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_one_third_element">
                    <AveragesOverviewSection 
                        initialData={this.state.totalOverview?.averageOverview} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
            </div>
            <div className="section_row"> 
                <SurprisingMatchesOverviewSection 
                    initialData={this.state.totalOverview?.surprisingMatches} 
                    levelDataProps={this.props.levelDataProps}/>
            </div>
            <div className="section_row"> 
                <div className="section_row_half_element">
                    <HatstatsTeamOverviewSection 
                        initialData={this.state.totalOverview?.topHatstatsTeams} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_half_element">
                    <SalaryTeamOverviewSection 
                        initialData={this.state.totalOverview?.topSalaryTeams} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
            </div>
            <div className="section_row"> 
                <TopMatchesOverviewSection 
                    initialData={this.state.totalOverview?.topMatches} 
                    levelDataProps={this.props.levelDataProps}/>
            </div>
            <div className="section_row"> 
                <div className="section_row_half_element">
                    <SalaryPlayerOverviewSection 
                        initialData={this.state.totalOverview?.topSalaryPlayers} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_half_element">
                    <RatingPlayerOverviewSection 
                        initialData={this.state.totalOverview?.topRatingPlayers} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
            </div>
        </>
        }
        
    }
}

export default OverviewPage