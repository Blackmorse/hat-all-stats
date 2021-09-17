import React from 'react'
import LevelData from '../../rest/models/leveldata/LevelData';
import LevelDataProps, { LevelDataPropsWrapper } from '../LevelDataProps';
import { LoadingEnum } from '../enums/LoadingEnum';
import DreamTeamPlayer from '../../rest/models/player/DreamTeamPlayer';
import { getDreamTeam } from '../../rest/Client'
import { StatsType, StatsTypeEnum } from '../../rest/models/StatisticsParameters';
import DreamTeamPlayerCard from './dreamteam/DreamTeamPlayerCard'
import './DreamTeamPage.css'
import '../../i18n'
import i18n from '../../i18n';
import StatsTypeSelector from '../selectors/StatsTypeSelector'
import SeasonSelector from '../selectors/SeasonSelector';
import FormationSelector, { Formation } from '../selectors/FormationSelector'
import { ratingFormatter } from '../Formatters'
import ExecutableComponent, { LoadableState } from '../sections/ExecutableComponent';
import Section, { SectionState } from '../sections/Section';

interface State {
    dreamTeamPlayers?: Array<DreamTeamPlayer>,
    formation: Formation
}

interface Request {
    statsType: StatsType,
    season: number
}

export interface DreamTeamPlayerPosition {
    player?: DreamTeamPlayer,
    position: string
}

