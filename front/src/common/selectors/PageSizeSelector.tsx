import React from 'react';
import { Translation } from 'react-i18next'
import './PageSizeSelector.css'
import '../../i18n'

interface PageSizeProperties {
    selectedSize: number,
    linkAction: (pageSize: number) => void
}

class PageSizeSelector extends React.Component<PageSizeProperties> {
    sizes: Array<number> = [8, 16, 32, 64]

    render() {
        return <Translation>{
            (t, { i18n }) =><div className="d-flex flex-row align-items-end">
            <span className="very-small-font ms-auto mx-2 my-xs-2 my-sm-2 my-lg-0 my-md-0">{t('filter.page_size')}</span>
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
    </Translation>
    }
}

export default PageSizeSelector
