import React from 'react';
import './About.css'
import { PayPalButton } from "react-paypal-button-v2";
import i18n from '../i18n'
import { Link } from 'react-router-dom';
import Section, { SectionState } from '../common/sections/Section';
import { Card } from 'react-bootstrap';

class AboutSection extends React.Component<{}, SectionState> {

    constructor(props: {}) {
        super(props)
        this.state={collapsed: false}
    }

    render(): JSX.Element {
        return  <Card className="mt-3 shadow">
            <Card.Header className="lead">About Hattid</Card.Header>
            <Card.Body>
        <article className="about_section">
        <p>Hattid (AlltidLike) is a tool for providing useful statistics for a <a href="https://hattrick.org" target="_tab">Hattrick</a> site. It consists of various types datasets about players, matches, teams, salaries etc.</p>
        <p>
            This site is divided into 5 levels, so you separately have an access for World, Country, Division (I, II, III ...), League and Team statistics. Currently information is available only for league matches and updates once a week via Hattrick CHPP.
        </p>
        <p>
            If you want to help this project you can <a href="http://www.hattrick.org/goto.ashx?path=/Community/CHPP/ChppProgramDetails.aspx?ApplicationId=5044" target="_tab">rate application at Hattrick</a> and to donate.
        </p>
        <p>
            Start exploring the world of Hattrick statistics from the <Link to="/worldOverview">{i18n.t('overview.world_overview')}</Link> and have fun!
        </p>
        <p>
            This is non-commercial project with no advertisments and It's maintained by my own money. I will appreciate any donations. Thanks!
        </p>
    
    <span>
    <PayPalButton
        amount="5.00"
    
    onSuccess={(details: any, data: any) => {
        alert("Transaction completed by " + details.payer.name.given_name);

    }}
    options={{
        clientId: "AR1D88EuepqIo1C7LI6Qb7W_JTiagLEcyl2nAAVco-YTmOzw_ZvQZ_fyOWWwWcce7XdGH7kzQBDJZGcT"
    }}
/>
    </span>
</article>
</Card.Body>
</Card>
    } 
}

export default AboutSection