import { type JSX } from "react";
import TeamRanking from "../../rest/models/team/TeamRanking";
import { PagesEnum } from "../enums/PagesEnum";

interface RankingParameters {
    title: string,
    positionFunc: (teamRanking: TeamRanking) => number,
    valueFunc: (teamRanking: TeamRanking) => number,
    formatter: (value: number) => JSX.Element,
    sortingField: string,
    page: PagesEnum,
    yAxisFunc?: (n: number) => number 
}

export default RankingParameters
