import React from 'react';
import { Translation } from 'react-i18next'
import '../../i18n'
import './ModelTableTh.css'
import { SortingDirection } from '../../rest/models/StatisticsParameters';

interface SortingState {
    callback: (sortBy: string) => void,
    currentSorting: string,
    sortingDirection: SortingDirection
}

interface ThProps {
    title: string,
    titlePostfix?: string,
    sortingField: string,
    sortingState: SortingState,
    poppedHint?: string
}

class ModelTableTh extends React.Component<ThProps> {
    render() {
        return <Translation>
            {(t, { i18n }) =>
                <th className={(this.props.poppedHint) ? "model_table_header value hint" : "model_table_header value"} popped-hint={this.props.poppedHint} onClick={() => this.props.sortingState.callback(this.props.sortingField)}>
                    {t(this.props.title) + ((this.props.titlePostfix) ? this.props.titlePostfix : '')}
                    {(this.props.sortingField === this.props.sortingState.currentSorting && this.props.sortingState.sortingDirection === SortingDirection.DESC) ? "↓" : ""}
                    {(this.props.sortingField === this.props.sortingState.currentSorting && this.props.sortingState.sortingDirection === SortingDirection.ASC) ? "↑" : ""}
                </th>
            }
        </Translation>
    }
}

export default ModelTableTh;