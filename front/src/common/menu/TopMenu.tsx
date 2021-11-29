import React from 'react'
import './TopMenu.css'
import { Link } from 'react-router-dom';
import ContentLoader from 'react-content-loader'
import { Navbar, Container, NavbarBrand, Image, Nav, Row , Col} from 'react-bootstrap'


abstract class TopMenu<Props> extends React.Component<Props> {

    abstract links(): Array<[string, string?]>

    abstract selectBox(): JSX.Element | undefined

    sectionLinks(): JSX.Element | undefined {
        return undefined
    }

    sectionLinksNew(): Array<{href: string, text: string}> {
        return []
    }

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

       return <Container fluid>
           {/* 
           {this.links().map((link, index) => {
               return <React.Fragment key={'top_menu_link_' + index} >
                    <Link className="header_link" to={link[0]} >
                        <span className="header_link_text">{(link[1]) ? link[1] : placeholder}</span>
                    </Link>
                    {(index === links.length - 1) ? <span className="header_link external">{this.externalLink()}</span> : <></>}
                    <span>{(index !== links.length - 1 || selectBox) ? arrow : <></>}</span>
                </React.Fragment>
           })}
*/}
            <Navbar bg="dark" className="navbar-dark "  expand="md"   >
                <Container  fluid d-flex className="flex-row-reverse" >
                    <Navbar.Brand><Image  width="300"   src="/logo.png" alt="AlltidLike" className="logo d-inline-block align-top" /></Navbar.Brand>
                    <Navbar.Toggle aria-controls="topmenu-navbar" /> 

                    <Navbar.Collapse id="topmenu-navbar" >
                        <ul className="navbar-nav me-auto mb-2 d-flex">
                            {/* Place for this.links()! */}
                            <li className="nav-item me-md-5" >{selectBox}</li>
                                {this.sectionLinksNew().map(link => {
                                    return <li className="nav-item mx-md-3 d-flex align-items-center">
                                            <Nav.Link className="lead d-flex overflow-text" href={link.href}><strong>{link.text}</strong></Nav.Link>
                                        </li>
                                })}
                        </ul>
                    </Navbar.Collapse>                   
                </Container>
            </Navbar>
        </Container>
   }
}

export default TopMenu