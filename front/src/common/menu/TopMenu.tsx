import React from 'react'
import { Link } from 'react-router-dom'
import { Navbar, Container, Image, Nav, NavLink} from 'react-bootstrap'

interface Props<Data> {
    selectBox?: JSX.Element 
    links: Array<TopMenuLink>
    sectionLinks: Array<{href: string, text: string}>
    externalLink?: JSX.Element
    data?: Data
}

export interface TopMenuLink {
    href: string
    content?: string
    beforeLink?: JSX.Element
    afterLink?: JSX.Element
}

const TopMenu = <Data extends {}>(props: Props<Data>) => {
   let selectBox = props.selectBox
   let links = props.links
   let arrow = <>&#8674;</>

   let placeholder = <span className="placeholder-glow"><span className="placeholder placeholder-lg w-100 bg-light rounded" style={{minWidth: "70px"}}></span></span>
    

   return <Container fluid>
        {/* ATTENTION! It's flex-row-REVERSE! Hard to understand! */}
        <Navbar bg="dark" className='navbar-dark rounded'  expand="md" >
            <Container  fluid d-flex='true' className="flex-row-reverse" >
                <Navbar.Brand><Link to='/about'><Image  width="300"   src="/logo.png" alt="AlltidLike" className="logo d-inline-block align-top rounded" /></Link></Navbar.Brand>
                <Navbar.Toggle aria-controls="topmenu-navbar" /> 

                <Navbar.Collapse id='topmenu-navbar' >
                    <ul className='navbar-nav me-auto d-flex text-light align-items-lg-center'>                           
                        {links.map((link, index) => {
                            return <li key={'top_nav_' + index} className='nav-item mx-md-1 h4 d-flex flex-row align-items-center mb-0'>                                 
                                {link.beforeLink}
                                <NavLink href={link.href} >
                                    {(link.content) ? link.content : placeholder}
                                </NavLink>
                                {link.afterLink}
                                {(index === links.length - 1) ? <span className='me-2' >{props.externalLink}</span> : <></>}
                                <span className="d-none d-sm-none d-md-block">{(index !== links.length - 1  || selectBox) ? arrow : <></>}</span>
                            </li>
                        })}

                        <li className='nav-item h4 d-flex align-items-center mb-0' >
                            {(props.data !== undefined) ? selectBox : placeholder}
                        </li>

                        {props.sectionLinks.map((link, index) => {
                                return <li key={'section_links_' + index} className='nav-item mx-3 d-flex align-items-center h4'>
                                        <Nav.Link className='d-flex overflow-text' href={link.href}><strong>{link.text}</strong></Nav.Link>
                                    </li>
                            })}
                    </ul>
                </Navbar.Collapse>                   
            </Container>
        </Navbar>
    </Container>
}

export default TopMenu
