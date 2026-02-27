import TeamSortingKey from './TeamSortingKey'

interface TeamFanclubFlags {
    teamSortingKey: TeamSortingKey,
    fanclubSize: number,
    homeFlags: number,
    awayFlags: number,
    allFlags: number
}

export type TeamFanclubFlagsChart = TeamFanclubFlags & { season: number, round: number }

export default TeamFanclubFlags
