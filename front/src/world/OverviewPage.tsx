import React from 'react';
import StatisticsSection from "../common/StatisticsSection";
import TotalOverview from "../rest/models/overview/TotalOverview";
import { getTotalOverview } from '../rest/Client'
import WorldData from '../rest/models/leveldata/WorldData';
import OverviewRequest from '../rest/models/request/OverviewRequest';
import ModelTableWorldProps from './ModelTableWorldProps';
import { ModelTablePropsWrapper } from '../common/ModelTable';
import './OverviewPage.css'
import '../common/StatisticsSection.css'
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

class OverviewPage extends StatisticsSection<ModelTablePropsWrapper<WorldData, ModelTableWorldProps>, State> {
    constructor(props: ModelTablePropsWrapper<WorldData, ModelTableWorldProps>) {
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
            season: this.props.modelTableProps.currentSeason(),
            round: this.props.modelTableProps.currentRound()
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
                        modelTableProps={this.props.modelTableProps}/>
                </div>
                <div className="section_row_one_third_element">
                    <FormationsOverviewSection 
                        initialData={this.state.totalOverview?.formations} 
                        modelTableProps={this.props.modelTableProps}/>
                </div>
                <div className="section_row_one_third_element">
                    <AveragesOverviewSection 
                        initialData={this.state.totalOverview?.averageOverview} 
                        modelTableProps={this.props.modelTableProps}/>
                </div>
            </div>
            <div className="section_row"> 
                <SurprisingMatchesOverviewSection 
                    initialData={this.state.totalOverview?.surprisingMatches} 
                    modelTableProps={this.props.modelTableProps}/>
            </div>
            <div className="section_row"> 
                <div className="section_row_half_element">
                    <HatstatsTeamOverviewSection 
                        initialData={this.state.totalOverview?.topHatstatsTeams} 
                        modelTableProps={this.props.modelTableProps}/>
                </div>
                <div className="section_row_half_element">
                    <SalaryTeamOverviewSection 
                        initialData={this.state.totalOverview?.topSalaryTeams} 
                        modelTableProps={this.props.modelTableProps}/>
                </div>
            </div>
            <div className="section_row"> 
                <TopMatchesOverviewSection 
                    initialData={this.state.totalOverview?.topMatches} 
                    modelTableProps={this.props.modelTableProps}/>
            </div>
            <div className="section_row"> 
                <div className="section_row_half_element">
                    <SalaryPlayerOverviewSection 
                        initialData={this.state.totalOverview?.topSalaryPlayers} 
                        modelTableProps={this.props.modelTableProps}/>
                </div>
                <div className="section_row_half_element">
                    <RatingPlayerOverviewSection 
                        initialData={this.state.totalOverview?.topRatingPlayers} 
                        modelTableProps={this.props.modelTableProps}/>
                </div>
            </div>
        </>
        }
        
    }
}

export default OverviewPage