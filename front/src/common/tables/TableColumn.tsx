interface TableColumn<RowModel> {
    columnHeader: {
        title: string
        poppedHint?: string,
        sortingField?: string,
        center?: boolean
    },
    columnValue: {
        provider: (rowModel: RowModel, index: number) => string | JSX.Element,
        center?: boolean
    }
}

export default TableColumn
