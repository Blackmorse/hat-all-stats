import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import { Form } from 'react-bootstrap'

export class Formation {
    defenders: number
    midfielders: number
    forwards: number

    constructor(defenders: number, midfielders: number, forwards: number) {
        this.defenders = defenders
        this.midfielders = midfielders
        this.forwards = forwards
    }

    toString(): string {
        return this.defenders + "-" + this.midfielders + "-" + this.forwards
    }

    static fromString(formation: string): Formation {
        let split = formation.split("-")
        return new Formation(Number(split[0]), Number(split[1]), Number(split[2]))
    }
}

interface FormationSelectorProps {
    currentFormation: Formation,
    callback: (formation: Formation) => void
}

class FormationSelector extends React.Component<FormationSelectorProps> {
    formations: Array<Formation>

    constructor(props: FormationSelectorProps) {
        super(props)

        this.formations = [
            Formation.fromString("5-5-0"),
            Formation.fromString("5-4-1"),
            Formation.fromString("5-3-2"),
            Formation.fromString("5-2-3"),
            Formation.fromString("4-5-1"),
            Formation.fromString("4-4-2"),
            Formation.fromString("4-3-3"),
            Formation.fromString("3-5-2"),
            Formation.fromString("3-4-3"),
            Formation.fromString("2-5-3")
        ]
    }

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        let formation = Formation.fromString(event.currentTarget.value)
        this.props.callback(formation)
    }

    render() {
        return <Translation>
            { (t, { i18n }) =>
            <div className="d-flex flex-row align-items-center mx-2  my-xs-2 my-sm-2 my-lg-0 my-md-0">
                <span className='me-1'>{t('matches.formation')}:</span>
                <Form.Select size='sm' value={this.props.currentFormation.toString()}
                    onChange={this.onChanged}>
                    {this.formations.map(formation => {
                        return <option key={"formation_selector_" + formation.toString()}
                            value={formation.toString()}>
                                {formation.toString()}
                        </option>
                    })}
                
                </Form.Select>
            </div>
        }
        </Translation>
    }
}

export default FormationSelector
