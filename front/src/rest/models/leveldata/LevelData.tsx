export default interface LevelData {
    seasonOffset: number,
    seasonRoundInfo: Array<[number, Array<number>]>,
    currency: string,
    currencyRate: number,
    countries: Array<[number, string]>,
}