class DreamTeamPageBase<Data extends LevelData, Props extends LevelDataProps<Data>> 
    extends ExecutableComponent<LevelDataPropsWrapper<Data, LevelDataProps<Data>>, State, Array<DreamTeamPlayer>, Request,
        LoadableState<State, Request> & SectionState> {

    constructor(props: LevelDataPropsWrapper<Data, Props>) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {
                statsType: {
                    statType: StatsTypeEnum.ROUND,
                    roundNumber: props.levelDataProps.currentRound(),
                },
                season: props.levelDataProps.currentSeason(),
            },
            state: {
                formation: new Formation(4, 4, 2)
            },
            collapsed: false
        }
        this.statsTypeChanged=this.statsTypeChanged.bind(this)
        this.seasonChanged=this.seasonChanged.bind(this)
        this.formationChanged=this.formationChanged.bind(this) 
    }

    executeDataRequest(dataRequest: Request, 
            callback: (loadingState: LoadingEnum, result?: Array<DreamTeamPlayer>) => void): void {
        getDreamTeam(this.props.levelDataProps.createLevelRequest(), 
            dataRequest.season, dataRequest.statsType, "rating", callback)
    }

    stateFromResult(result?: Array<DreamTeamPlayer>): State {
        return {
            dreamTeamPlayers: (result) ? result : this.state.state.dreamTeamPlayers,
            formation: this.state.state.formation
        }
    }

    statsTypeChanged(statType: StatsType) {
        let newRequest = Object.assign({}, this.state.dataRequest)
        newRequest.statsType = statType
        
        this.updateWithRequest(newRequest)
    }

    seasonChanged(season: number) {
        let newRequest = Object.assign({}, this.state.dataRequest)

        let rounds = this.props.levelDataProps.rounds(season)

        newRequest.season = season
        newRequest.statsType = {statType: StatsTypeEnum.ROUND, roundNumber: rounds[rounds.length - 1]}

        this.updateWithRequest(newRequest)
    }

    formationChanged(formation: Formation) {
        let newState = Object.assign({}, this.state.state)
        newState.formation = formation
        this.setState({
            state: newState
        })
    }


    private pushPlayer(array: Array<DreamTeamPlayerPosition>, index: number, 
            players: Array<DreamTeamPlayer>, position: string) {
        array.push((players.length > index) ? {player: players[index], position: position } : 
            { position: position})
    }

    private wings(dreamTeamPlayers: Array<DreamTeamPlayer>, linePositions: number, position: string): Array<DreamTeamPlayerPosition> {
        let result: Array<DreamTeamPlayerPosition> = []
        if (linePositions === 2) {
            return []
        } else {
            this.pushPlayer(result, 0, dreamTeamPlayers, position)
            this.pushPlayer(result, 1, dreamTeamPlayers, position)     
            return result;       
        }
    }

    private centers(dreamTeamPlayers: Array<DreamTeamPlayer>, linePositions: number, position: string): Array<DreamTeamPlayerPosition> {
        let result: Array<DreamTeamPlayerPosition> = []
        if(linePositions === 2 || linePositions === 4) {
            this.pushPlayer(result, 0, dreamTeamPlayers, position)
            this.pushPlayer(result, 1, dreamTeamPlayers, position)
        } else if (linePositions === 3) {
            this.pushPlayer(result, 0, dreamTeamPlayers, position)
        } else {
            this.pushPlayer(result, 0, dreamTeamPlayers, position)
            this.pushPlayer(result, 1, dreamTeamPlayers, position)
            this.pushPlayer(result, 2, dreamTeamPlayers, position)
        }
        return result
    }

    private forwardsKeeper(dreamTeamPlayers: Array<DreamTeamPlayer>, linePositions: number, position: string): Array<DreamTeamPlayerPosition> {
        let result: Array<DreamTeamPlayerPosition> = []
        Array.from(Array(linePositions).keys()).forEach(i => {
            this.pushPlayer(result, i, dreamTeamPlayers, position)
        })
        return result
    }
    
    private starsOfPlayers(players: Array<DreamTeamPlayerPosition>): number {
        if (players.length === 0) {
            return 0
        }
        return players.map(player => (player.player !== undefined) ? player.player.rating : 0).reduce((a, b) => a + b)
    }

    renderSection(): JSX.Element {
        if (this.state.state.dreamTeamPlayers === undefined) {
            return <></>
        }
        
        let keepers = this.state.state.dreamTeamPlayers.filter(player => player.role === 'keeper')
        let defenders = this.state.state.dreamTeamPlayers.filter(player => player.role === 'defender')
        let wingbacks = this.state.state.dreamTeamPlayers.filter(player => player.role === 'wingback')
        let midfielders = this.state.state.dreamTeamPlayers.filter(player => player.role === 'midfielder')
        let wingers = this.state.state.dreamTeamPlayers.filter(player => player.role === 'winger')
        let forwards = this.state.state.dreamTeamPlayers.filter(player => player.role === 'forward')

        let displayedKeepers = this.forwardsKeeper(keepers, 1, i18n.t('dream_team.keeper'))
        let displayedWingbacks = this.wings(wingbacks, this.state.state.formation.defenders, i18n.t('dream_team.wingback'))
        let displayedDefs = this.centers(defenders, this.state.state.formation.defenders, i18n.t('dream_team.defender'))
        let displayedWings = this.wings(wingers, this.state.state.formation.midfielders, i18n.t('dream_team.winger'))
        let displayedMidfielders = this.centers(midfielders, this.state.state.formation.midfielders, i18n.t('dream_team.midfielder'))
        let displayedForwards = this.forwardsKeeper(forwards, this.state.state.formation.forwards, i18n.t('dream_team.forward'))

        let sumStars = 0
        sumStars += this.starsOfPlayers(displayedKeepers)
        sumStars += this.starsOfPlayers(displayedWingbacks)
        sumStars += this.starsOfPlayers(displayedDefs)
        sumStars += this.starsOfPlayers(displayedWings)
        sumStars += this.starsOfPlayers(displayedMidfielders)
        sumStars += this.starsOfPlayers(displayedForwards)

        return <div className="dream_team_page">
            <div className="stats_and_selectors_div">
                <div className="dream_team_stats">
                    <span className="dream_team_stats_total">{i18n.t('dream_team.total')}:</span> {ratingFormatter(sumStars)}
                </div>
                <nav className="selectors_nav">
                    <FormationSelector currentFormation={this.state.state.formation}
                        callback={this.formationChanged} />
                    <SeasonSelector currentSeason={this.state.dataRequest.season}
                        seasonOffset={this.props.levelDataProps.levelData.seasonOffset} 
                        seasons={this.props.levelDataProps.seasons()}
                        callback={this.seasonChanged} />
                    <StatsTypeSelector rounds={this.props.levelDataProps.rounds(this.state.dataRequest.season)}
                        statsTypes={[StatsTypeEnum.ROUND, StatsTypeEnum.ACCUMULATE]}
                        selectedStatType={this.state.dataRequest.statsType}
                        onChanged={this.statsTypeChanged}/>
                </nav>
            </div>
            <div className="core_team">
                <div className="core_team_column"></div>
                <div className="core_team_column">
                    {displayedKeepers.map(keeper => 
                        <DreamTeamPlayerCard dreamTeamPlayerPosition={keeper} 
                                             showTeamCountryFlag={this.props.showCountryFlags}
                                             key={'dream_team_player_' + keeper.player?.playerSortingKey.playerId} />
                    )}
                </div>
                <div className="core_team_column"></div>
                {/* def */}
                <div className="core_team_column">
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWingbacks[0]} showTeamCountryFlag={this.props.showCountryFlags} />
                </div>
                <div className="core_team_column">
                    {displayedDefs.map(def => 
                        <DreamTeamPlayerCard dreamTeamPlayerPosition={def} 
                                             showTeamCountryFlag={this.props.showCountryFlags} 
                                             key={'dream_team_player_' + def.player?.playerSortingKey.playerId}/>
                    )}
                </div>
                <div className="core_team_column">
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWingbacks[1]} showTeamCountryFlag={this.props.showCountryFlags} />
                </div>
                {/* mid */}
                <div className="core_team_column">
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWings[0]} showTeamCountryFlag={this.props.showCountryFlags} />
                </div>
                <div className="core_team_column">
                    {displayedMidfielders.map(midfielder => 
                        <DreamTeamPlayerCard dreamTeamPlayerPosition={midfielder} 
                                             showTeamCountryFlag={this.props.showCountryFlags} 
                                             key={'dream_team_player_' + midfielder.player?.playerSortingKey.playerId} />
                    )}
                </div>
                <div className="core_team_column">
                <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWings[1]} showTeamCountryFlag={this.props.showCountryFlags} />
                </div>
                {/* attack */}
                <div className="core_team_column"></div>
                <div className="core_team_column">
                    {displayedForwards.map(forward => 
                        <DreamTeamPlayerCard dreamTeamPlayerPosition={forward} 
                                             showTeamCountryFlag={this.props.showCountryFlags} 
                                             key={'dream_team_player_' + forward.player?.playerSortingKey.playerId} />
                    )}
                </div>
                <div className="core_team_column"></div>
            </div>
            <div className="substitutions_separator">
                {i18n.t('dream_team.substitions')}
            </div>
            <div className="substitutions">
                <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: keepers[1], position: i18n.t('dream_team.keeper')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: wingbacks[displayedWingbacks.length], position: i18n.t('dream_team.wingback')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: defenders[displayedDefs.length], position: i18n.t('dream_team.defender')}}  showTeamCountryFlag={this.props.showCountryFlags}/>
                <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: midfielders[displayedMidfielders.length], position: i18n.t('dream_team.midfielder')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: forwards[displayedForwards.length], position: i18n.t('dream_team.forward')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: wingers[displayedWings.length], position: i18n.t('dream_team.winger')}} showTeamCountryFlag={this.props.showCountryFlags}/>
            </div>
        </div>
    }
    
}

const DreamTeamPage = Section(DreamTeamPageBase, _ => 'menu.dream_team')
export default DreamTeamPage