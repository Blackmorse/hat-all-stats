import React from 'react';
import Blur from '../widgets/Blur'
import { Translation } from 'react-i18next'
import '../../i18n'
import { LoadingEnum } from '../enums/LoadingEnum'
import Bot from '../widgets/Bot'
import i18n from '../../i18n';

interface LoadableState {
    loadingState: LoadingEnum
}

abstract class StatisticsSection<Props, State extends LoadableState> extends React.Component<Props, State> {
    title: string | JSX.Element

    constructor(props: Props, title: string | JSX.Element) {
        super(props)
        this.title = title

        this.updateCurrent=this.updateCurrent.bind(this)
    }

    abstract renderSection(): JSX.Element

    abstract updateCurrent(): void

    render() {
        if(this.state.loadingState === LoadingEnum.BOT) {
            return <Bot />
        }
        let title: JSX.Element
        if (typeof this.title  === 'string') {
            title = <>{i18n.t(this.title)}</>
        } else {
            title = this.title
        }

        return <Translation>{
            (t, { i18n }) => <section className="statistics_section">
                <Blur loadingState={this.state.loadingState}
                    updateCallback={this.updateCurrent} />
                <header className="statistics_header">
                    <span className="statistics_header_triangle">&#x25BC; {title}</span>
                </header>
                {this.renderSection()}
            </section>
        }
        </Translation>
    }
} 

export default StatisticsSection