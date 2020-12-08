import React from 'react'
import LevelData from '../../rest/models/leveldata/LevelData';
import LevelDataProps, { LevelDataPropsWrapper } from '../LevelDataProps';
import StatisticsSection from '../sections/StatisticsSection';
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

interface State {
    loadingState: LoadingEnum,
    dreamTeamPlayers?: Array<DreamTeamPlayer>,
    statsType: StatsType,
    season: number,
    formation: Formation
}

class DreamTeamPage<Data extends LevelData, Props extends LevelDataProps<Data>> 
    extends StatisticsSection<LevelDataPropsWrapper<Data, LevelDataProps<Data>>, State> {
    
    constructor(props: LevelDataPropsWrapper<Data, Props>) {
        super(props, 'menu.dream_team')
        this.state = {
            loadingState: LoadingEnum.OK,
            statsType: {
                statType: StatsTypeEnum.ROUND,
                roundNumber: props.levelDataProps.currentRound(),
            },
            season: props.levelDataProps.currentSeason(),
            formation: new Formation(4, 4, 2)
        }
        this.statsTypeChanged=this.statsTypeChanged.bind(this)
        this.seasonChanged=this.seasonChanged.bind(this)
        this.formationChanged=this.formationChanged.bind(this) 
    }
    
    componentDidMount() {
        this.updateCurrent()
    }

    updateCurrent(): void {
        this.update(this.state.statsType, this.state.formation, this.state.season)
    }

    update(statsType: StatsType, formation: Formation, season: number) {
        this.setState({
            loadingState: LoadingEnum.LOADING,
            dreamTeamPlayers: this.state.dreamTeamPlayers
        })

        getDreamTeam(this.props.levelDataProps.createLevelRequest(), season,
            statsType, "rating", (players) => this.setState({
                loadingState: LoadingEnum.OK,
                dreamTeamPlayers: players,
                formation: formation,
                statsType: statsType,
                season: season
            }),
            () => this.setState({
                loadingState: LoadingEnum.ERROR,
                dreamTeamPlayers: this.state.dreamTeamPlayers
            }))
    }

    statsTypeChanged(statType: StatsType) {
        this.update(statType, this.state.formation, this.state.season)
    }

    seasonChanged(season: number) {
        let rounds = this.props.levelDataProps.rounds(season)
        this.update({statType: StatsTypeEnum.ROUND, roundNumber: rounds[rounds.length - 1]}, 
            this.state.formation, season)
    }

    formationChanged(formation: Formation) {
        this.setState({
            formation: formation
        })
    }


    private pushPlayer(array: Array<JSX.Element>, index: number, 
            players: Array<DreamTeamPlayer>, position: string) {
        array.push((players.length > index) ? <DreamTeamPlayerCard player={players[index]} position={position}/> : 
            <DreamTeamPlayerCard position={position}/>)
    }

    private wings(dreamTeamPlayers: Array<DreamTeamPlayer>, linePositions: number, position: string): Array<JSX.Element> {
        let result: Array<JSX.Element> = []
        if (linePositions === 2) {
            return []
        } else {
            this.pushPlayer(result, 0, dreamTeamPlayers, position)
            this.pushPlayer(result, 1, dreamTeamPlayers, position)     
            return result;       
        }
    }

    private centers(dreamTeamPlayers: Array<DreamTeamPlayer>, linePositions: number, position: string): Array<JSX.Element> {
        let result: Array<JSX.Element> = []
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

    private forwardsKeeper(dreamTeamPlayers: Array<DreamTeamPlayer>, linePositions: number, position: string): Array<JSX.Element> {
        let result: Array<JSX.Element> = []
        Array.from(Array(linePositions).keys()).forEach(i => {
            this.pushPlayer(result, i, dreamTeamPlayers, position)
        })
        return result
    }
    
    renderSection(): JSX.Element {
        if (this.state.dreamTeamPlayers === undefined) {
            return <></>
        }
        console.log(this.state.statsType)
        let keepers = this.state.dreamTeamPlayers.filter(player => player.role === 'keeper')
        let defenders = this.state.dreamTeamPlayers.filter(player => player.role === 'defender')
        let wingbacks = this.state.dreamTeamPlayers.filter(player => player.role === 'wingback')
        let midfielders = this.state.dreamTeamPlayers.filter(player => player.role === 'midfielder')
        let wingers = this.state.dreamTeamPlayers.filter(player => player.role === 'winger')
        let forwards = this.state.dreamTeamPlayers.filter(player => player.role === 'forward')

        let jsxWingbacks = this.wings(wingbacks, this.state.formation.defenders, i18n.t('dream_team.wingback'))
        let jsxDefs = this.centers(defenders, this.state.formation.defenders, i18n.t('dream_team.defender'))
        let jsxWings = this.wings(wingers, this.state.formation.midfielders, i18n.t('dream_team.winger'))
        let jsxMidfielders = this.centers(midfielders, this.state.formation.midfielders, i18n.t('dream_team.midfielder'))
        let jsxForwards = this.forwardsKeeper(forwards, this.state.formation.forwards, i18n.t('dream_team.forward'))

        return <div className="dream_team_page">
            <div className="selectors_div">
                <FormationSelector currentFormation={this.state.formation}
                    callback={this.formationChanged} />
                <SeasonSelector currentSeason={this.state.season}
                    seasonOffset={this.props.levelDataProps.levelData.seasonOffset} 
                    seasons={this.props.levelDataProps.seasons()}
                    callback={this.seasonChanged} />
                <StatsTypeSelector rounds={this.props.levelDataProps.rounds(this.state.season)}
                    statsTypes={[StatsTypeEnum.ROUND, StatsTypeEnum.ACCUMULATE]}
                    selectedStatType={this.state.statsType}
                    onChanged={this.statsTypeChanged}/>
            </div>
            <div className="core_team">
                <div className="core_team_column"></div>
                <div className="core_team_column">
                    {this.forwardsKeeper(keepers, 1, i18n.t('dream_team.keeper'))}
                </div>
                <div className="core_team_column"></div>
                {/* def */}
                <div className="core_team_column">
                    {jsxWingbacks[0]}
                </div>
                <div className="core_team_column">
                    {jsxDefs}
                </div>
                <div className="core_team_column">
                    {jsxWingbacks[1]}
                </div>
                {/* mid */}
                <div className="core_team_column">
                    {jsxWings[0]}
                </div>
                <div className="core_team_column">
                    {jsxMidfielders}
                </div>
                <div className="core_team_column">
                    {jsxWings[1]}
                </div>
                {/* attack */}
                <div className="core_team_column"></div>
                <div className="core_team_column">
                    {jsxForwards}
                </div>
                <div className="core_team_column"></div>
            </div>
            <div className="substitutions_separator">
                {i18n.t('dream_team.substitions')}
            </div>
            <div className="substitutions">
                <DreamTeamPlayerCard player={keepers[1]} position={i18n.t('dream_team.keeper')}/>
                <DreamTeamPlayerCard player={wingbacks[jsxWingbacks.length]} position={i18n.t('dream_team.wingback')}/>
                <DreamTeamPlayerCard player={defenders[jsxDefs.length]} position={i18n.t('dream_team.defender')} />
                <DreamTeamPlayerCard player={midfielders[jsxMidfielders.length]} position={i18n.t('dream_team.midfielder')}/>
                <DreamTeamPlayerCard player={forwards[jsxForwards.length]} position={i18n.t('dream_team.forward')}/>
                <DreamTeamPlayerCard player={wingers[jsxWings.length]} position={i18n.t('dream_team.winger')}/>
            </div>
        </div>
    }
    
}

export default DreamTeamPage