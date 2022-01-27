import React from 'react';
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom';
import '../../i18n'
import { SortingDirection } from '../../rest/models/StatisticsParameters';
import HattidTooltip from './HattidTooltip'

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

const SortingTableTh = (props: ThProps) => {
    const t = useTranslation().t

    return  <th className='text-center' >
          <HattidTooltip
                poppedHint={props.poppedHint}
                content={<Link className="link-dark" to='#' onClick={() => props.sortingState.callback(props.sortingField)}>{t(props.title) + ((props.titlePostfix) ? props.titlePostfix : '')}</Link>}
            />
            {(props.sortingField === props.sortingState.currentSorting && props.sortingState.sortingDirection === SortingDirection.DESC) ? "↓" : ""}
            {(props.sortingField === props.sortingState.currentSorting && props.sortingState.sortingDirection === SortingDirection.ASC) ? "↑" : ""}
            </th>
}

export default SortingTableTh;
