import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import './Selector.css'
import { Form } from 'react-bootstrap'

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
                <div className='d-flex flex-row align-items-center mx-2  my-xs-2 my-sm-2 my-lg-0 my-md-0'>
                    <span className="me-1">{t('filter.season')}:</span>
                    <Form.Select size='sm' defaultValue={this.props.currentSeason}
                        onChange={this.onChanged}>
                        {this.props.seasons.map(season => {
                            return <option key={"select_season_" + season} value={season}>{season + this.props.seasonOffset}</option>
                        })}
                    </Form.Select>
                </div>    
            }
            </Translation>
    }
}

export default SeasonSelector