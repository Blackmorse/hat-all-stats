import React from 'react'
import { LoadingEnum } from '../../common/enums/LoadingEnum'
import ExecutableComponent, { LoadableState } from '../../common/sections/ExecutableComponent'
import MatchOpponentCombinedInfo from '../../rest/models/analyzer/MatchOpponentCombinedInfo'
import { teamAndOpponentMatches, combineMatches, opponentTeamMatches } from '../../rest/Client'
import TeamLevelDataProps from '../TeamLevelDataProps'
import '../../common/sections/StatisticsSection.css'
import './OpponentAnalyzerSection.css'
import '../../common/tables/TableSection.css'
import Section, { SectionState } from '../../common/sections/Section'
import MatchSelectorTable from './MatchSelectorTable'
import NearestMatch from '../../rest/models/match/NearestMatch'
import SingleMatch from '../../rest/models/match/SingleMatch'
import TeamMatchInfo from '../matches/TeamMatchInfo'
import Blur from '../../common/widgets/Blur'

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

class OpponentAnalyzerSectionBase extends ExecutableComponent<Props, State, MatchOpponentCombinedInfo, TeamId, LoadableState<State, TeamId> & SectionState> {
    constructor(props: Props) {
        super(props)
        this.state = {
            collapsed: false,
            loadingState: LoadingEnum.OK,
            dataRequest: props.props.teamId(),
            state: {
                simulatedMatch: {loadingEnum: LoadingEnum.LOADING},
                playedOpponentMatches: {loadingEnum: LoadingEnum.LOADING}
            }
        }

        this.updateSelectedOriginMatch = this.updateSelectedOriginMatch.bind(this)
        this.updateSelectedOpponentMatch = this.updateSelectedOpponentMatch.bind(this)
        this.opponentChanged = this.opponentChanged.bind(this)
    }

    executeDataRequest(dataRequest: number, callback: (loadingState: LoadingEnum, result?: MatchOpponentCombinedInfo) => void): void {
        teamAndOpponentMatches(dataRequest, callback)
    }

    stateFromResult(result?: MatchOpponentCombinedInfo): State {
        if (result === undefined) {
            return this.state.state
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
            }
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
        let selectedNextOpponent = this.state.state.selectedNextOpponent
        if (selectedNextOpponent !== undefined && this.state.state.selectedOpponentMatchId !== undefined) {
            let newState: LoadableState<State, TeamId> & SectionState = {
                ...this.state,
                state: {
                    ...this.state.state,
                    selectedOriginMatchId: matchId,
                    simulatedMatch: {loadingEnum: LoadingEnum.LOADING}
                }
            }
            this.setState(newState)

            combineMatches(this.props.props.teamId(), matchId, selectedNextOpponent[0], this.state.state.selectedOpponentMatchId,
                (loadingEnum, result) => {

                    let newState: LoadableState<State, TeamId> & SectionState = {
                        ...this.state,
                        state: {
                            ...this.state.state,
                            selectedOriginMatchId: matchId,
                            simulatedMatch: {
                                data: result,
                                loadingEnum: loadingEnum
                            }
                        }
                    }
                    this.setState(newState)
                })
        } else {
            let newState: LoadableState<State, TeamId> & SectionState = {
                ...this.state,
                state: {
                    ...this.state.state,
                    selectedOriginMatchId: matchId
                }
            }
            this.setState(newState)
        }
    }

    updateSelectedOpponentMatch(matchId: number) {      
        if(this.state.state.selectedOriginMatchId !== undefined && this.state.state.selectedNextOpponent !== undefined) {
            let newState: LoadableState<State, TeamId> & SectionState = {
                ...this.state,
                state: {
                    ...this.state.state,
                    selectedOpponentMatchId: matchId,
                    simulatedMatch: {loadingEnum: LoadingEnum.LOADING}
                }
            }
            this.setState(newState)
        
            combineMatches(this.props.props.teamId(), this.state.state.selectedOriginMatchId, this.state.state.selectedNextOpponent[0], matchId,
                (loadingEnum, result) => {
                    let newState: LoadableState<State, TeamId> & SectionState = {
                        ...this.state,
                        state: {
                            ...this.state.state,
                            simulatedMatch: {
                                loadingEnum: loadingEnum,
                                data: result
                            }
                        }
                    }
                    this.setState(newState)
                })
        } else {
            let newState: LoadableState<State, TeamId> & SectionState = {
                ...this.state,
                state: {
                    ...this.state.state,
                    selectedOpponentMatchId: matchId
                }
            }
            this.setState(newState)
        }
    }

