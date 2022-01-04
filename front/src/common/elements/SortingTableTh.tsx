import React from 'react';
import { Translation } from 'react-i18next'
import { Link } from 'react-router-dom';
import '../../i18n'
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

class SortingTableTh extends React.Component<ThProps> {
    render() {
        return <Translation>
            {(t, { i18n }) =>
                <th className={(this.props.poppedHint) ? "text-center hint" : "text-center"} popped-hint={this.props.poppedHint} >
                    <Link className="link-dark" to='#' onClick={() => this.props.sortingState.callback(this.props.sortingField)}>{t(this.props.title) + ((this.props.titlePostfix) ? this.props.titlePostfix : '')}</Link>
                    {(this.props.sortingField === this.props.sortingState.currentSorting && this.props.sortingState.sortingDirection === SortingDirection.DESC) ? "↓" : ""}
                    {(this.props.sortingField === this.props.sortingState.currentSorting && this.props.sortingState.sortingDirection === SortingDirection.ASC) ? "↑" : ""}
                </th>
            }
        </Translation>
    }
}

export default SortingTableTh;