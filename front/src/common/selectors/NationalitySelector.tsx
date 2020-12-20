import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import './Selector.css'

interface Props {
    value?: number,
    countryMap: Map<number, string>,
    callback: (nationality?: number) => void
}

class NationalitySelector extends React.Component<Props> {
    nationalities: Array<number | undefined>

    constructor(props: Props) {
        super(props)
        this.nationalities = new Array<number | undefined>()
        
        this.nationalities = new Array<number | undefined>()
        this.nationalities.push(undefined)
        Array.from(this.props.countryMap.keys()).forEach(nationality => this.nationalities.push(nationality))
    }

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        let nationality = Number(event.currentTarget.value)
        
        this.props.callback((nationality === 0) ? undefined : nationality)
    }

    render() {
        return <Translation>
        { (t, { i18n }) =>
            <div className="selector_div">
                <span className="selector_div_entry">{t('filter.nationality')}</span>
                <select className="selector_div_entry" defaultValue={this.props.value}
                        onChange={this.onChanged}>
                    {this.nationalities.map(nationality => {
                        return <option value={nationality} key={'select_nationality_' + nationality}>
                            {this.props.countryMap.get(nationality || 0)}
                        </option>
                    })}
                </select>
            </div>
        }
        </Translation>
    }
}

export default NationalitySelector