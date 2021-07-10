import React from 'react';
import '../../i18n'
import i18n from '../../i18n'

export interface StatisticsSectionState {
    collapsed: boolean
}

abstract class StatisticsSection<Props = {}, State extends StatisticsSectionState = StatisticsSectionState> extends React.Component<Props, State> {
    title: string | JSX.Element

    constructor(props: Props, title: string | JSX.Element) {
        super(props)
        this.title = title
        this.collapse=this.collapse.bind(this)
    }

    abstract renderContent(): JSX.Element

    collapse() {
        let newState: StatisticsSectionState = Object.assign({}, this.state)
        newState.collapsed = !this.state.collapsed
        this.setState(newState)
    }

    render(): JSX.Element {
        let title: JSX.Element
        if (typeof this.title  === 'string') {
            title = <>{i18n.t(this.title)}</>
        } else {
            title = this.title
        }

        return <section className="statistics_section">         
            <header className="statistics_header">
                {this.state.collapsed ? <span className="statistics_header_title_with_triangle" onClick={this.collapse}>&#x25BA; <span className="statistics_header_title">{title}</span></span> : 
                <span className="statistics_header_title_with_triangle" onClick={this.collapse}>&#x25BC; <span className="statistics_header_title">{title}</span></span>}
            </header>
            <span className={(this.state.collapsed) ? 'hidden' : ''}>
                {this.renderContent()}
            </span>
        </section>
    }
}

export default StatisticsSection