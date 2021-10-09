import React from 'react'
import Section from '../../common/sections/Section';
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
            (loadingEnum, result) => {
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
        let TeamMatchInfoSection = Section(TeamMatchInfo)

        return <>
        <table className="statistics_table match_selector" >
        <tbody>
        {this.props.matches.map((match) => 
            <tr className={match.matchId === this.props.selectedMatchId ? "match_selected" : undefined} 
                    onClick={() => this.onClick(match.matchId)} >
                <td className={match.homeTeamId === this.props.selectedTeamId ? "value selected_team" : "value"}>{match.homeTeamName}</td>
                <td className="value">{match.homeGoals} : {match.awayGoals}</td>
                <td className={match.awayTeamId === this.props.selectedTeamId ? "value selected_team" : "value"}>{match.awayTeamName}</td>
                <td style={{position: 'relative'}}>
                    <img className="info_icon" src='/info.svg' alt='info'
                        onMouseOver={() => this.onMouseIn(match.matchId)}
                        onMouseOut={this.onMouseOut}
                    />
                    {match.matchId === this.state.expandedMatch?.matchId ? 
                        <div className="team_match_info_tooltip">
                            <TeamMatchInfoSection singleMatch={this.state.expandedMatch} hideSimulator/> 
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