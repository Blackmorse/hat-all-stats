import LevelData from '../rest/models/leveldata/LevelData';
import LevelRequest from '../rest/models/request/LevelRequest';
import OverviewRequest from '../rest/models/request/OverviewRequest';

export interface LevelDataPropsWrapper<LevelProps extends LevelDataProps> {
    levelDataProps: LevelProps,
    showCountryFlags?: boolean
}

abstract class LevelDataProps {
    levelData: LevelData


    constructor(levelData: LevelData) {
        this.levelData = levelData
    }

    abstract leagueId(): number

    currentSeason(): number {
        return this.seasonRoundInfo()[this.seasonRoundInfo().length - 1][0]
    }

    offsettedSeason(): number {
        return this.currentSeason() + this.levelData.seasonOffset
    }

    seasonOffset(): number {
        return this.levelData.seasonOffset
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

    countries(): Array<[number, string]> {return this.levelData.countries}

    seasonRoundInfo(): Array<[number, Array<number>]> {return this.levelData.seasonRoundInfo}

    currency(): string {return this.levelData.currency}

    currencyRate(): number {return this.levelData.currencyRate}

    countriesMap(): Map<number, string> {
        return new Map(this.levelData.countries)
    }

    abstract createLevelRequest(): LevelRequest

    abstract createOverviewRequest(): OverviewRequest
}

export default LevelDataProps
