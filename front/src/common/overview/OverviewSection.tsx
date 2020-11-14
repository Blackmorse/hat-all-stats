import React from 'react';
import LevelData from '../../rest/models/leveldata/LevelData';
import LevelDataProps from '../../common/LevelDataProps';
import StatisticsSection from '../../common/sections/StatisticsSection';
import OverviewRequest from '../../rest/models/request/OverviewRequest';
import '../../common/sections/StatisticsSection.css'
import SeasonRoundSelector from './SeasonRoundSelector'
import { LoadingEnum } from '../enums/LoadingEnum';

export interface OverviewSectionProps<Data extends LevelData, OverviewEntity> {
    initialData?: OverviewEntity,
    levelDataProps: LevelDataProps<Data>
}

interface State<OverviewEntity> {
    loadingState: LoadingEnum,
    data?: OverviewEntity,
    selectedSeason: number,
    selectedRound: number
}

abstract class OverviewSection<Data extends LevelData, OverviewEntity, OverviewProps extends OverviewSectionProps<Data, OverviewEntity>> 
    extends StatisticsSection<OverviewProps, State<OverviewEntity>> {
    isWorldData: boolean

    constructor(props: OverviewProps, title: string) {
        super(props, title)
        this.state = {
            loadingState: LoadingEnum.OK,
            data: props.initialData,
            selectedSeason: props.levelDataProps.currentSeason(),
            selectedRound: props.levelDataProps.currentRound()
        }

        this.isWorldData = 'countries' in  props.levelDataProps.levelData
        
        this.loadRound=this.loadRound.bind(this)
    }

    abstract loadOverviewEntity(overviewRequest: OverviewRequest,
            callback: (loadingEnum: LoadingEnum, entities?: OverviewEntity) => void): void

    loadRound(season: number, round: number) {
        this.setState({
            loadingState: LoadingEnum.LOADING,
            data: this.state.data,
            selectedSeason: this.state.selectedSeason,
            selectedRound: this.state.selectedRound
        })

        let request: OverviewRequest = this.props.levelDataProps.createOverviewRequest()
        request.season = season
        request.round = round

        this.loadOverviewEntity(request,
            (loadingStatus, overviewEntity) => this.setState({
                loadingState: loadingStatus,
                data: (overviewEntity) ? overviewEntity : this.state.data,
                selectedSeason: season,
                selectedRound: round
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
                    seasonRoundInfo={this.props.levelDataProps.seasonRoundInfo()}
                    callback={this.loadRound} />
            </div>
        } else {
            return <></>
        }
    }

    abstract renderOverviewSection(data: OverviewEntity): JSX.Element
}

export default OverviewSection