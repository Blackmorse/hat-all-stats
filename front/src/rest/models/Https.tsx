import { LoadingEnum } from "../../common/enums/LoadingEnum"

export interface HttpError {}

export interface Response {
    loadingEnum: LoadingEnum
}

export interface Success<Model> extends Response {
    loadingEnum: LoadingEnum.OK
    model: Model
}

export interface Failure extends Response {
    loadingEnum: LoadingEnum
    error: HttpError
}

export type Callback<Model> = (payload: Success<Model> | Failure) => void