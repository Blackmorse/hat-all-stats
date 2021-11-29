import React from 'react'
import { PagesEnum } from "../enums/PagesEnum";
import Mappings from '../enums/Mappings'
import { Link } from 'react-router-dom';
import { NavLink } from 'react-bootstrap'
import './TableLink.css'
import QueryParams from '../QueryParams';

export interface LinkProps {
    text: string | JSX.Element,
    tableLink?: boolean,
    page?: PagesEnum,
    queryParams?: QueryParams,
    forceRefresh?: boolean,
    className?: string
}

abstract class HattidLink<Props extends LinkProps> extends React.Component<Props, {}> {
    abstract baseString(): string

    additionalContent(): JSX.Element {
        return <></>
    }

    render() {
        let parameters: any = {}
        let page: PagesEnum | undefined = this.props.page
        if(page) {    
            parameters.page = Mappings.queryParamToPageMap.getKey(page as PagesEnum)
        }

        if(this.props.queryParams !== undefined) {
            if(this.props.queryParams.sortingField) {
                parameters.sortingField = this.props.queryParams.sortingField || ''
            }
            if(this.props.queryParams.selectedRow !== undefined) {
                parameters.row = this.props.queryParams.selectedRow
            }
            if(this.props.queryParams.round !== undefined) {
                parameters.round = this.props.queryParams.round
            }
            if(this.props.queryParams.season) {
                parameters.season = this.props.queryParams.season || 0
            }
            if(this.props.queryParams.teamId !== undefined) {
                parameters.teamId = this.props.queryParams.teamId || 0
            }
        }
        let queryParams = new URLSearchParams(parameters).toString()

        let className: string
        if (this.props.className !== undefined) {
            className = this.props.className!
        } else if(this.props.tableLink !== undefined) {
            className = (this.props.tableLink) ? "table_link" : "menu_link link-dark rounded m-0 p-0 w-75"
        } else {
            className = "table_link"
        }

        //**cking workaround. Can't update the page.... 
        //TODO
        let cback: () => void = () => {}
        if(this.props.forceRefresh !== undefined && this.props.forceRefresh) {
            cback = () => {setTimeout( () => {window.location.reload()}, 100)}
        } 

        return <> 
            {this.additionalContent()}
            <NavLink className="p-0"
                href={this.baseString() + '?' + queryParams}
                onClick={cback} >
                    <u className={className} >{this.props.text}</u>
            </NavLink>
            
        </>
    }
}

export default HattidLink