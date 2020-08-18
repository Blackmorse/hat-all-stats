import React from 'react';
import './PageNavigator.css'

interface PageNavigatorProps {
    pageNumber: number,
    isLastPage: boolean
    linkAction:(pageNumber: number) => void
}

class PageNavigator extends React.Component<PageNavigatorProps> {
    
    render() {
        let previousArrow = null;
        let previousLink = null
        if(this.props.pageNumber > 0) {
            previousLink = <button className="a_page page_link_active" 
                                onClick={() => this.props.linkAction(this.props.pageNumber - 1)}>
                    <div className="page_link">
                        {this.props.pageNumber}
                    </div>
                </button>
            previousArrow = <button className="a_page page_link_active"
                                onClick={() => this.props.linkAction(this.props.pageNumber - 1)}>
                    <div className="page_link">
                        &laquo;
                    </div>
                </button>
        }

        let nextLink = null
        let nextArrow = null
        if(!this.props.isLastPage) {
            nextLink = <button className="a_page page_link_active"
                            onClick={() => this.props.linkAction(this.props.pageNumber + 1)}>
                <div className="page_link">
                    {this.props.pageNumber + 2}
                </div>
            </button>
            nextArrow = <button className="a_page page_link_active"
                            onClick={() => this.props.linkAction(this.props.pageNumber + 1)}>
                <div className="page_link">
                    &raquo;
                </div>
            </button>
        }
        return <nav className="page_navigation">
            {previousArrow}
            {previousLink}
            
            <button className="a_page page_link_not_active">
                <div className="page_link ">
                    {this.props.pageNumber + 1}
                </div>
            </button>

            {nextLink}
            {nextArrow}
        </nav>
    }
}

export default PageNavigator