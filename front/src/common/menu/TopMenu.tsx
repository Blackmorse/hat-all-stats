import React from 'react'
import './TopMenu.css'
import { Link } from 'react-router-dom';
import ContentLoader from 'react-content-loader'
import { Navbar, Container, Image, Nav, Row , Col, NavLink} from 'react-bootstrap'

interface TopMenuProps<Data> {
    data?: Data
}

abstract class TopMenu<Data, Props extends TopMenuProps<Data>> extends React.Component<Props> {

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

       let placeholder = <span className="placeholder-glow"><span className="placeholder placeholder-lg w-100 bg-light rounded" style={{minWidth: "70px"}}></span></span>
        

       return <Container fluid>
            {/* ATTENTION! It's flex-row-REVERSE! Hard to understand! */}
            <Navbar bg="dark" className="navbar-dark rounded"  expand="md"   >
                <Container  fluid d-flex className="flex-row-reverse" >
                    <Navbar.Brand><Image  width="300"   src="/logo.png" alt="AlltidLike" className="logo d-inline-block align-top" /></Navbar.Brand>
                    <Navbar.Toggle aria-controls="topmenu-navbar" /> 

                    <Navbar.Collapse id="topmenu-navbar" >
                        <ul className="navbar-nav me-auto my-2 d-flex text-light align-items-center">                           
                            {links.map((link, index) => {
                                return <li className="nav-item mx-md-1 h4 d-flex flex-row align-items-center">                                 
                                    <NavLink href={link[0]}>
                                        {(link[1]) ? link[1] : placeholder}
                                    </NavLink>
                                    <span className="d-none d-sm-none d-md-block">{(index !== links.length - 1  || selectBox) ? arrow : <></>}</span>
                                </li>
                            })}

                            <li className="nav-item h4 d-flex align-items-center" >
                                {(this.props.data !== undefined) ? selectBox : placeholder}
                            </li>

                            {this.sectionLinksNew().map(link => {
                                    return <li className="nav-item mx-3 d-flex align-items-center h4">
                                            <Nav.Link className="d-flex overflow-text" href={link.href}><strong>{link.text}</strong></Nav.Link>
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