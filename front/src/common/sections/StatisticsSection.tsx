import React from 'react';
import Blur from '../widgets/Blur'
import { Translation } from 'react-i18next'
import '../../i18n'

interface LoadableState {
    dataLoading: boolean,
    isError: boolean
}

abstract class StatisticsSection<Props, State extends LoadableState> extends React.Component<Props, State> {
    title: string

    constructor(props: Props, title: string) {
        super(props)
        this.title = title

        this.updateCurrent=this.updateCurrent.bind(this)
    }

    abstract renderSection(): JSX.Element

    abstract updateCurrent(): void

    render() {
        return <Translation>{
            (t, { i18n }) => <section className="statistics_section">
                <Blur dataLoading={this.state.dataLoading} isError={this.state.isError} 
                    updateCallback={this.updateCurrent} />
                <header className="statistics_header">
                    <span className="statistics_header_triangle">&#x25BC; {t(this.title)}</span>
                </header>
                {this.renderSection()}
            </section>
        }
        </Translation>
    }
} 

export default StatisticsSection