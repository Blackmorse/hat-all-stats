import React from 'react'
import './Selector.css'
import '../../i18n'
import { Translation } from 'react-i18next'

interface Props {
    value: boolean,
    callback: (playedAllMatches: boolean) => void
}

class PlayedAllMatchesSelector extends React.Component<Props> {
    onChanged = (event: React.ChangeEvent<HTMLInputElement>) => {
        let checked = event.currentTarget.checked
        this.props.callback(checked)
    }

    render() {
        return <Translation>
        { (t, { i18n }) =>
            <div className="selector_div">
                <input type="checkbox" id="played_all_matches_checkbox" defaultChecked={this.props.value} onChange={this.onChanged} />
                <label htmlFor="played_all_matches_checkbox" className="selector_div_entry">{t('filter.full_season')}</label>
            </div>
        }
        </Translation>
    }
}

export default PlayedAllMatchesSelector