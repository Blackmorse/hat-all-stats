import React, { type JSX } from 'react';
import './SeasonRoundSelector.css'
import '../../i18n'
import { Translation } from 'react-i18next'

interface Props {    
    season: number,
    offsettedSeason: number,
    round: number,
    seasonRoundInfo: Array<[number, Array<number>]>,
    callback: (season: number, round: number) => void
}

class SeasonRoundSelector extends React.Component<Props> {
        
    previousRound(): [number, number] | undefined {
        const map = new Map(this.props.seasonRoundInfo)
        const rounds = map.get(this.props.season)

        if(rounds && rounds.indexOf(this.props.round - 1) > -1) {
            return [this.props.season, this.props.round - 1]
        } else {
            const previousRounds = map.get(this.props.season - 1)
            if(previousRounds && previousRounds.indexOf(14) > -1) {
                return [this.props.season - 1, 14]
            } else {
                return undefined
            }
        }
    }

    nextRound(): [number, number] | undefined {
        const map = new Map(this.props.seasonRoundInfo)
        const rounds = map.get(this.props.season)

        if(rounds && rounds.indexOf(this.props.round + 1) > -1) {
            return [this.props.season, this.props.round + 1]
        } else {
            const nextRounds = map.get(this.props.season + 1)
            if(nextRounds && nextRounds.indexOf(1) > -1) {
                return [this.props.season + 1, 1]
            } else {
                return undefined
            }
        }
    }

    render() {
        let leftLink: JSX.Element
        const previousRound = this.previousRound()
        if(previousRound) {
            const previousRoundDefined = previousRound
            leftLink = <span className="season_round_selector_link" 
                onClick={() => this.props.callback(previousRoundDefined[0], previousRoundDefined[1])}>
                ◀
            </span>
        } else {
            leftLink = <span className="season_round_selector_link disabled">◀</span>
        }

        let rightLink: JSX.Element
        const nextRound = this.nextRound()
        if(nextRound) {
            const nextRoundDefined = nextRound
            rightLink = <span className="season_round_selector_link" 
                onClick={() => this.props.callback(nextRoundDefined[0], nextRoundDefined[1])}>
            ▶
        </span>
        } else {
            rightLink = <span className="season_round_selector_link disabled">▶</span>
        }

        return <Translation>
        {(t) => <nav className="season_round_selector">
            {leftLink}
            <span className="season_round_selector_center">
                <span className="srselector_info">{t('filter.season')}: {this.props.offsettedSeason}</span>
                <span className="srselector_info">{t('filter.round')} {this.props.round}</span>
            </span>
            {rightLink}
        </nav>
        }
        </Translation>
    }
}

export default SeasonRoundSelector
