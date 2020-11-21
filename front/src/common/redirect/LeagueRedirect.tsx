import React from 'react'
import { RouteComponentProps } from 'react-router';


interface Props extends RouteComponentProps {}

class LeagueRedirect extends React.Component<Props> {
    constructor(props: Props) {
        super(props)
        let params = new URLSearchParams(window.location.search);
        let leagueId = params.get('leagueId')
        if (leagueId?.charAt(leagueId.length - 1) === ']') {
            leagueId = leagueId.slice(0, leagueId.length - 1)
        }
        this.props.history.push('/league/' + leagueId)
    }

    render() {
        return <></>
    }
}

export default LeagueRedirect