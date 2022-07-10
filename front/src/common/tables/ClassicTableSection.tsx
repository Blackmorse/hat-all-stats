import LevelDataProps, { LevelDataPropsWrapper } from "../LevelDataProps";
import AbstractTableSection, { DataRequest } from './AbstractTableSection'
import StatisticsParameters, { StatsTypeEnum, StatsType } from "../../rest/models/StatisticsParameters";
import { LoadingEnum } from '../enums/LoadingEnum'
import RestTableData from '../../rest/models/RestTableData'
import { SelectorsEnum } from "./SelectorsEnum";
import LevelRequest from "../../rest/models/request/LevelRequest";

abstract class ClassicTableSection<TableProps extends LevelDataProps, Model>
    extends AbstractTableSection<TableProps, Model, RestTableData<Model>> {

    constructor(props: LevelDataPropsWrapper<TableProps>, 
            defaultSortingField: string, 
            defaultStatsType: StatsType,
            statsTypes: Array<StatsTypeEnum>) {
        super(props, defaultSortingField, defaultStatsType, statsTypes, 
            [SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR])
    }

    abstract fetchDataFunction(levelRequest: LevelRequest,
        statisticsParameters: StatisticsParameters,
        callback: (loadingEnum: LoadingEnum, restTableData?: RestTableData<Model>) => void): void

    executeDataRequest(dataRequest: DataRequest, 
            callback: (loadingState: LoadingEnum, result?: RestTableData<Model>) => void): void {
        const leveRequest = this.props.levelDataProps.createLevelRequest()
        this.fetchDataFunction(leveRequest, dataRequest.statisticsParameters, callback)
    }

    responseModelToRowModel(responseModel: RestTableData<Model>): RestTableData<Model> {
        return responseModel
    }
}

export default ClassicTableSection
