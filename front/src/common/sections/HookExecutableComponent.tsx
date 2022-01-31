import React, {useEffect, useState} from 'react'
import {LoadingEnum} from '../enums/LoadingEnum'
import Blur from '../widgets/Blur'
import Bot from '../widgets/Bot'

interface Props<Request, Response> {
    initialRequest: Request
    executeRequest: (request: Request, callback: (loadingState: LoadingEnum, result?: Response) => void) => void
    content: (setRequest: (request: Request) => void, data?: Response) => JSX.Element
}

const ExecutableComponent = <Request, Response>(props: Props<Request, Response>) => {
    const [data, setData] = useState(undefined as Response | undefined)
    const [loadingEnum, setLoadingEnum] = useState(LoadingEnum.LOADING)
    const [request, setRequest] = useState(props.initialRequest)
    const [updateCounter, setUpdateCounter] = useState(0) //To update after failes, because request is not updated

    useEffect(() => {
        setLoadingEnum(LoadingEnum.LOADING)
        props.executeRequest(request, (loadingState, result) => {
            setData(result)
            setLoadingEnum(loadingState)
        })
    }, [request, updateCounter])

    if (loadingEnum === LoadingEnum.BOT) {
        return <Bot />
    }

    return <>
                <Blur loadingState={loadingEnum}
                    updateCallback={() => {setUpdateCounter(updateCounter + 1)}} />               
                {props.content(setRequest, data)}
            </>
}

export default ExecutableComponent
