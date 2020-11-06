import LevelData from '../rest/models/leveldata/LevelData';
import LevelRequest from '../rest/models/request/LevelRequest';

export interface LevelDataPropsWrapper<Data extends LevelData, TableProps extends LevelDataProps<Data>> {
    levelDataProps: TableProps
}

abstract class LevelDataProps<Data extends LevelData> {
    levelData: Data

    constructor(levelData: Data) {
        this.levelData = levelData
    }

    abstract leagueId(): number

    currentSeason(): number {
        return this.seasonRoundInfo()[this.seasonRoundInfo().length - 1][0]
    }
    seasons(): Array<number> {
        return this.seasonRoundInfo().map(seasonInfo => seasonInfo[0])
    }
    currentRound(): number {
        let rounds = this.seasonRoundInfo()[this.seasonRoundInfo().length - 1][1]
        return rounds[rounds.length - 1]
    }

    rounds(seas: number): Array<number> {
        let r = this.seasonRoundInfo().filter(season => season[0] === seas )
        return r[0][1]
    }

    seasonRoundInfo(): Array<[number, Array<number>]> {return this.levelData.seasonRoundInfo}

    currency(): string {return this.levelData.currency}

    currencyRate(): number {return this.levelData.currencyRate}

    abstract createLevelRequest(): LevelRequest
}

export default LevelDataProps