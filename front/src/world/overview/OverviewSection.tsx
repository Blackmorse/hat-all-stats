import React from 'react';
import LevelData from '../../rest/models/leveldata/LevelData';
import { ModelTableProps } from '../../common/ModelTable';
import StatisticsSection from '../../common/StatisticsSection';
import OverviewRequest from '../../rest/models/request/OverviewRequest';
import '../../common/StatisticsSection.css'
import SeasonRoundSelector from './SeasonRoundSelector'

export interface OverviewSectionProps<Data extends LevelData, OverviewEntity> {
    initialData?: OverviewEntity,
    modelTableProps: ModelTableProps<Data>
}

interface State<OverviewEntity> {
    dataLoading: boolean,
    isError: boolean,
    data?: OverviewEntity,
    selectedSeason: number,
    selectedRound: number
}

abstract class OverviewSection<Data extends LevelData, OverviewEntity> 
    extends StatisticsSection<OverviewSectionProps<Data, OverviewEntity>, State<OverviewEntity>> {

    constructor(props: OverviewSectionProps<Data, OverviewEntity>, title: string) {
        super(props, title)
        this.state = {
            dataLoading: false,
            isError: false,
            data: props.initialData,
            selectedSeason: props.modelTableProps.currentSeason(),
            selectedRound: props.modelTableProps.currentRound()
        }

        this.loadRound=this.loadRound.bind(this)
    }

    abstract loadOverviewEntity(overviewRequest: OverviewRequest,
            callback: (entities: OverviewEntity) => void,
            onError: () => void): void

    
    loadRound(season: number, round: number) {
        this.setState({
            dataLoading: true,
            isError: false,
            data: this.state.data,
            selectedSeason: this.state.selectedSeason,
            selectedRound: this.state.selectedRound
        })

        let request: OverviewRequest = {
            season: season,
            round: round
        }

        this.loadOverviewEntity(request,
            overviewEntity => this.setState({
                dataLoading: false,
                isError: false,
                data: overviewEntity,
                selectedSeason: season,
                selectedRound: round
            }),
            () => this.setState({
                dataLoading: false,
                isError: true,
                data: this.state.data,
                selectedSeason: this.state.selectedSeason,
                selectedRound: this.state.selectedRound
            }))
    }

    updateCurrent(): void {
        this.loadRound(this.state.selectedSeason, this.state.selectedRound)
    }

    renderSection(): JSX.Element {
        let data = this.state.data
        if (data) {
            let dataDefined = data
            return <div className="statistics_section_inner">
                
                {this.renderOverviewSection(dataDefined)}
                <SeasonRoundSelector 
                    season={this.state.selectedSeason}
                    round={this.state.selectedRound}
                    seasonRoundInfo={this.props.modelTableProps.seasonRoundInfo()}
                    callback={this.loadRound} />
            </div>
        } else {
            return <></>
        }
    }

    abstract renderOverviewSection(data: OverviewEntity): JSX.Element
}

export default OverviewSection