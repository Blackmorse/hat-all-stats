import React from 'react';
import './Layout.css'
import { Translation } from 'react-i18next'
import '../../i18n'
import i18n from '../../i18n';
import Cookies from 'js-cookie';
import { YMInitializer } from 'react-yandex-metrika';
import {  Col, Container, Row } from 'react-bootstrap'
import CountryImage from '../elements/CountryImage';
import { Link } from 'react-router-dom';

abstract class Layout<Props, State> extends React.Component<Props, State & {collapsed: boolean}> {
    
    abstract topMenu(): JSX.Element;

    abstract content(): JSX.Element;

    abstract leftMenu(): JSX.Element

    collapse = () => {
        let newState = {
            ...this.state,
            collapsed: !this.state.collapsed
        }

        this.setState(newState)
    }

    collapsibleLeftMenu(): JSX.Element {

        let leftMenu: JSX.Element

        if (this.state.collapsed) {
            leftMenu = <div style={{height: '60px'}}></div>
        } else {
            
            leftMenu = this.leftMenu()
        }

        let arrow = (this.state.collapsed) ? <>&#9656;</> : <>&#9666;</>

        return <div className='mt-3 d-flex flex-row'>
                <button 
                    className={'mb-3 shadow border-start border-top border-bottom bg-light text-center hide_left_menu p-0 collapse_button ' + ((this.state.collapsed) ? 'border-end rounded' : 'border-end-0 rounded-start')}
                    onClick={this.collapse}
                    style={{width: '15px'}}> {arrow}
                    {/* <button ></button> */}
                </button>
                <div style={{width: '100%'}}>
                    {/* {this.leftMenu()} */}
                    {leftMenu}
                </div>
                
            </div>
    }

    private changeLanguage(lang: string) {
        Cookies.set('hattid_language', lang, { sameSite: "Lax", expires: 180})
        i18n.changeLanguage(lang)
    }

    render() {
        if(Cookies.get('hattid_language')) {
            this.changeLanguage(Cookies.get('hattid_language') || 'en')
        }

        let body = (!this.state.collapsed) ? <Container  d-flex fluid>
        <Row>
            <Col lg={3} md={4} xs={4}>
                {this.collapsibleLeftMenu()}
            </Col>
            <Col lg={9} md={8} xs={8}>
                {this.content()}
            </Col>
        </Row>

     </Container> :
     <Container className='d-flex flex-row' fluid>
         <div className='h-25'>{this.collapsibleLeftMenu()}</div>
         <div className='w-100'>{this.content()}</div>
     </Container>

        return <Translation>
        { (t, { i18n }) => 
             <>
                 <header>
                    <YMInitializer accounts={[67069579]} />
                    <Container fluid style={{height: '25px'}} className='d-flex mb-1'>
                        <div className='me-auto ms-2'>
                            Follow me: 
                            <a className='ms-1' target='_tab' href='https://www.hattrick.org/goto.ashx?path=/MyHattrick/Inbox/?actionType=newMail%26userId=4040806'>
                                <img src='/hattrick_logo.png' alt='Hattrick' style={{height: '25px'}}/>
                            </a>
                            <a target='_tab' href='https://github.com/Blackmorse/hat-all-stats'>
                                <img src='/GitHub-logo.png' alt='GitHub' style={{height: '25px'}}/>
                            </a>
                            <a href='https://t.me/hattid_ht' target='_tab'>
                                <img src='/telegram_logo.png' alt='Telegram' style={{height: '25px'}}/>
                            </a>
                        </div>

                        <div className='ms-auto me-2'>
                            <Link to='#' onClick={(e) => this.changeLanguage("en")} className='link'><CountryImage countryId={2} text='en'/></Link>
                            <Link to='#' onClick={(e) => this.changeLanguage("es")} className='link'><CountryImage countryId={36} text='es'/></Link>
                            <Link to='#' onClick={(e) => this.changeLanguage("de")} className='link'><CountryImage countryId={3} text='de'/></Link>
                            <Link to='#' onClick={(e) => this.changeLanguage("hr")} className='link'><CountryImage countryId={58} text='hr'/></Link>
                            <Link to='#' onClick={(e) => this.changeLanguage("it")} className='link'><CountryImage countryId={4} text='it'/></Link>
                            <Link to='#' onClick={(e) => this.changeLanguage("ru")} className='link'><CountryImage countryId={35} text='ru'/></Link>
                            <Link to='#' onClick={(e) => this.changeLanguage("tr")} className='link'><CountryImage countryId={32} text='tr'/></Link>
                            <Link to='#' onClick={(e) => this.changeLanguage("fa")} className='link'><CountryImage countryId={85} text='fa'/></Link>
                        </div>
                    </Container>
                    {this.topMenu()}
                
                </header>
                 {body}
                 <footer className='container-fluid small-font text-muted d-flex'>
                    <span className='ms-auto me-2'>Powered by React, Scala Play & ClickHouse. <a className="aside_link" target="_tab" href="https://github.com/Blackmorse/hat-all-stats">GitHub</a></span>
                 </footer>  
        </>
        }
        </Translation>
    }
}

export default Layout