import React from 'react';
import './LeftMenu.css'
import { PagesEnum } from '../enums/PagesEnum'
import { Translation } from 'react-i18next'
import '../../i18n'

interface Props {
    callback: (page: PagesEnum) => void;
    pages: Array<PagesEnum>
}

class LeftMenu extends React.Component<Props, {}> {
    render() {
        return <Translation>{
            (t, { i18n }) =>
            <div className="left_side_inner">
                <div className="left_bar">
                    <header className="left_bar_header">{t('menu.statistics')}</header>

                    {this.props.pages.map(page => {
                        return <button className="left_bar_link" onClick={() => this.props.callback(page)}>{t(page)}</button>
                    })}
                </div>
            </div>
        }
        </Translation>
    }
}

export default LeftMenu