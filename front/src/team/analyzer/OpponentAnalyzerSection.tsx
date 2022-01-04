import React from 'react'
import { LoadingEnum } from '../../common/enums/LoadingEnum'
import ExecutableComponent, { LoadableState } from '../../common/sections/ExecutableComponent'
import MatchOpponentCombinedInfo from '../../rest/models/analyzer/MatchOpponentCombinedInfo'
import { teamAndOpponentMatches, combineMatches, opponentTeamMatches } from '../../rest/Client'
import TeamLevelDataProps from '../TeamLevelDataProps'
import '../../common/tables/TableSection.css'
import Section, { SectionState } from '../../common/sections/Section'
import MatchSelectorTable from './MatchSelectorTable'
import NearestMatch from '../../rest/models/match/NearestMatch'
import SingleMatch from '../../rest/models/match/SingleMatch'
import TeamMatchInfo from '../matches/TeamMatchInfo'
import Blur from '../../common/widgets/Blur'
import { Col, Form, Row } from 'react-bootstrap'

interface Props {
    props: TeamLevelDataProps
}

type Team = [number, string]

interface OriginTeamAndMatches {
    team: Team,
    matches: Array<NearestMatch>
}

interface State {
    originTeamAndMatches?: OriginTeamAndMatches,
    selectedOriginMatchId?: number,
    nextOpponents?: Array<Team>,
    selectedNextOpponent?: Team,
    playedOpponentMatches: LoadingState<Array<NearestMatch>>,
    selectedOpponentMatchId?: number,
    simulatedMatch: LoadingState<SingleMatch>
}

type TeamId = number

interface LoadingState<Data> {
    loadingEnum: LoadingEnum,
    data?: Data
}

class OpponentAnalyzerSectionBase extends ExecutableComponent<Props, State & SectionState, MatchOpponentCombinedInfo, TeamId> {
    constructor(props: Props) {
        super(props)
        this.state = {
            collapsed: false,
            loadingState: LoadingEnum.OK,
            dataRequest: props.props.teamId(),
            simulatedMatch: {loadingEnum: LoadingEnum.LOADING},
            playedOpponentMatches: {loadingEnum: LoadingEnum.LOADING}         
        }

        this.updateSelectedOriginMatch = this.updateSelectedOriginMatch.bind(this)
        this.updateSelectedOpponentMatch = this.updateSelectedOpponentMatch.bind(this)
        this.opponentChanged = this.opponentChanged.bind(this)
    }

    executeDataRequest(dataRequest: number, callback: (loadingState: LoadingEnum, result?: MatchOpponentCombinedInfo) => void): void {
        teamAndOpponentMatches(dataRequest, callback)
    }

    stateFromResult(result?: MatchOpponentCombinedInfo): State & SectionState {
        if (result === undefined) {
            return this.state
        }
        return {
            originTeamAndMatches: {
                team: [this.props.props.teamId(), this.props.props.levelData.teamName],
                matches: result.currentTeamPlayedMatches
            },
            selectedOriginMatchId: result.currentTeamPlayedMatches.length === 0 ? undefined : result.currentTeamPlayedMatches[result.currentTeamPlayedMatches.length - 1].matchId,
            nextOpponents: result.currentTeamNextOpponents,
            selectedNextOpponent: result.currentTeamNextOpponents.length === 0 ? undefined : result.currentTeamNextOpponents[0],
            playedOpponentMatches: {
                loadingEnum: LoadingEnum.OK,
                data: result.opponentPlayedMatches,
            },
            selectedOpponentMatchId: result.opponentPlayedMatches.length === 0 ? undefined : result.opponentPlayedMatches[result.opponentPlayedMatches.length - 1].matchId,
            simulatedMatch: {
                loadingEnum: LoadingEnum.OK,
                data: result.simulatedMatch
            },
            collapsed: this.state.collapsed
        }
    }

    static showStateElement<Data>(loadingState: LoadingState<Data>, 
            dataElement: (data: Data) => JSX.Element,
            updateOnError: () => void): JSX.Element | undefined {
        return <>
            <Blur loadingState={loadingState.loadingEnum}
                    updateCallback={updateOnError} />
            {loadingState.data === undefined ? undefined : dataElement(loadingState.data)}
            </>
    }

    updateSelectedOriginMatch(matchId: number) {
        let selectedNextOpponent = this.state.selectedNextOpponent
        if (selectedNextOpponent !== undefined && this.state.selectedOpponentMatchId !== undefined) {
            let newState: LoadableState<TeamId> & State & SectionState = {
                ...this.state,
                selectedOriginMatchId: matchId,
                simulatedMatch: {loadingEnum: LoadingEnum.LOADING}
            }
            this.setState(newState)

            combineMatches(this.props.props.teamId(), matchId, selectedNextOpponent[0], this.state.selectedOpponentMatchId,
                (loadingEnum, result) => {

                    let newState: LoadableState<TeamId> & State & SectionState = {
                        ...this.state,
                        selectedOriginMatchId: matchId,
                        simulatedMatch: {
                            data: result,
                            loadingEnum: loadingEnum
                        }
                    }
                    this.setState(newState)
                })
        } else {
            let newState: LoadableState<TeamId> & State & SectionState = {
                ...this.state,
                selectedOriginMatchId: matchId
            }
            this.setState(newState)
        }
    }

