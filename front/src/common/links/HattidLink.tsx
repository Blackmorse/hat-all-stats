import React from 'react'
import { PagesEnum } from "../enums/PagesEnum";
import Mappings from '../enums/Mappings'
import { Link } from 'react-router-dom';
import './TableLink.css'

export interface LinkProps {
    text: string | JSX.Element,
    tableLink?: boolean,
    page?: PagesEnum,
    sortingField?: string,
    rowNumber?: number,
    season?: number,
    round?: number,
    callback?: () => void
}

abstract class HattidLink<Props extends LinkProps> extends React.Component<Props, {}> {
    abstract baseString(): string

    render() {
        let parameters: any = {}
        let page: PagesEnum | undefined = this.props.page
        if(page) {    
            parameters.page = Mappings.queryParamToPageMap.getKey(page as PagesEnum)
        }
        if(this.props.sortingField) {
            parameters.sortingField = this.props.sortingField || ''
        }
        if(this.props.rowNumber) {
            parameters.row = this.props.rowNumber || 0
        }
        if(this.props.round) {
            parameters.round = this.props.round || 0
        }
        if(this.props.season) {
            parameters.season = this.props.season || 0
        }

        let queryParams = new URLSearchParams(parameters).toString()

        let className: string
        if(this.props.tableLink !== undefined) {
            className = (this.props.tableLink) ? "table_link" : "left_bar_link page"
        } else {
            className = "table_link"
        }

        return <Link className={className} 
            to={this.baseString() + '?' + queryParams}
            onClick={this.props.callback} >
                {this.props.text}
            </Link>
    }
}

export default HattidLink