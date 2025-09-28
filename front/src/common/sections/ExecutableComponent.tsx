import React, { type JSX } from 'react'
import { LoadingEnum } from '../enums/LoadingEnum';
import Blur from '../widgets/Blur';
import Bot from '../widgets/Bot';

export interface LoadableState<DataRequest> {
    loadingState: LoadingEnum,
    dataRequest: DataRequest
}

abstract class ExecutableComponent<Props, State, DataType, DataRequest>
    extends React.Component<Props, State & LoadableState<DataRequest>> {
    
    constructor(props: Props) {
        super(props)
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
            ...this.state,
            loadingState: LoadingEnum.LOADING,
            dataRequest: dataRequest,
        })

        this.executeDataRequest(dataRequest, (loadingStatus, result) => this.setState({
            loadingState: loadingStatus,
            dataRequest: this.state.dataRequest,
            ...this.stateFromResult(result)
        }))
    }

    render() {
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

export default ExecutableComponent