    opponentChanged(event: React.FormEvent<HTMLSelectElement> | number) {
        let teamNumber = typeof(event) === 'number' ? event : Number(event.currentTarget.value)
        if (this.state.state.nextOpponents !== undefined) {
            let team = this.state.state.nextOpponents.find(team => team[0] === teamNumber) as Team  

            let newState: LoadableState<State, TeamId> & SectionState  = {
                ...this.state,
                state: {
                    ...this.state.state,
                    playedOpponentMatches: {loadingEnum: LoadingEnum.LOADING},
                    selectedNextOpponent: team
                }
            }
            this.setState(newState)

            opponentTeamMatches(teamNumber, (loadingEnum, matches) => {        
                let newState: LoadableState<State, TeamId> & SectionState = {
                    ...this.state,
                    state: {
                        ...this.state.state,
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
                }
                this.setState(newState)
            })
        }
    }

    renderSection(): JSX.Element {
        let originTeamAndMatches = this.state.state.originTeamAndMatches
        let originMatchesTable: JSX.Element | undefined = undefined
        if (originTeamAndMatches !== undefined && originTeamAndMatches.matches.length > 0 && this.state.state.selectedOriginMatchId !== undefined) {
            originMatchesTable = <MatchSelectorTable matches={originTeamAndMatches.matches} 
                selectedMatchId={this.state.state.selectedOriginMatchId}
                selectedTeamId={this.props.props.teamId()}
                callback={this.updateSelectedOriginMatch}/>
        }
        
        let opponentMatchesTable: JSX.Element | undefined = undefined
        if (this.state.state.selectedNextOpponent !== undefined) { 

            opponentMatchesTable = OpponentAnalyzerSection.showStateElement(this.state.state.playedOpponentMatches,    
                (data) => 
                    <MatchSelectorTable matches={data} 
                        selectedMatchId={this.state.state.selectedOpponentMatchId}
                        selectedTeamId={this.state.state.selectedNextOpponent![0]}
                        callback={this.updateSelectedOpponentMatch} />,
                () => this.opponentChanged(this.state.state.selectedNextOpponent![0]))
        }

        const TeamMatchInfoSection = Section(TeamMatchInfo, _ => 'team.simulate_match')

        let simulatedMatchElement = OpponentAnalyzerSection.showStateElement(this.state.state.simulatedMatch, 
            (singleMatch) => <TeamMatchInfoSection singleMatch={singleMatch}/>,
            () => this.updateSelectedOpponentMatch(this.state.state.selectedOpponentMatchId!))

        return  <>
        <div className="opponent_analyzer_section">
            <div className="section_row_half_element analyze_side_secton">
                <div className="analyzer_opponent_name">
                    {this.props.props.levelData.teamName}
                </div>
                {originMatchesTable}
            </div>
            <div className="section_row_half_element analyze_side_secton">
            <div className="analyzer_opponent_name">
                <select defaultValue={this.state.state.selectedNextOpponent?.[0]} onChange={this.opponentChanged}>
                    {this.state.state.nextOpponents?.map(team =>
                        <option value={team[0]}>{team[1]}</option>)}
                </select>
            </div> 
                
                {opponentMatchesTable}
            </div>
            
        </div>
        {simulatedMatchElement}
        </>
    }
}

const OpponentAnalyzerSection = Section(OpponentAnalyzerSectionBase, _ => 'team.analyzer')

export default OpponentAnalyzerSection