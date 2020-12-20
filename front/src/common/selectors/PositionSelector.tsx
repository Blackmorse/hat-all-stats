import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import './Selector.css'

interface Props {
    value?: string,
    callback: (role?: string) => void
}

class PositionSelector extends React.Component<Props> {
    positions: Array<string | undefined>
    
    constructor(props: Props) {
        super(props)
        this.positions = [undefined, 'defender', 'wingback', 'midfielder'
            , 'winger', 'forward', 'keeper']
    }

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        let role = event.currentTarget.value
        this.props.callback(role)
    }

    render() {
        return <Translation>
            { (t, { i18n }) =>
                <div className="selector_div">
                    <span className="selector_div_entry">{t('table.position')}:</span>
                    <select className="selector_div_entry" defaultValue={this.props.value}
                            onChange={this.onChanged}>
                        {this.positions.map(position => {
                            return <option value={position} key={'select_position_' + position}>{t('dream_team.' + position)}</option>
                        })}
                    </select>
                </div>
            }
            </Translation>
    }
}

export default PositionSelector