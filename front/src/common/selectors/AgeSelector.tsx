import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import './Selector.css'
import TwoThumbs from '../elements/TwoThumbs'

interface Props {
    minAge?: number,
    maxAge?: number,
    callback: (ages: [number?, number?]) => void
}

class AgeSelector extends React.Component<Props> {
    render() {
        return <Translation>
        { (t, { i18n }) =>
        <div className="selector_div" style={{width: '200px'}}>
            <span className="selector_div_entry">{t('table.age')}:</span>
            <span className="selector_div_entry" style={{width: '100%', transform: 'translate(7px, 11px)'}}>
                <TwoThumbs callback={this.props.callback}/>
            </span>
        </div>
        }
        </Translation>   
    }
}

export default AgeSelector