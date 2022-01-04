import React from 'react'
import LevelData from '../../rest/models/leveldata/LevelData';
import LevelDataProps, { LevelDataPropsWrapper } from '../LevelDataProps';
import { LoadingEnum } from '../enums/LoadingEnum';
import DreamTeamPlayer from '../../rest/models/player/DreamTeamPlayer';
import { getDreamTeam } from '../../rest/Client'
import { StatsType, StatsTypeEnum } from '../../rest/models/StatisticsParameters';
import DreamTeamPlayerCard from './dreamteam/DreamTeamPlayerCard'
import '../../i18n'
import i18n from '../../i18n';
import StatsTypeSelector from '../selectors/StatsTypeSelector'
import SeasonSelector from '../selectors/SeasonSelector';
import FormationSelector, { Formation } from '../selectors/FormationSelector'
import { ratingFormatter } from '../Formatters'
import ExecutableComponent from '../sections/ExecutableComponent';
import Section, { SectionState } from '../sections/Section';
import { Col, Container, Row } from 'react-bootstrap';

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
    extends ExecutableComponent<LevelDataPropsWrapper<Data, LevelDataProps<Data>>, State & SectionState, Array<DreamTeamPlayer>, Request> {

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
            formation: new Formation(4, 4, 2),
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

    stateFromResult(result?: Array<DreamTeamPlayer>): State & SectionState {
        return {
            dreamTeamPlayers: (result) ? result : this.state.dreamTeamPlayers,
            formation: this.state.formation,
            collapsed: this.state.collapsed
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
        this.setState({
            ...this.state,
            formation: formation
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
        if (this.state.dreamTeamPlayers === undefined) {
            return <></>
        }

        let keepers = this.state.dreamTeamPlayers.filter(player => player.role === 'keeper')
        let defenders = this.state.dreamTeamPlayers.filter(player => player.role === 'defender')
        let wingbacks = this.state.dreamTeamPlayers.filter(player => player.role === 'wingback')
        let midfielders = this.state.dreamTeamPlayers.filter(player => player.role === 'midfielder')
        let wingers = this.state.dreamTeamPlayers.filter(player => player.role === 'winger')
        let forwards = this.state.dreamTeamPlayers.filter(player => player.role === 'forward')

        let displayedKeepers = this.forwardsKeeper(keepers, 1, i18n.t('dream_team.keeper'))
        let displayedWingbacks = this.wings(wingbacks, this.state.formation.defenders, i18n.t('dream_team.wingback'))
        let displayedDefs = this.centers(defenders, this.state.formation.defenders, i18n.t('dream_team.defender'))
        let displayedWings = this.wings(wingers, this.state.formation.midfielders, i18n.t('dream_team.winger'))
        let displayedMidfielders = this.centers(midfielders, this.state.formation.midfielders, i18n.t('dream_team.midfielder'))
        let displayedForwards = this.forwardsKeeper(forwards, this.state.formation.forwards, i18n.t('dream_team.forward'))

        let sumStars = 0
        sumStars += this.starsOfPlayers(displayedKeepers)
        sumStars += this.starsOfPlayers(displayedWingbacks)
        sumStars += this.starsOfPlayers(displayedDefs)
        sumStars += this.starsOfPlayers(displayedWings)
        sumStars += this.starsOfPlayers(displayedMidfielders)
        sumStars += this.starsOfPlayers(displayedForwards)

        return <div>
            <Row>
                <Col lg={4} className="d-flex flex-row mb-2">
                    <span className="me-2">{i18n.t('dream_team.total')}:</span> {ratingFormatter(sumStars)}
                </Col>
                <Col lg={8} md={10} sm={12} className='d-flex flex-row'>
                    <div className='ms-auto d-flex flex-column flex-xs-column flex-sm-column flex-md-row flex-lg-row'><FormationSelector currentFormation={this.state.formation}
                        callback={this.formationChanged} />
                    <SeasonSelector currentSeason={this.state.dataRequest.season}
                        seasonOffset={this.props.levelDataProps.levelData.seasonOffset} 
                        seasons={this.props.levelDataProps.seasons()}
                        callback={this.seasonChanged} />
                    <StatsTypeSelector rounds={this.props.levelDataProps.rounds(this.state.dataRequest.season)}
                        statsTypes={[StatsTypeEnum.ROUND, StatsTypeEnum.ACCUMULATE]}
                        selectedStatType={this.state.dataRequest.statsType}
                        onChanged={this.statsTypeChanged}/>
                        </div>
                </Col>
            </Row>
            <Row className='justify-content-around my-3'>
                <Col lg={2} className=""></Col>
                    {displayedKeepers.map(keeper => <Col lg={2}>
                        <DreamTeamPlayerCard dreamTeamPlayerPosition={keeper} 
                                             showTeamCountryFlag={this.props.showCountryFlags}
                                             key={'dream_team_player_' + keeper.player?.playerSortingKey.playerId} />
                                        </Col>
                    )}
                <Col lg={2}></Col>
            </Row>
            {/* defenders */}
            <Row className='justify-content-around align-items-stretch my-3'>
                <Col className='mx-0' lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWingbacks[0]} showTeamCountryFlag={this.props.showCountryFlags} />
                </Col>
                    {displayedDefs.map(def => <Col lg={2} className='mx-0'>
                        <DreamTeamPlayerCard dreamTeamPlayerPosition={def} 
                                             showTeamCountryFlag={this.props.showCountryFlags} 
                                             key={'dream_team_player_' + def.player?.playerSortingKey.playerId}/>
                                             </Col>
                    )}
                <Col lg={2}  className='mx-0'>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWingbacks[1]} showTeamCountryFlag={this.props.showCountryFlags} />
                </Col>
            </Row>
            {/* mid */}
            <Row className='justify-content-around align-items-stretch my-3'>
                <Col className='mx-0' lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWings[0]} showTeamCountryFlag={this.props.showCountryFlags} />
                </Col>
                
                    {displayedMidfielders.map(midfielder => <Col className='mx-0' lg={2}  >
                        <DreamTeamPlayerCard dreamTeamPlayerPosition={midfielder} 
                                             showTeamCountryFlag={this.props.showCountryFlags} 
                                             key={'dream_team_player_' + midfielder.player?.playerSortingKey.playerId} />
                                </Col>
                    )}
                
                <Col className='mx-0' lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={displayedWings[1]} showTeamCountryFlag={this.props.showCountryFlags} />
                </Col>
            </Row>
                {/* attack */}
            <Row className='justify-content-around align-items-stretch my-3'>
                <Col className='mx-0' lg={2}></Col>
                {displayedForwards.map(forward => <Col className='mx-0' lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={forward} 
                                            showTeamCountryFlag={this.props.showCountryFlags} 
                                            key={'dream_team_player_' + forward.player?.playerSortingKey.playerId} />
                            </Col>
                )}
                <Col className='mx-0' lg={2}></Col>
            </Row>
            <Container fluid className="shadow-sm my-3 text-center bg-light border">
                {i18n.t('dream_team.substitions')}
            </Container>
            <Row>
                <Col lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: keepers[1], position: i18n.t('dream_team.keeper')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                </Col>
                <Col lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: wingbacks[displayedWingbacks.length], position: i18n.t('dream_team.wingback')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                </Col>
                <Col lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: defenders[displayedDefs.length], position: i18n.t('dream_team.defender')}}  showTeamCountryFlag={this.props.showCountryFlags}/>
                </Col>
                <Col lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: midfielders[displayedMidfielders.length], position: i18n.t('dream_team.midfielder')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                </Col>
                <Col lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: forwards[displayedForwards.length], position: i18n.t('dream_team.forward')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                </Col>
                <Col lg={2}>
                    <DreamTeamPlayerCard dreamTeamPlayerPosition={{player: wingers[displayedWings.length], position: i18n.t('dream_team.winger')}} showTeamCountryFlag={this.props.showCountryFlags}/>
                </Col>
            </Row>
        </div>
    }
    
}

const DreamTeamPage = Section(DreamTeamPageBase, _ => 'menu.dream_team')
export default DreamTeamPage