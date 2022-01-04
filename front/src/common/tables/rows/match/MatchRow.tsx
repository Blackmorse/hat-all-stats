import React from 'react'
import TeamMatchInfoExecutableSection from '../../../../team/matches/TeamMatchInfoExecutableSection'
import '../../../links/TableLink.css'

export interface TableRowProps<RowModel> {
    rowIndex: number,
    rowModel: RowModel,
    className: string,
    showCountryFlags?: boolean
}

interface State {
    expanded: boolean
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
                <td>                   
                    <i className='bi bi-caret-right-fill table_link' onClick={() => this.setState({expanded: !this.state.expanded})}></i>
                </td>
                {columns}
            </tr>
        } else {
            return <>
                <tr className={this.props.className}>
                    <td>
                        <i className='bi bi-caret-down-fill table_link' onClick={() => this.setState({expanded: !this.state.expanded})}></i>
                    </td>
                    {columns}
                </tr>
                <tr className='white_row'>
                    <td colSpan={columns.length + 1}>
                        <TeamMatchInfoExecutableSection matchId={this.props.rowModel.matchId} />
                    </td>
                </tr>
            </>
        }
    }
}

export default MatchRow