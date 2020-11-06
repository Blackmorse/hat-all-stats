import React from 'react'
import WorldData from '../rest/models/leveldata/WorldData';
import { getWorldData } from '../rest/Client'
import Layout from '../Layout';
import WorldLeftMenu from './WorldLeftMenu'
import WorldTopMenu from './WorldTopMenu'
import { RouteComponentProps } from 'react-router';
import OverviewPage from './OverviewPage';
import ModelTableWorldProps from './ModelTableWorldProps'


interface Props extends RouteComponentProps<{}>{}

interface State {
    levelData?: WorldData
}

class World extends Layout<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state = {}

        this.leagueIdSelected=this.leagueIdSelected.bind(this)
    }

    componentDidMount() {
        getWorldData(worldData => {
            this.setState({levelData: worldData})
        })
    }

    leagueIdSelected(leagueId: number) {
        this.props.history.push('/league/' + leagueId)
    }

    topMenu(): JSX.Element {
        return <WorldTopMenu worldData={this.state.levelData} 
            callback={this.leagueIdSelected}/>
    }

    content(): JSX.Element {
        if(this.state.levelData) {
            let props = new ModelTableWorldProps(this.state.levelData)
            return <OverviewPage modelTableProps={props} />
        } else {
            return <></>
        }
    }

    leftMenu(): JSX.Element {
        return <WorldLeftMenu worldData={this.state.levelData}/>
    }
}

export default World