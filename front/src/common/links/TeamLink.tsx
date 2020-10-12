import React from 'react';
import { Link } from 'react-router-dom';
import './TableLink.css'


interface Props {
    id: number,
    name: string,
    callback?: () => void
}

class TeamLink extends React.Component<Props> {
    render() {
        return <Link className="table_link" to={"/team/" + this.props.id} onClick={this.props.callback}>
            {this.props.name}
        </Link>
    }
}

export default TeamLink