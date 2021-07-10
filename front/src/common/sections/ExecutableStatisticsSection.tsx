import React from 'react';
import Blur from '../widgets/Blur'
import '../../i18n'
import { LoadingEnum } from '../enums/LoadingEnum'
import Bot from '../widgets/Bot'
import StatisticsSection from './StatisticsSection';

export interface LoadableState<State, DataRequest> {
    loadingState: LoadingEnum,
    dataRequest: DataRequest,
    state: State
}

abstract class ExecutableExecutableStatisticsSection<Props, State, DataType, DataRequest> extends StatisticsSection<Props, LoadableState<State, DataRequest>> {

    constructor(props: Props, title: string | JSX.Element) {
        super(props, title)

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

    renderContent() {
        if(this.state.loadingState === LoadingEnum.BOT) {
            return <Bot />
        }

        return <>
                <Blur loadingState={this.state.loadingState}
                    updateCallback={this.update} />               
                {this.renderSection()}
        </>
    }
} 

export default ExecutableExecutableStatisticsSection