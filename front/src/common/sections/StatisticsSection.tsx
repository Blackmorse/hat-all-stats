import React from 'react';
import Blur from '../widgets/Blur'
import { Translation } from 'react-i18next'
import '../../i18n'
import { LoadingEnum } from '../enums/LoadingEnum'
import Bot from '../widgets/Bot'
import i18n from '../../i18n';

export interface LoadableState<State, DataRequest> {
    loadingState: LoadingEnum,
    dataRequest: DataRequest,
    state: State
}

abstract class NewStatisticsSection<Props, State, DataType, DataRequest> extends React.Component<Props, LoadableState<State, DataRequest>> {
    title: string | JSX.Element

    constructor(props: Props, title: string | JSX.Element) {
        super(props)
        this.title = title

        this.update=this.update.bind(this)
        this.updateWithRequest=this.updateWithRequest.bind(this)
        this.stateFromResult=this.stateFromResult.bind(this)
        this.executeDataRequest=this.executeDataRequest.bind(this)
    }

    abstract renderSection(): JSX.Element

    abstract executeDataRequest(dataRequest: DataRequest, callback: (loadingState: LoadingEnum, result?: DataType) => void): void

    abstract stateFromResult(result?: DataType): State

    componentDidMount() {
        this.update()
    }

    update() {
        this.updateWithRequest(this.state.dataRequest)
    }

    updateWithRequest(dataRequest: DataRequest): void {
        this.setState({
            loadingState: LoadingEnum.LOADING,
            dataRequest: dataRequest
        })

        this.executeDataRequest(dataRequest, (loadingStatus, result) => this.setState({
            loadingState: loadingStatus,
            state: this.stateFromResult(result)
        }))
    }

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
                    updateCallback={this.update} />
                <header className="statistics_header">
                    <span className="statistics_header_triangle">&#x25BC; {title}</span>
                </header>
                {this.renderSection()}
            </section>
        }
        </Translation>
    }
} 

export default NewStatisticsSection