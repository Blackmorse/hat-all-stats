import { getPlayerInjuries } from '../../../rest/clients/PlayerStatsClient'
import PlayerInjury from '../../../rest/models/player/PlayerInjury'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const PlayerInjuriesTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    return <TableSection<LevelProps, PlayerInjury>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getPlayerInjuries(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='injury'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        statsTypes={[StatsTypeEnum.ROUND]}
        pageEnum={PagesEnum.PLAYER_INJURIES}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.player(pi => pi.playerSortingKey, props.levelDataProps.countriesMap()),
            TableColumns.teamTableColumn(pi => pi.playerSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pi => pi.playerSortingKey),
            TableColumns.ageTableColumn(pi => pi.age, 'age'),
            TableColumns.injury(pi => pi.injury, 'injury')
        ]}
    />
}

export default PlayerInjuriesTable
