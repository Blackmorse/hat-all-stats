import React from 'react';
import { RouteComponentProps } from 'react-router';
import Layout from '../common/layouts/Layout';
import { getWorldData } from '../rest/Client'
import WorldData from '../rest/models/leveldata/WorldData';
import WorldTopMenu from './WorldTopMenu'
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu'
import WorldLeftMenu from './WorldLeftMenu'
import '../common/sections/StatisticsSection.css'
import { Link } from 'react-router-dom';
import { PayPalButton } from "react-paypal-button-v2";
import './About.css'
import { Translation } from 'react-i18next'
import '../i18n'

interface Props extends RouteComponentProps<{}> {}

interface State {
    levelData?: WorldData
}

class About extends Layout<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state = {}

        this.leagueIdSelected=this.leagueIdSelected.bind(this)
    }

    componentDidMount() {
        getWorldData(worldData => {
            this.setState({levelData: worldData})
        }, () => {})
    }

    leagueIdSelected(leagueId: number) {
        this.props.history.push('/league/' + leagueId)
    }

    topMenu(): JSX.Element {
        return <WorldTopMenu worldData={this.state.levelData} 
            callback={this.leagueIdSelected}/>
    }
    content(): JSX.Element {
        return <Translation>{
            (t, { i18n }) => <section className="statistics_section">
                    <header className="statistics_header">
                    <span className="statistics_header_triangle">&#x25BC; About Hattid</span>
                </header>
                <article className="about_section">
                    <p>Hattid (AlltidLike) is a tool for providing useful statistics for a <a href="https://hattrick.org" target="_tab">Hattrick</a> site. It consists of various types datasets about players, matches, teams, salaries etc.</p>
                    <p>
                        This site is divided into 5 levels, so you separately have an access for World, Country, Division (I, II, III ...), League and Team statistics. Currently information is available only for league matches and updates once a week via Hattrick CHPP.
                    </p>
                    <p>
                        If you want to help this project you can <a href="http://www.hattrick.org/goto.ashx?path=/Community/CHPP/ChppProgramDetails.aspx?ApplicationId=5044" target="_tab">rate application at Hattrick</a> and to donate.
                    </p>
                    <p>
                        Start exploring the world of Hattrick statistics from the <Link to="/worldOverview">{t('overview.world_overview')}</Link> and have fun!
                    </p>
                    <p>
                        This is non-commercial project with no advertisments and It's maintained by my own money. I will appreciate any donations. Thanks!
                    </p>
                
                <p>
                <PayPalButton
                    amount="5.00"
                
                onSuccess={(details: any, data: any) => {
                    alert("Transaction completed by " + details.payer.name.given_name);

                }}
                options={{
                    clientId: "AR1D88EuepqIo1C7LI6Qb7W_JTiagLEcyl2nAAVco-YTmOzw_ZvQZ_fyOWWwWcce7XdGH7kzQBDJZGcT"
                }}
            />
                </p>
            </article>
            </section>
        }</Translation>
    }
    leftMenu(): JSX.Element {
        return <>
            <WorldLeftLoadingMenu worldData={this.state.levelData}/>
            <WorldLeftMenu worldData={this.state.levelData}/>
        </>
    }
}

export default About










