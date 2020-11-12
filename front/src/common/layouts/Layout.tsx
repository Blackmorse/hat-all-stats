import React from 'react';
import './Layout.css'
import { Translation } from 'react-i18next'
import '../../i18n'

abstract class Layout<Props, State> extends React.Component<Props, State> {
    
    abstract topMenu(): JSX.Element;

    abstract content(): JSX.Element;

    abstract leftMenu(): JSX.Element

    render() {
        return <Translation>
        { (t, { i18n }) => 
        <div className='main_frame'>
            <aside className="top_links">
                <span className="suggestions_reports">
                    Any suggestions/bugs? <a className="aside_link" target="_tab" href="https://www.hattrick.org/goto.ashx?path=/MyHattrick/Inbox/?actionType=newMail%26userId=4040806">Write to me at Hattrick</a>
                </span>
                <span className="language_links">
                    <button className="language_link_button" onClick={(e) => i18n.changeLanguage("en")}>en</button>
                    <button className="language_link_button" onClick={(e) => i18n.changeLanguage("ru")}>ru</button>
                </span>
            </aside>
            <header className="header">{this.topMenu()}</header>
            <main className="main_content">
                <aside className="left_side">
                    {this.leftMenu()}
                </aside>
                <section className="content">
                    {this.content()}
                  
                </section>
            </main>
            <footer className="credentials">
                Powered by React, Scala Play & ClickHouse. <a className="aside_link" target="_tab" href="https://github.com/Blackmorse/hat-all-stats">GitHub</a>
            </footer>
        </div>
        }
        </Translation>
    }
}

export default Layout