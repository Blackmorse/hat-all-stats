import React from 'react';
import { RouteComponentProps } from 'react-router';
import Layout from '../Layout';
import { getDivisionLevelData } from '../rest/Client' 
import DivisionLevelData from '../rest/models/DivisionLevelData';
import DivisionLevelTopMenu from './DivisionLevelTopMenu'
import LeftMenu from '../menu/LeftMenu'

interface MatchParams {
    leagueId: string,
    divisionLevel: string
}

interface State {
    divisionLevelData?: DivisionLevelData
}

interface Props extends RouteComponentProps<MatchParams>{}

class DivisionLevel extends Layout<Props, State> {

    constructor(props: Props) {
        super(props)
        this.state = {}
    
    }

    componentDidMount() {
        getDivisionLevelData(Number(this.props.match.params.leagueId), Number(this.props.match.params.divisionLevel),
            divisionLevelData => this.setState({divisionLevelData: divisionLevelData}))
    }

    topMenu(): JSX.Element {
        return <DivisionLevelTopMenu divisionLevelData={this.state.divisionLevelData} />
    }
    
    content(): JSX.Element {
        return <></>
    }

    leftMenu(): JSX.Element {
        return <LeftMenu callback={page =>{}}/>
    }

}

export default DivisionLevel