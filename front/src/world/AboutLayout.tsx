import React from 'react';
import { RouteComponentProps } from 'react-router';
import Layout from '../common/layouts/Layout';
import { getWorldData } from '../rest/Client'
import WorldData from '../rest/models/leveldata/WorldData';
import WorldTopMenu from './WorldTopMenu'
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu'
import WorldLeftMenu from './WorldLeftMenu'
import './About.css'
import '../i18n'
import AboutSection from './AboutSection'

interface Props extends RouteComponentProps<{}> {}

interface State {
    levelData?: WorldData
}

class AboutLayout extends Layout<Props, State> {
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
        return <AboutSection />
    }
    leftMenu(): JSX.Element {
        return <>
            <WorldLeftLoadingMenu worldData={this.state.levelData}/>
            <WorldLeftMenu worldData={this.state.levelData}/>
        </>
    }
}

export default AboutLayout










