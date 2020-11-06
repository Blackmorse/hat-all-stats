export default interface RestTableData<Entity> {
    entities: Array<Entity>,
    isLastPage: boolean
}