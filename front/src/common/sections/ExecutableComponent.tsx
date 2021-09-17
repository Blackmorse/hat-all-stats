import React from 'react'
import { LoadingEnum } from '../enums/LoadingEnum';
import Blur from '../widgets/Blur';
import Bot from '../widgets/Bot';

export interface LoadableState<State, DataRequest> {
    loadingState: LoadingEnum,
    dataRequest: DataRequest,
    state: State
}

abstract class ExecutableComponent<Props, BaseState, DataType, DataRequest, State extends LoadableState<BaseState, DataRequest> = LoadableState<BaseState, DataRequest>>
    extends React.Component<Props, State> {
    
    constructor(props: Props) {
        super(props)
        this.update=this.update.bind(this)
        this.updateWithRequest=this.updateWithRequest.bind(this)
        this.stateFromResult=this.stateFromResult.bind(this)
        this.executeDataRequest=this.executeDataRequest.bind(this)
    }

    abstract renderSection(): JSX.Element

    abstract executeDataRequest(dataRequest: DataRequest, callback: (loadingState: LoadingEnum, result?: DataType) => void): void

    abstract stateFromResult(result?: DataType): BaseState

    componentDidMount() {
        this.update()
    }

    update() {
        this.updateWithRequest(this.state.dataRequest)
    }

    updateWithRequest(dataRequest: DataRequest): void {
        this.setState({
            loadingState: LoadingEnum.LOADING,
            dataRequest: dataRequest,
            state: this.state.state
        })

        this.executeDataRequest(dataRequest, (loadingStatus, result) => this.setState({
            loadingState: loadingStatus,
            state: this.stateFromResult(result),
            dataRequest: this.state.dataRequest
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