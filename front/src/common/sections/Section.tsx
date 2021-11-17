import React from 'react'
import '../../i18n'
import i18n from '../../i18n'
import './StatisticsSection.css'

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
    
            let triangle = (this.state.collapsed) ? <>&#x25BA;</> : <>&#x25BC;</>
            return <section className="statistics_section">         
                <header className="statistics_header">
                        <span className="statistics_header_title_with_triangle" onClick={this.collapse}>
                            {triangle} <span className="statistics_header_title">{title}</span>
                        </span>
                        {additionalElement}
                </header>
                <span className={(this.state.collapsed) ? 'hidden' : ''}>
                    {super.render()}
                </span>
            </section>
        }
    }
}

