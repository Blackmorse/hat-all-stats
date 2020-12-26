import React from 'react'
import './Selector.css'
import '../../i18n'
import { Translation } from 'react-i18next'

interface Props {
    value: boolean,
    callback: (checkProperty: boolean) => void,
    title: string
}

class CheckBoxSelector extends React.Component<Props> {
    onChanged = (event: React.ChangeEvent<HTMLInputElement>) => {
        let checked = event.currentTarget.checked
        this.props.callback(checked)
    }

    render() {
        return <Translation>
        { (t, { i18n }) => <div className="selector_div">
            <input type="checkbox" id={"checkbox_" + this.props.title} defaultChecked={this.props.value} onChange={this.onChanged} />
                <label htmlFor={"checkbox_" + this.props.title} className="selector_div_entry">{t(this.props.title)}</label>
        </div>
        }
        </Translation>
    }
}

export default CheckBoxSelector