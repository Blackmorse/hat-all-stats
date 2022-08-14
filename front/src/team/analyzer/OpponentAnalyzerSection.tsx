import React from 'react'
import {Col, Form, Row} from 'react-bootstrap'
import {useTranslation} from 'react-i18next'
import {LoadingEnum} from '../../common/enums/LoadingEnum'
import ExecutableComponent, {StateAndRequest} from '../../common/sections/HookExecutableComponent'
import Section from '../../common/sections/HookSection'
import Blur from '../../common/widgets/Blur'
import {combineMatches, opponentTeamMatches, teamAndOpponentMatches} from '../../rest/Client'
import MatchOpponentCombinedInfo from '../../rest/models/analyzer/MatchOpponentCombinedInfo'
import NearestMatch from '../../rest/models/match/NearestMatch'
import SingleMatch from '../../rest/models/match/SingleMatch'
import TeamMatchInfo from '../matches/TeamMatchInfo'
import TeamLevelDataProps from '../TeamLevelDataProps'
import MatchSelectorTable from './MatchSelectorTable'

interface Props {
    props: TeamLevelDataProps
}

type Team = [number, string]

interface OriginTeamAndMatches {
    team: Team,
    matches: Array<NearestMatch>
}

interface LoadingState<Data> {
    loadingEnum: LoadingEnum,
    data?: Data
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

function showStateElement<Data>(loadingState: LoadingState<Data>, 
        dataElement: (data: Data) => JSX.Element,
        updateOnError: () => void): JSX.Element | undefined {
    return <>
        <Blur loadingState={loadingState.loadingEnum}
                updateCallback={updateOnError} />
        {loadingState.data === undefined ? undefined : dataElement(loadingState.data)}
        </>
}

const OpponentAnalyzerSection = (props: Props) => {
    const i18n = useTranslation().i18n

    const updateSelectedOriginMatch = (matchId: number, state: State, setState: (st: State) => void) => {
        let selectedNextOpponent = state.selectedNextOpponent
        if (selectedNextOpponent !== undefined && state.selectedOpponentMatchId !== undefined) {
            let newState: State = {
                ...state,
                selectedOriginMatchId: matchId,
                simulatedMatch: {loadingEnum: LoadingEnum.LOADING}
            }
            setState(newState)

            combineMatches(props.props.teamId(), matchId, selectedNextOpponent[0], state.selectedOpponentMatchId,
                (loadingEnum, result) => {

                    let newState: State = {
                        ...state,
                        selectedOriginMatchId: matchId,
                        simulatedMatch: {
                            data: result,
                            loadingEnum: loadingEnum
                        }
                    }
                    setState(newState)
                })
        } else {
            let newState: State = {
                ...state,
                selectedOriginMatchId: matchId
            }
            setState(newState)
        }
    }

    const updateSelectedOpponentMatch = (matchId: number, state: State, setState: (st: State) => void) => {      
        if(state.selectedOriginMatchId !== undefined && state.selectedNextOpponent !== undefined) {
            let newState: State = {
                ...state,
                selectedOpponentMatchId: matchId,
                simulatedMatch: {loadingEnum: LoadingEnum.LOADING},
            }
            setState(newState)
        
            combineMatches(props.props.teamId(), state.selectedOriginMatchId, state.selectedNextOpponent[0], matchId,
                (loadingEnum, result) => {
                    let newStateInner: State = {
                        ...newState,
                        simulatedMatch: {
                            loadingEnum: loadingEnum,
                            data: result
                        }
                    }
                    setState(newStateInner)
                })
        } else {
            let newState: State = {
                ...state,
                selectedOpponentMatchId: matchId
            }
            setState(newState)
        }
    }

    const opponentChanged = (event: React.FormEvent<HTMLSelectElement> | number, state: State, setState: (st: State) => void) => {
        let teamNumber = typeof(event) === 'number' ? event : Number(event.currentTarget.value)
        if (state.nextOpponents !== undefined) {
            let team = state.nextOpponents.find(team => team[0] === teamNumber) as Team  

            let newState: State  = {
                ...state,
                playedOpponentMatches: {loadingEnum: LoadingEnum.LOADING},
                selectedNextOpponent: team
            }
            setState(newState)

            opponentTeamMatches(teamNumber, (loadingEnum, matches) => {        
                let newState: State = {
                    ...state,
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
                setState(newState)
            })
        }
    }

    const content = (stateAndRequest: StateAndRequest<TeamId, State>) => {
        let originTeamAndMatches = stateAndRequest.currentState.originTeamAndMatches
        let originMatchesTable: JSX.Element | undefined = undefined
        if (originTeamAndMatches !== undefined && originTeamAndMatches.matches.length > 0 && stateAndRequest.currentState.selectedOriginMatchId !== undefined) {
            originMatchesTable = <MatchSelectorTable matches={originTeamAndMatches.matches} 
                selectedMatchId={stateAndRequest.currentState.selectedOriginMatchId}
                selectedTeamId={props.props.teamId()}
                callback={matchId => updateSelectedOriginMatch(matchId, stateAndRequest.currentState, stateAndRequest.setState)}/>
        }
        
        let opponentMatchesTable: JSX.Element | undefined = undefined
        if (stateAndRequest.currentState.selectedNextOpponent !== undefined) { 

            opponentMatchesTable = showStateElement(stateAndRequest.currentState.playedOpponentMatches,    
                (data) => 
                    <MatchSelectorTable matches={data} 
                        selectedMatchId={stateAndRequest.currentState.selectedOpponentMatchId}
                        selectedTeamId={stateAndRequest.currentState.selectedNextOpponent![0]}
                        callback={matchId => updateSelectedOpponentMatch(matchId, stateAndRequest.currentState, stateAndRequest.setState)} />,
                () => opponentChanged(stateAndRequest.currentState.selectedNextOpponent![0], stateAndRequest.currentState, stateAndRequest.setState))
        }

        let simulatedMatchElement = showStateElement(stateAndRequest.currentState.simulatedMatch, 
            (singleMatch) => <Section element={<TeamMatchInfo singleMatch={singleMatch}/>} title={i18n.t('team.simulate_match')}/>,
            () => updateSelectedOpponentMatch(stateAndRequest.currentState.selectedOpponentMatchId!, stateAndRequest.currentState, stateAndRequest.setState))

        return  <>
        <Row>
            <Col className='d-flex flex-column align-items-center'>
                <div className='mb-1'>
                    {props.props.teamName()}
                </div>
                {originMatchesTable}
            </Col>
            <Col className='d-flex flex-column align-items-center'>
                <div className='mb-1'>
                    <Form.Select size='sm' defaultValue={stateAndRequest.currentState.selectedNextOpponent?.[0]} onChange={event => opponentChanged(event, stateAndRequest.currentState, stateAndRequest.setState)}>
                        {stateAndRequest.currentState.nextOpponents?.map(team =>
                            <option value={team[0]}>{team[1]}</option>)}
                    </Form.Select>
                </div> 
                
                {opponentMatchesTable}
            </Col>
            
        </Row>
        {simulatedMatchElement}
        </>

    }

    const initialState = {
            collapsed: false,
            loadingState: LoadingEnum.OK,
            dataRequest: props.props.teamId(),
            simulatedMatch: {loadingEnum: LoadingEnum.LOADING},
            playedOpponentMatches: {loadingEnum: LoadingEnum.LOADING}         
        }

    const stateFromResult = (result?: MatchOpponentCombinedInfo, currentState?: State) => {
        if (result === undefined) {
            return currentState === undefined ? initialState : currentState
        }

        let newState: State = {
            originTeamAndMatches: {
                team: [props.props.teamId(), props.props.teamName()],
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
            }
        }

        return newState
    }

    return <ExecutableComponent<TeamId, MatchOpponentCombinedInfo, State> 
        initialRequest={props.props.teamId()}
        responseToState={stateFromResult}
        content={content}
        executeRequest={(request, callback) => {
            teamAndOpponentMatches(request, callback) 
        }}
        sectionTitle={() => i18n.t('')}
    />
}

export default OpponentAnalyzerSection
