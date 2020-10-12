import React from 'react';
import { Link } from 'react-router-dom';
import './TableLink.css'

interface Props {
    id: number,
    name: string
}

class LeagueUnitLink extends React.Component<Props> {
    render() {
        return <Link className="table_link" to={"/leagueUnit/" + this.props.id} >
                {this.props.name}
            </Link>
    }
}

export default LeagueUnitLink