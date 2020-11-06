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
            <aside className="language_links">
                <button className="language_link_button" onClick={(e) => i18n.changeLanguage("en")}>en</button>
                <button className="language_link_button" onClick={(e) => i18n.changeLanguage("ru")}>ru</button>
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
        </div>
        }
        </Translation>
    }
}

export default Layout