import LevelData from './LevelData'

interface WorldData extends LevelData {
    countries: Array<[number, string]>
}

export default WorldData