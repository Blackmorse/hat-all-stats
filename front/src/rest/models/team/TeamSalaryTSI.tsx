import TeamSortingKey from "./TeamSortingKey";

interface TeamSalaryTSI {
    teamSortingKey: TeamSortingKey,
    tsi: number,
    salary: number,
    playersCount: number,
    avgSalary: number,
    avgTsi: number,
    salaryPerTsi: number
}

export default TeamSalaryTSI