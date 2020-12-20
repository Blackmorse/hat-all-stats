import LevelDataProps, { LevelDataPropsWrapper } from "../LevelDataProps";
import LevelData from "../../rest/models/leveldata/LevelData";
import AbstractTableSection, { DataRequest } from './AbstractTableSection'
import StatisticsParameters, { StatsTypeEnum, StatsType } from "../../rest/models/StatisticsParameters";
import { LoadingEnum } from '../enums/LoadingEnum'
import RestTableData from '../../rest/models/RestTableData'
import { SelectorsEnum } from "./SelectorsEnum";
import LevelRequest from "../../rest/models/request/LevelRequest";
import PlayersParameters from "../../rest/models/PlayersParameters";


abstract class PlayersTableSection<Data extends LevelData, TableProps extends LevelDataProps<Data>, Model>
    extends AbstractTableSection<Data, TableProps, Model> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>, 
            defaultSortingField: string, 
            defaultStatsType: StatsType,
            statsTypes: Array<StatsTypeEnum>) {
        super(props, defaultSortingField, defaultStatsType, statsTypes, 
            [SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYER_ROLES, SelectorsEnum.NATIONALITIES_SELECTOR])
    }

    abstract fetchDataFunction(levelRequest: LevelRequest,
        statisticsParameters: StatisticsParameters,
        playersParameters: PlayersParameters,
        callback: (loadingEnum: LoadingEnum, restTableData?: RestTableData<Model>) => void): void

    executeDataRequest(dataRequest: DataRequest, 
            callback: (loadingState: LoadingEnum, result?: RestTableData<Model>) => void): void {
        const leveRequest = this.props.levelDataProps.createLevelRequest()
        this.fetchDataFunction(leveRequest, dataRequest.statisticsParameters, dataRequest.playersParameters, callback)
    }
}

export default PlayersTableSection