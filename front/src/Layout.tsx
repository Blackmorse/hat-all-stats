import React from 'react';
import './Layout.css'


abstract class Layout<Props, State> extends React.Component<Props, State> {
    
    abstract topMenu(): JSX.Element;

    abstract content(): JSX.Element;

    abstract leftMenu(): JSX.Element
    
    render() {
        return <div className='main_frame'>
            <header className="header">{this.topMenu()}</header>
            <main className="main_content">
                <aside className="left_side">
                    {this.leftMenu()}
                </aside>
                <section className="content">
                    <header className="content_header"></header>
                    <div className="content_body">
                        {this.content()}
                    </div>
                </section>
            </main>
        </div>
    }
}

export default Layout