import React from 'react';
import StatisticsSection from "../sections/StatisticsSection";
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
import LevelData from '../../rest/models/leveldata/LevelData';


interface State {
    dataLoading: boolean,
    isError: boolean,
    totalOverview?: TotalOverview
}

interface OverviewPageProps<Data extends LevelData, LevelProps extends LevelDataProps<Data>> {
    levelDataProps: LevelProps,
    title: string
}

class OverviewPage<Data extends LevelData, LevelProps extends LevelDataProps<Data>> 
        extends StatisticsSection<OverviewPageProps<Data, LevelProps>, State> {
    constructor(props: OverviewPageProps<Data, LevelProps>) {
        super(props, props.title)
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
        
        let request = this.props.levelDataProps.createOverviewRequest()

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
                    <NumberOverviewSection<Data> 
                        initialData={this.state.totalOverview?.numberOverview} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_one_third_element">
                    <FormationsOverviewSection<Data> 
                        initialData={this.state.totalOverview?.formations} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_one_third_element">
                    <AveragesOverviewSection<Data>  
                        initialData={this.state.totalOverview?.averageOverview} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
            </div>
            <div className="section_row"> 
                <SurprisingMatchesOverviewSection<Data>  
                    initialData={this.state.totalOverview?.surprisingMatches} 
                    levelDataProps={this.props.levelDataProps}/>
            </div>
            <div className="section_row"> 
                <div className="section_row_half_element">
                    <HatstatsTeamOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topHatstatsTeams} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_half_element">
                    <SalaryTeamOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topSalaryTeams} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
            </div>
            <div className="section_row"> 
                <TopMatchesOverviewSection<Data>  
                    initialData={this.state.totalOverview?.topMatches} 
                    levelDataProps={this.props.levelDataProps}/>
            </div>
            <div className="section_row"> 
                <div className="section_row_half_element">
                    <SalaryPlayerOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topSalaryPlayers} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
                <div className="section_row_half_element">
                    <RatingPlayerOverviewSection<Data>  
                        initialData={this.state.totalOverview?.topRatingPlayers} 
                        levelDataProps={this.props.levelDataProps}/>
                </div>
            </div>
        </>
        }
        
    }
}

export default OverviewPage