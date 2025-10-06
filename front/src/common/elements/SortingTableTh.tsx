import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom';
import '../../i18n'
import { SortingDirection } from '../../rest/models/StatisticsParameters';
import HattidTooltip from './HattidTooltip'
import { Fragment } from 'react';

interface SortingState {
    callback: (sortBy: string) => void,
    currentSorting: string,
    sortingDirection: SortingDirection
}

export interface SortingHeader {
    title: string,
    titlePostfix?: string,
    sorting?: {
        field: string,
        state: SortingState
    }
    poppedHint?: string,
    center?: boolean
}

const SortingTableTh = (props: SortingHeader) => {
    const t = useTranslation().t

    if(props.sorting === undefined) {
        return <th className={(props.center === undefined || !props.center) ? '' : 'text-center'}><HattidTooltip
                poppedHint={props.poppedHint}
                content={<Fragment>{props.title}</Fragment>}
            /></th>
    } else {

        return  <th className='text-center' key={props.sorting.field} >
              <HattidTooltip
                    poppedHint={props.poppedHint}
                    content={<Link className="link-dark" to='#' onClick={() => props.sorting!.state.callback(props.sorting!.field)}>{t(props.title) + ((props.titlePostfix) ? props.titlePostfix : '')}</Link>}
                />
                {(props.sorting.field === props.sorting.state.currentSorting && props.sorting.state.sortingDirection === SortingDirection.DESC) ? "↓" : ""}
                {(props.sorting.field === props.sorting.state.currentSorting && props.sorting.state.sortingDirection === SortingDirection.ASC) ? "↑" : ""}
                </th>
    }
}

export default SortingTableTh;
