import React from 'react';
import LevelData from '../../rest/models/leveldata/LevelData';
import LevelDataProps from '../../common/LevelDataProps';
import StatisticsSection from '../sections/StatisticsSection';
import OverviewRequest from '../../rest/models/request/OverviewRequest';
import '../../common/sections/StatisticsSection.css'
import SeasonRoundSelector from './SeasonRoundSelector'
import { LoadingEnum } from '../enums/LoadingEnum';

export interface OverviewSectionProps<Data extends LevelData, OverviewEntity> {
    initialData?: OverviewEntity,
    levelDataProps: LevelDataProps<Data>
}

interface State<OverviewEntity> {
    data?: OverviewEntity
}

interface Request {
    round: number,
    season: number
}

abstract class OverviewSection<Data extends LevelData, OverviewEntity, OverviewProps extends OverviewSectionProps<Data, OverviewEntity>> 
    extends StatisticsSection<OverviewProps, State<OverviewEntity>, OverviewEntity, Request> {
    isWorldData: boolean

    constructor(props: OverviewProps, title: string) {
        super(props, title)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {
                season: props.levelDataProps.currentSeason(),
                round: props.levelDataProps.currentRound()
            },
            state: {
                data: props.initialData
            }
        }

        this.isWorldData = 'isWorldData' in  props.levelDataProps.levelData
    }

    abstract loadOverviewEntity(overviewRequest: OverviewRequest,
            callback: (loadingEnum: LoadingEnum, entities?: OverviewEntity) => void): void


    executeDataRequest(dataRequest: Request, 
            callback: (loadingState: LoadingEnum, result?: OverviewEntity) => void) {
        let request: OverviewRequest = this.props.levelDataProps.createOverviewRequest()
        request.season = dataRequest.season
        request.round = dataRequest.round

        this.loadOverviewEntity(request, callback)
    }

    stateFromResult(result?: OverviewEntity): State<OverviewEntity> {
        return {
            data: (result) ? result : this.state.state.data
        }
    }

    renderSection(): JSX.Element {
        let data = this.state.state.data
        if (data) {
            let dataDefined = data
            return <div className="statistics_section_inner">
                
                {this.renderOverviewSection(dataDefined)}
                <SeasonRoundSelector 
                    season={this.state.dataRequest.season}
                    offsettedSeason={this.state.dataRequest.season + this.props.levelDataProps.levelData.seasonOffset}
                    round={this.state.dataRequest.round}
                    seasonRoundInfo={this.props.levelDataProps.seasonRoundInfo()}
                    callback={(season, round) => this.updateWithRequest({season: season, round: round})} />
            </div>
        } else {
            return <></>
        }
    }

    abstract renderOverviewSection(data: OverviewEntity): JSX.Element
}

export default OverviewSection