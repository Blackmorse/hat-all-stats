import React from 'react';
import './PageSizeSelector.css'

interface PageSizeProperties {
    selectedSize: number,
    linkAction: (pageSize: number) => void
}

class PageSizeSelector extends React.Component<PageSizeProperties> {
    sizes: Array<number> = [8, 16, 32, 64]

    render() {
        return <div className="page_size">
            <span className="page_size_title">page_size:</span>
            {this.sizes.map(size => {
                return <button 
                        key={"page_size_button_" + size}
                        className={(size === this.props.selectedSize) ? "page_size_number page_size_number_selected" : "page_size_number page_size_number_link"}
                        onClick={(size !== this.props.selectedSize) ? () => this.props.linkAction(size): undefined}
                    >
                    {size}
                </button>
            })}
        </div>
    }
}

export default PageSizeSelector