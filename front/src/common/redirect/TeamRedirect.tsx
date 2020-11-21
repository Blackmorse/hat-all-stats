import React from 'react'
import { RouteComponentProps } from 'react-router';

class TeamRedirect extends React.Component<RouteComponentProps> {
    constructor(props: RouteComponentProps) {
        super(props)
        let params = new URLSearchParams(window.location.search);
        let teamId = params.get('teamId')
        this.props.history.push('/team/' + teamId)
    }

    render() {
        return <></>
    }
}

export default TeamRedirect