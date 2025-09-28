import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import TwoThumbs from '../elements/TwoThumbs'

interface Props {
    minAge?: number,
    maxAge?: number,
    callback: (ages: [number?, number?]) => void
}

class AgeSelector extends React.Component<Props> {
    render() {
        return <Translation>
        { (t) =>
        <div className='d-flex flex-row align-items-center mx-2 my-xs-2 my-sm-2 my-lg-0 my-md-0' style={{width: '200px'}}>
            <span>{t('table.age')}:</span>
            <span style={{width: '100%', transform: 'translate(7px, 11px)'}}>
                <TwoThumbs callback={this.props.callback} left={this.props.minAge} right={this.props.maxAge}/>
            </span>
        </div>
        }
        </Translation>   
    }
}

export default AgeSelector
