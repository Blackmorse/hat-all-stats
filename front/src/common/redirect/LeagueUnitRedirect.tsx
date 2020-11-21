import React from 'react'
import { RouteComponentProps } from 'react-router';

class LeagueUnitRedirect extends React.Component<RouteComponentProps> {
    constructor(props: RouteComponentProps) {
        super(props)
        let params = new URLSearchParams(window.location.search);
        let leagueUnitId = params.get('leagueUnitId')
        this.props.history.push('/leagueUnit/' + leagueUnitId)
    }

    render() {
        return <></>
    }
}

export default LeagueUnitRedirect