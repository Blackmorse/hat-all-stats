import React from 'react'
import './TopMenu.css'
import { Link } from 'react-router-dom';


abstract class TopMenu<Props> extends React.Component<Props> {

    abstract links(): Array<[string, string?]>

    abstract selectBox(): JSX.Element | undefined

    render(): JSX.Element {
       let selectBox = this.selectBox()
       let links = this.links()
       let arrow = <>&#8674;</>
       return <div className="header_inner">
           {this.links().map((link, index) => {
               return <React.Fragment key={'top_menu_link_' + index}>
                    <Link className="header_link" to={link[0]} >{link[1]}</Link>
                    {(index !== links.length - 1 || selectBox) ? arrow : <></>}
                </React.Fragment>
           })}
            {selectBox}
            <Link className="logo_href" to="/">
                <img className="logo" src="/logo.png" alt="AlltidLike"/>
            </Link>
        </div>
   }
}

export default TopMenu