    updateSelectedOpponentMatch(matchId: number) {      
        if(this.state.selectedOriginMatchId !== undefined && this.state.selectedNextOpponent !== undefined) {
            let newState: LoadableState<TeamId> & State & SectionState = {
                ...this.state,
                selectedOpponentMatchId: matchId,
                simulatedMatch: {loadingEnum: LoadingEnum.LOADING}
            }
            this.setState(newState)
        
            combineMatches(this.props.props.teamId(), this.state.selectedOriginMatchId, this.state.selectedNextOpponent[0], matchId,
                (loadingEnum, result) => {
                    let newState: LoadableState<TeamId> & State & SectionState = {
                        ...this.state,
                        simulatedMatch: {
                            loadingEnum: loadingEnum,
                            data: result
                        }
                    }
                    this.setState(newState)
                })
        } else {
            let newState: LoadableState<TeamId>& State & SectionState = {
                ...this.state,
                selectedOpponentMatchId: matchId
            }
            this.setState(newState)
        }
    }

    opponentChanged(event: React.FormEvent<HTMLSelectElement> | number) {
        let teamNumber = typeof(event) === 'number' ? event : Number(event.currentTarget.value)
        if (this.state.nextOpponents !== undefined) {
            let team = this.state.nextOpponents.find(team => team[0] === teamNumber) as Team  

            let newState: LoadableState<TeamId> & State & SectionState  = {
                ...this.state,
                playedOpponentMatches: {loadingEnum: LoadingEnum.LOADING},
                selectedNextOpponent: team
            }
            this.setState(newState)

            opponentTeamMatches(teamNumber, (loadingEnum, matches) => {        
                let newState: LoadableState<TeamId> & State & SectionState = {
                    ...this.state,
                    selectedNextOpponent: team,
                    playedOpponentMatches: {
                        loadingEnum: loadingEnum,
                        data: matches
                    },
                    selectedOpponentMatchId: undefined,
                    simulatedMatch: {
                        loadingEnum: LoadingEnum.OK
                    }
                }
                this.setState(newState)
            })
        }
    }

    renderSection(): JSX.Element {
        let originTeamAndMatches = this.state.originTeamAndMatches
        let originMatchesTable: JSX.Element | undefined = undefined
        if (originTeamAndMatches !== undefined && originTeamAndMatches.matches.length > 0 && this.state.selectedOriginMatchId !== undefined) {
            originMatchesTable = <MatchSelectorTable matches={originTeamAndMatches.matches} 
                selectedMatchId={this.state.selectedOriginMatchId}
                selectedTeamId={this.props.props.teamId()}
                callback={this.updateSelectedOriginMatch}/>
        }
        
        let opponentMatchesTable: JSX.Element | undefined = undefined
        if (this.state.selectedNextOpponent !== undefined) { 

            opponentMatchesTable = OpponentAnalyzerSection.showStateElement(this.state.playedOpponentMatches,    
                (data) => 
                    <MatchSelectorTable matches={data} 
                        selectedMatchId={this.state.selectedOpponentMatchId}
                        selectedTeamId={this.state.selectedNextOpponent![0]}
                        callback={this.updateSelectedOpponentMatch} />,
                () => this.opponentChanged(this.state.selectedNextOpponent![0]))
        }

        const TeamMatchInfoSection = Section(TeamMatchInfo, _ => 'team.simulate_match')

        let simulatedMatchElement = OpponentAnalyzerSection.showStateElement(this.state.simulatedMatch, 
            (singleMatch) => <TeamMatchInfoSection singleMatch={singleMatch}/>,
            () => this.updateSelectedOpponentMatch(this.state.selectedOpponentMatchId!))

        return  <>
        <Row>
            <Col className='d-flex flex-column align-items-center'>
                <div className='mb-1'>
                    {this.props.props.levelData.teamName}
                </div>
                {originMatchesTable}
            </Col>
            <Col className='d-flex flex-column align-items-center'>
                <div className='mb-1'>
                    <Form.Select size='sm' defaultValue={this.state.selectedNextOpponent?.[0]} onChange={this.opponentChanged}>
                        {this.state.nextOpponents?.map(team =>
                            <option value={team[0]}>{team[1]}</option>)}
                    </Form.Select>
                </div> 
                
                {opponentMatchesTable}
            </Col>
            
        </Row>
        {simulatedMatchElement}
        </>
    }
}

const OpponentAnalyzerSection = Section(OpponentAnalyzerSectionBase, _ => 'team.analyzer')

export default OpponentAnalyzerSection