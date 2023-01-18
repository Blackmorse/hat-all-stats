import React from 'react'
import { Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import '../../i18n'
import i18n from '../../i18n'

export interface SectionState {
    collapsed: boolean
}

interface HeaderElement {
    header: JSX.Element | string,
    additionalElement?: JSX.Element
}

export type GConstructor<T = {}> = new (...args: any[]) => T;

export type GSection<Props, State extends SectionState> = GConstructor<React.Component<Props, State>>

export default function Section<P, S extends SectionState, TBase extends GSection<P, S>>
            (Base: TBase,
            titleFunc?: (p: P, s: S) =>  string | HeaderElement) {

    return class Section extends Base {

        constructor(...args: any[]) {
            super(args[0])
            this.collapse=this.collapse.bind(this)
        }

        collapse() {
            let newState: S = Object.assign({}, this.state)
            newState.collapsed = !this.state.collapsed
            this.setState(newState)
        }

        render(): JSX.Element {
            let titleBase: string | HeaderElement= titleFunc === undefined ? '' : titleFunc(this.props, this.state)
                      
            let title: JSX.Element
            let additionalElement: JSX.Element = <></>
            if (typeof titleBase  === 'string') {
                title = <>{i18n.t(titleBase)}</>
            } else {
                if (typeof titleBase.header === 'string') {
                    title = <>{i18n.t(titleBase.header)}</>
                } else {
                    title = titleBase.header
                }
                additionalElement = titleBase.additionalElement === undefined? <></> : titleBase.additionalElement
            }
    
            let triangle = (!this.state.collapsed) ? <i className="bi bi-caret-down-fill"></i> : <i className="bi bi-caret-right-fill"></i>

            return <Card className="shadow-sm mt-3">
                <Card.Header className="lead text-start"  onClick={this.collapse}>
                    {triangle} <Link to='#' className="link-dark section-cursor"> {title}</Link> 
                    {additionalElement}
                </Card.Header>
                <Card.Body className="p-2">
                    <span className={(this.state.collapsed) ? 'd-none' : ''}>
                        {super.render()}
                    </span>
                </Card.Body>
            </Card>
        }
    }
}

