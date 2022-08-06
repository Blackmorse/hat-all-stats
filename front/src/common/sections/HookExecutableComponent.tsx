import React, {useEffect, useState} from 'react'
import {LoadingEnum} from '../enums/LoadingEnum'
import Blur from '../widgets/Blur'
import Bot from '../widgets/Bot'
import Section from './HookSection'

interface Props<Request, Response, State=Response> {
    initialRequest: Request
    responseToState: (response?: Response, currentState?: State) => State
    executeRequest: (request: Request, callback: (loadingState: LoadingEnum, result?: Response) => void) => void
    //Client has an ability to set the request, or directly set state
    content: (setRequest: (request: Request) => void, setState: (state: State) => void, currentState: State) => JSX.Element
    sectionTitle?: (state: State) => string | JSX.Element
}

const ExecutableComponent = <Request, Response, State=Response>(props: Props<Request, Response, State>) => {
    const [state, setState] = useState(props.responseToState(undefined as Response | undefined))
    const [loadingEnum, setLoadingEnum] = useState(LoadingEnum.LOADING)
    const [request, setRequest] = useState(props.initialRequest)
    const [updateCounter, setUpdateCounter] = useState(0) //To update after failures, because request is not updated

    useEffect(() => {
        setLoadingEnum(LoadingEnum.LOADING)
        props.executeRequest(request, (loadingState, result) => {
            setState(props.responseToState(result, state))
            setLoadingEnum(loadingState)
        })
    }, [request, updateCounter])

    if (loadingEnum === LoadingEnum.BOT) {
        return <Bot />
    }

    let render = <>
                <Blur loadingState={loadingEnum}
                    updateCallback={() => {setUpdateCounter(updateCounter + 1)}} />               
                {props.content(setRequest, setState, state)}
            </>

    if (props.sectionTitle === undefined) {
        return render
    } else {
        return <Section element={render} title={props.sectionTitle(state)}/>
    }
}

export default ExecutableComponent
