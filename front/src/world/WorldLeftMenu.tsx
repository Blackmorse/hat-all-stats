import React from 'react';
import WorldData from '../rest/models/leveldata/WorldData'
import { Translation } from 'react-i18next'
import '../i18n'
import LeagueLink from '../common/links/LeagueLink';
import './WorldLeftMenu.css'
import '../common/menu/LeftMenu.css'

interface Props {
    worldData?: WorldData
}

class WorldLeftMenu extends React.Component<Props, {}> {
    constructor(props: Props) {
        super(props)
        this.state={}
    }

    render() {
        return <Translation>{
            (t, { i18n }) =>
            <div className="left_side_inner">
            <div className="left_bar">
                <header className="left_bar_header">{t('world.countries')}</header>
                <section className="left_bar_links scrolled">
                    {this.props.worldData?.countries.map(leagueInfo => {
                        return <LeagueLink tableLink={false} id={leagueInfo[0]} name={leagueInfo[1]} />
                    })}
                </section>
            </div>
        </div>
    }
    </Translation>
    }
}

export default WorldLeftMenu
