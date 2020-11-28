import React from 'react'
import './TopMenu.css'
import { Link } from 'react-router-dom';
import ContentLoader from 'react-content-loader'

abstract class TopMenu<Props> extends React.Component<Props> {

    abstract links(): Array<[string, string?]>

    abstract selectBox(): JSX.Element | undefined

    abstract externalLink(): JSX.Element | undefined

    render(): JSX.Element {
       let selectBox = this.selectBox()
       let links = this.links()
       let arrow = <>&#8674;</>
       let placeholder = <ContentLoader 
            speed={1}
            width={80}
            height={80}
            viewBox="0 0 100 70"
            backgroundColor="#008000"
            foregroundColor="#00aa00"   
            >
            <rect x="8" y="8" rx="10" ry="10" width="77" height="50" />
        </ContentLoader>

       return <div className="header_inner">
           {this.links().map((link, index) => {
               return <React.Fragment key={'top_menu_link_' + index} >
                    <Link className="header_link" to={link[0]} >
                        <span className="header_link_text">{(link[1]) ? link[1] : placeholder}</span>
                    </Link>
                    {(index === links.length - 1) ? <span className="header_link external">{this.externalLink()}</span> : <></>}
                    <span>{(index !== links.length - 1 || selectBox) ? arrow : <></>}</span>
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