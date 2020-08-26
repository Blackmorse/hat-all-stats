export enum SortingDirection {
    ASC = "asc",
    DESC = "desc"
}

export default interface StatisticsParameters {
    page: number,
    pageSize: number,
    sortingField: string,
    sortingDirection: SortingDirection
}