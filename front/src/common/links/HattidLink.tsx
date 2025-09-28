import React, { type JSX } from 'react'
import { PagesEnum } from "../enums/PagesEnum";
import Mappings from '../enums/Mappings'
import { NavLink } from 'react-bootstrap'
import './TableLink.css'
import QueryParams from '../QueryParams';

export interface LinkProps {
    text: string | JSX.Element | undefined,
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

    postfixAdditionalContent(): JSX.Element {
        return <></>
    }

    render() {
        const parameters: any = Object.assign({}, this.props.queryParams)
        
        const page: PagesEnum | undefined = this.props.page
        if(page !== undefined) {    
            parameters.pageName = Mappings.queryParamToPageMap.getKey(page!)
        }

        const queryParams = new URLSearchParams(parameters)

        const keysToDel: Array<string> = []
        queryParams.forEach((key, value) => {
            if (value === undefined || value === 'undefined') {
                keysToDel.push(key)
            }
        })

        keysToDel.forEach(key => queryParams.delete(key))

        
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
            cback = () => {setTimeout( () => {window.location.replace(this.baseString() + '?' + queryParams)  }, 100)}
        } 

        return <> 
            {this.additionalContent()}
            <NavLink className='p-0  d-inline'
                href={this.baseString() + '?' + queryParams}
                onClick={cback} >
                    <u className={className} >{this.props.text}</u>
            </NavLink>
            {this.postfixAdditionalContent()}
        </>
    }
}

export default HattidLink
