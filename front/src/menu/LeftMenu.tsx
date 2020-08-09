import React from 'react';
import './LeftMenu.css'
import LeaguePage from '../league/League';

interface Props {
    callback: (page: LeaguePage) => void;
}

class LeftMenu extends React.Component<Props, {}> {
    render() {
        return <div className="left_side_inner">
            <div className="left_bar">
                <header className="left_bar_header">menu.statistics</header>

                <button className="left_bar_link" onClick={() => this.props.callback(LeaguePage.TEAM_HATSTATS)}>menu.best_teams</button>

                <button className="left_bar_link" onClick={() => this.props.callback(LeaguePage.LEAGUE_UNITS)}>menu.best_league_units</button>

                <a className="left_bar_link" href="/#">menu.player_stats</a>

                <a className="left_bar_link" href="/#">menu.team_state</a>

                <a className="left_bar_link" href="/#">menu.player_state</a>

                <a className="left_bar_link" href="/#">menu.formal_team_stats</a>
            </div>
        </div>
    }
}

export default LeftMenu