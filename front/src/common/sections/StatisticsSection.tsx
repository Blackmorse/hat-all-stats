import React from 'react';
import '../../i18n'
import i18n from '../../i18n'

abstract class StatisticsSection<Props = {}, State = {}> extends React.Component<Props, State> {
    title: string | JSX.Element

    constructor(props: Props, title: string | JSX.Element) {
        super(props)
        this.title = title
    }

    abstract renderContent(): JSX.Element

    render(): JSX.Element {
        let title: JSX.Element
        if (typeof this.title  === 'string') {
            title = <>{i18n.t(this.title)}</>
        } else {
            title = this.title
        }

        return <section className="statistics_section">
            <header className="statistics_header">
                    <span className="statistics_header_triangle">&#x25BC; {title}</span>
                </header>
                {this.renderContent()}
        </section>
    }
}

export default StatisticsSection