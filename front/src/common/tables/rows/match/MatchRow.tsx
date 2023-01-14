import React, {useState} from 'react'
import TeamMatchInfoExecutableSection from '../../../../team/matches/TeamMatchInfoExecutableSection'
import '../../../links/TableLink.css'
import TableColumn from '../../TableColumn'

export interface TableRowProps<RowModel> {
    rowIndex: number,
    rowModel: RowModel,
    className: string,
    showCountryFlags?: boolean
}

interface State {
    expanded: boolean
}

interface HookMatchRowProperties<RowModel> {
    rowNum: number,
    entity: RowModel,
    tableColumns: Array<TableColumn<RowModel>>,
    className?: string,
    expandedRowFunc?: (model: RowModel) => JSX.Element
}

export const HookMatchRow = <RowModel extends {}>(props: HookMatchRowProperties<RowModel>) => {
    const [ expanded, setExpanded ] = useState(false)

    function row(): JSX.Element {
        return <>
                {props.tableColumns.map(tableColumn => {
                    return <td className={(tableColumn.columnValue.center === undefined || !tableColumn.columnValue.center) ? '' : 'text-center'}>{tableColumn.columnValue.provider(props.entity, props.rowNum)}</td>
                } ) }
            </>
    }

    if (props.expandedRowFunc === undefined) {
        return <tr className={props.className}>{row()}</tr>
    } else {
        if (!expanded) {
            return <tr className={props.className}>
                    <td key={'match_row' + '_' + Math.random()}>
                        <i className='bi bi-caret-right-fill table_link' onClick={() => setExpanded(!expanded)}></i>
                    </td>
                    {row()}
            </tr>
        } else {
            return <>
                <tr className={props.className}>
                    <td key={'match_row' + '_' + Math.random()}>
                        <i className='bi bi-caret-down-fill table_link' onClick={() => setExpanded(!expanded)}></i>
                    </td>
                    {row()}
                </tr>
                <tr className='white_row'>
                    <td colSpan={props.tableColumns.length + 1}>
                        {props.expandedRowFunc(props.entity)}
                    </td>
                </tr>
            </>
        }
    }
}


abstract class MatchRow<RowModel extends {matchId: number}, Props extends TableRowProps<RowModel>> extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state = {expanded: false}
    }

    abstract columns(rowIndex: number, rowModel: RowModel): Array<JSX.Element>

    render() {
        let columns = this.columns(this.props.rowIndex, this.props.rowModel)
        if (!this.state.expanded) {
            return <tr className={this.props.className}>
                <td key={'match_row' + this.props.rowModel.matchId + '_' + Math.random()}>                   
                    <i className='bi bi-caret-right-fill table_link' onClick={() => this.setState({expanded: !this.state.expanded})}></i>
                </td>
                {columns}
            </tr>
        } else {
            return <>
                <tr className={this.props.className}>
                    <td key={'match_row' + this.props.rowModel.matchId + '_' + Math.random()}>
                        <i className='bi bi-caret-down-fill table_link' onClick={() => this.setState({expanded: !this.state.expanded})}></i>
                    </td>
                    {columns}
                </tr>
                <tr className='white_row'>
                    <td colSpan={columns.length + 1} key={'match_row' + this.props.rowModel.matchId + '_' + Math.random()}>
                        <TeamMatchInfoExecutableSection matchId={this.props.rowModel.matchId} />
                    </td>
                </tr>
            </>
        }
    }
}

export default MatchRow
