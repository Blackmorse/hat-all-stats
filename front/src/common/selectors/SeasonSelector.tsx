import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import './Selector.css'

interface SeasonSelectorProps {
    currentSeason: number,
    seasonOffset: number
    seasons: Array<number>,
    callback: (season: number) => void
}

class SeasonSelector extends React.Component<SeasonSelectorProps> {
    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        let season = Number(event.currentTarget.value)
        this.props.callback(season)
    }

    render() {
        return <Translation>
            { (t, { i18n }) =>
                <div className="selector_div">
                    <span className="selector_div_entry">{t('filter.season')}</span>
                    <select className="selector_div_entry" defaultValue={this.props.currentSeason}
                        onChange={this.onChanged}>
                        {this.props.seasons.map(season => {
                            return <option key={"select_season_" + season} value={season}>{season + this.props.seasonOffset}</option>
                        })}
                    </select>
                </div>    
            }
            </Translation>
    }
}

export default SeasonSelector