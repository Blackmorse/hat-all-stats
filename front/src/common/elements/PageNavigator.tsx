import React from 'react';

interface PageNavigatorProps {
    pageNumber: number,
    isLastPage: boolean
    linkAction:(pageNumber: number) => void
}

class PageNavigator extends React.Component<PageNavigatorProps> {
    
    render() {
        let previousArrow;
        let previousLink;
        if(this.props.pageNumber > 0) {
            previousLink = <li className="page-item" onClick={() => this.props.linkAction(this.props.pageNumber - 1)}>
                    <button className='page-link'>{this.props.pageNumber}</button>
                </li>
            previousArrow = <li className="page-item" onClick={() => this.props.linkAction(this.props.pageNumber - 1)}>
                    <button className='page-link'>&laquo;</button>
                </li>
        }

        let nextLink = null
        let nextArrow = null
        if(!this.props.isLastPage) {
            nextLink = <li className="page-item" onClick={() => this.props.linkAction(this.props.pageNumber + 1)}>
                    <button className='page-link'>{this.props.pageNumber + 2}</button>
                </li>
            nextArrow = <li className="page-item" onClick={() => this.props.linkAction(this.props.pageNumber + 1)}>
                    <button className='page-link'>&raquo;</button>
                </li>
        }
        return <nav aria-label="Navigation through table pages">
            <ul className="pagination justify-content-center">
                {previousArrow}
                {previousLink}
                <li className="page-item active">
                    <button className='page-link'>{this.props.pageNumber + 1}</button>
                </li>

                {nextLink}
                {nextArrow}
            </ul>
        </nav>
    }
}

export default PageNavigator