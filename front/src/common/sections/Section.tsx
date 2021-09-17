import React from 'react'
import '../../i18n'
import i18n from '../../i18n'
import './StatisticsSection.css'

export interface SectionState {
    collapsed: boolean
}

export type GConstructor<T = {}> = new (...args: any[]) => T;

export type GSection<Props, State extends SectionState> = GConstructor<React.Component<Props, State>>

export default function Section<P, S extends SectionState, TBase extends GSection<P, S>>
            (Base: TBase,
            titleFunc?: (p: P, s: S) => JSX.Element | string) {

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
            let titleBase: JSX.Element | string = titleFunc === undefined ? '' : titleFunc(this.props, this.state)
            let title: JSX.Element
            if (typeof titleBase  === 'string') {
                title = <>{i18n.t(titleBase)}</>
            } else {
                title = titleBase
            }
    
            let triangle = (this.state.collapsed) ? <>&#x25BA;</> : <>&#x25BC;</>
            return <section className="statistics_section">         
                <header className="statistics_header">
                        <span className="statistics_header_title_with_triangle" onClick={this.collapse}>{triangle} <span className="statistics_header_title">{title}</span></span>
                </header>
                <span className={(this.state.collapsed) ? 'hidden' : ''}>
                    {super.render()}
                </span>
            </section>
        }
    }
}

