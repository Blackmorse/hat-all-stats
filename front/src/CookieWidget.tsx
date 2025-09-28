import React, { type JSX } from 'react'
import { Translation } from 'react-i18next'
import './i18n'
import './CookieWidget'
import Cookies from 'js-cookie'
import './CookieWidget.css'

interface State {
    cookiesAccepted: boolean
}

class CookieWidget extends React.Component<{}, State> {
    constructor(props: {}) {
        super(props)
        this.state={cookiesAccepted: (Cookies.get("cookies_accepted") === 'true')}

        this.onClick=this.onClick.bind(this)
    }

    onClick() {
        Cookies.set('cookies_accepted', 'true', { sameSite: "Lax", expires: 180 })
        this.setState({cookiesAccepted: true})
    }

    render() {
        let cw: JSX.Element = <></>
        if(this.state.cookiesAccepted !== true) {
            cw = <Translation>{
            (t) => <div id="cookies">
            <header className="cookie_header">{t('cookies.header')}</header>
            <article className="cookie_article">{t('cookies.content')}</article>
            <button id="cookie_yes" onClick={this.onClick}>{t('cookies.accept')}</button>
        </div>
        } 
        </Translation>
        }

    return <>{cw}</>
    }
}

export default CookieWidget
