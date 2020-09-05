import React from 'react';
import './LeftMenu.css'
import { PagesEnum } from '../common/enums/PagesEnum'
import { Translation } from 'react-i18next'
import '../i18n'

interface Props {
    callback: (page: PagesEnum) => void;
}

class LeftMenu extends React.Component<Props, {}> {
    render() {
        return <Translation>{
            (t, { i18n }) =>
            <div className="left_side_inner">
                <div className="left_bar">
                    <header className="left_bar_header">{t('menu.statistics')}</header>

                    <button className="left_bar_link" onClick={() => this.props.callback(PagesEnum.TEAM_HATSTATS)}>{t('menu.best_teams')}</button>

                    <button className="left_bar_link" onClick={() => this.props.callback(PagesEnum.LEAGUE_UNITS)}>{t('menu.best_league_units')}</button>

                    <a className="left_bar_link" href="/#">{t('menu.player_stats')}</a>

                    <a className="left_bar_link" href="/#">{t('menu.team_state')}</a>

                    <a className="left_bar_link" href="/#">{t('menu.player_state')}</a>

                    <a className="left_bar_link" href="/#">{t('menu.formal_team_stats')}</a>
                </div>
            </div>
        }
        </Translation>
    }
}

export default LeftMenu