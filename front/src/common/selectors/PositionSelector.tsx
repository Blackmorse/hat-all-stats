import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import { Form } from 'react-bootstrap'

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
        const role = event.currentTarget.value
        this.props.callback(role)
    }

    render() {
        return <Translation>
            { (t) =>
                <div className="d-flex flex-row align-items-center mx-2  my-xs-2 my-sm-2 my-lg-0 my-md-0">
                    <span className="me-1">{t('table.position')}:</span>
                    <Form.Select size='sm' defaultValue={this.props.value}
                            onChange={this.onChanged}>
                        {this.positions.map(position => {
                            return <option value={position} key={'select_position_' + position}>{t('dream_team.' + position)}</option>
                        })}
                    </Form.Select>
                </div>
            }
            </Translation>
    }
}

export default PositionSelector
