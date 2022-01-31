import React, {useState, useEffect, Fragment} from 'react'
import { YMInitializer } from 'react-yandex-metrika';
import {  Col, Container, Row } from 'react-bootstrap'
import { Link } from 'react-router-dom';
import CountryImage from '../elements/CountryImage';
import './Layout.css'
import {useTranslation} from 'react-i18next';
import { isMobile } from 'react-device-detect'
import Cookies from 'js-cookie';

interface Props {
    content: JSX.Element,
    leftMenu: JSX.Element
    topMenu: JSX.Element
}

const Layout = (props: Props) => {
    const i18n = useTranslation().i18n
    const [ collapsed, setCollapsed ] = useState(isMobile)

    useEffect(() => {
        if(Cookies.get('hattid_language')) {
            i18n.changeLanguage(Cookies.get('hattid_language') || 'en')
        }
    }, [])

    function changeLanguage(lang: string) {
        Cookies.set('hattid_language', lang, { sameSite: "Lax", expires: 180})
        i18n.changeLanguage(lang)
    }

    let leftMenu: JSX.Element
    if (collapsed) {
        leftMenu = <div style={{height: '60px'}}></div>
    } else {
        leftMenu = props.leftMenu
    }
    let arrow = (collapsed) ? <>&#9656;</> : <>&#9666;</>

    let collapsibleLeftMenu =  <div className='mt-3 d-flex flex-row'>
            <button 
                className={'mb-3 shadow border-start border-top border-bottom bg-light text-center hide_left_menu p-0 collapse_button ' + ((collapsed) ? 'border-end rounded' : 'border-end-0 rounded-start')}
                onClick={() => setCollapsed(!collapsed)}
                style={{width: '15px'}}> {arrow}
            </button>
            <div style={{width: '100%'}}>
                {leftMenu}
            </div>
            
        </div>

    let direction = (i18n.language === 'fa') ? 'rtl' : 'ltr'

    let body = (!collapsed) ? <Container d-flex='true' dir={direction} fluid>
        <Row>
            <Col lg={3} md={4} xs={12} sm={12}>
                {collapsibleLeftMenu}
            </Col>
            <Col lg={9} md={8} xs={12} sm={12}>
                {props.content}
            </Col>
        </Row>
    </Container>
    :
     <Container className='d-flex flex-row' fluid>
         <div className='h-25'>{collapsibleLeftMenu}</div>
         <div className='w-100'>{props.content}</div>
     </Container>

    return <Fragment>
                 <header dir={direction}>
                    <YMInitializer accounts={[67069579]} />
                    <Container fluid className='d-flex mb-1'>
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
                            <Link to='#' onClick={() => changeLanguage("en")} className='link'><CountryImage countryId={2} text='en'/></Link>
                            <Link to='#' onClick={() => changeLanguage("es")} className='link'><CountryImage countryId={36} text='es'/></Link>
                            <Link to='#' onClick={() => changeLanguage("de")} className='link'><CountryImage countryId={3} text='de'/></Link>
                            <Link to='#' onClick={() => changeLanguage("hr")} className='link'><CountryImage countryId={58} text='hr'/></Link>
                            <Link to='#' onClick={() => changeLanguage("it")} className='link'><CountryImage countryId={4} text='it'/></Link>
                            <Link to='#' onClick={() => changeLanguage("ru")} className='link'><CountryImage countryId={35} text='ru'/></Link>
                            <Link to='#' onClick={() => changeLanguage("tr")} className='link'><CountryImage countryId={32} text='tr'/></Link>
                            <Link to='#' onClick={() => changeLanguage("fa")} className='link'><CountryImage countryId={85} text='fa'/></Link>
                        </div>
                    </Container>
                    {props.topMenu}
                
                </header>
                 {body}
                 <footer className='container-fluid small-font text-muted d-flex'>
                    <span className='ms-auto me-2'>Powered by React, Scala Play & ClickHouse. <a className="aside_link" target="_tab" href="https://github.com/Blackmorse/hat-all-stats">GitHub</a></span>
                 </footer>  
        </Fragment>
}
 export default Layout
