import React from 'react'
import Section from '../../common/sections/HookSection';
import { getSingleMatch } from '../../rest/Client';
import NearestMatch from '../../rest/models/match/NearestMatch';
import SingleMatch from '../../rest/models/match/SingleMatch';
import TeamMatchInfo from '../matches/TeamMatchInfo';
import './MatchSelectorTable.css'

interface Props {
    matches: Array<NearestMatch>,
    selectedMatchId?: number,
    selectedTeamId: number,
    callback: (matchId: number) => void
}

interface State {
    matchesCache: Array<SingleMatch>,
    expandedMatch?: SingleMatch
}

class MatchSelectorTable extends React.Component<Props, State> {
    
    constructor(props: Props) {
        super(props)
        this.state = {
            matchesCache: []
        }
        this.onMouseIn = this.onMouseIn.bind(this)
        this.onMouseOut = this.onMouseOut.bind(this)
    }

    onClick(matchId: number) {
        this.props.callback(matchId)
    }

    onMouseIn(matchId: number) {
        if(this.state.matchesCache.map(match => match.matchId).includes(matchId)) {
            this.setState({
                matchesCache: this.state.matchesCache,
                expandedMatch: this.state.matchesCache.find(match => match.matchId === matchId)
            })
            return
        }


        getSingleMatch(matchId, 
            (_loadingEnum, result) => {
                let cache = [...this.state.matchesCache]
                if (result !== undefined) {
                    cache.push(result)
                }
                this.setState({
                    matchesCache: cache,
                    expandedMatch: result
                })
            })
        
    }

    onMouseOut() {
        this.setState({
            matchesCache: this.state.matchesCache,
            expandedMatch: undefined
        })
    }
    
    render() {
        if (this.props.matches.length === 0) {
            return <></>
        }

        return <>
        <table className='table table-striped table-rounded table-sm small text-center w-75 border table-hover' >
        <tbody>
            <tr></tr>
        {this.props.matches.map((match) => 
            <tr className={match.matchId === this.props.selectedMatchId ? "match_selected" : undefined} 
                    onClick={() => this.onClick(match.matchId)} >
                <td className={match.homeTeamId === this.props.selectedTeamId ? "text-success" : ""}>{match.homeTeamName}</td>
                <td className="">{match.homeGoals} : {match.awayGoals}</td>
                <td className={match.awayTeamId === this.props.selectedTeamId ? "text-success" : ""}>{match.awayTeamName}</td>
                <td style={{position: 'relative'}}>
                    <i className="bi bi-info-circle"
                        onMouseOver={() => this.onMouseIn(match.matchId)}
                        onMouseOut={this.onMouseOut}
                    ></i>
                    {match.matchId === this.state.expandedMatch?.matchId ? 
                        <div className="team_match_info_tooltip">
                            <Section element={<TeamMatchInfo singleMatch={this.state.expandedMatch} hideSimulator/>} title={''} /> 
                        </div>
                        : <></>}
                </td>
            </tr>
        )}
        </tbody>
    </table>

    </>
    }
}

export default MatchSelectorTable
