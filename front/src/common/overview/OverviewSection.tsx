import React from 'react';
import LevelData from '../../rest/models/leveldata/LevelData';
import LevelDataProps from '../../common/LevelDataProps';
import StatisticsSection from '../../common/sections/StatisticsSection';
import OverviewRequest from '../../rest/models/request/OverviewRequest';
import '../../common/sections/StatisticsSection.css'
import SeasonRoundSelector from './SeasonRoundSelector'
import WorldData from '../../rest/models/leveldata/WorldData';
import LeagueLink from '../../common/links/LeagueLink';

export interface OverviewSectionProps<Data extends LevelData, OverviewEntity> {
    initialData?: OverviewEntity,
    levelDataProps: LevelDataProps<Data>
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
    isWorldData: boolean

    constructor(props: OverviewSectionProps<Data, OverviewEntity>, title: string) {
        super(props, title)
        this.state = {
            dataLoading: false,
            isError: false,
            data: props.initialData,
            selectedSeason: props.levelDataProps.currentSeason(),
            selectedRound: props.levelDataProps.currentRound()
        }

        this.isWorldData = 'countries' in  props.levelDataProps.levelData
        
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

        let request: OverviewRequest = this.props.levelDataProps.createOverviewRequest()
        request.season = season
        request.round = round

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
        let leagueNameFunc: (id: number) => JSX.Element

        if (this.isWorldData) {
            let nameMap = new Map(((this.props.levelDataProps.levelData as any) as WorldData).countries)
            leagueNameFunc = (id) => <td className="value">
                    <LeagueLink tableLink={true} id={id} text={nameMap.get(id) || ''}/>
                </td>
        } else {
            leagueNameFunc = (id) => <></>
        }
        let data = this.state.data
        if (data) {
            let dataDefined = data
            return <div className="statistics_section_inner">
                
                {this.renderOverviewSection(dataDefined, leagueNameFunc)}
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

    abstract renderOverviewSection(data: OverviewEntity, leagueNameFunc: (id: number) => JSX.Element): JSX.Element
}

export default OverviewSection