import React from 'react';
import { Link } from 'react-router-dom';
import './TableLink.css'

interface Props {
    id: number,
    name: string,
    tableLink: boolean
}


class LeagueLink extends React.Component<Props> {
    render() {
        return <Link className={(this.props.tableLink) ? "table_link" : "left_bar_link page"} to={"/league/" + this.props.id}>
            {this.props.name}
        </Link>
    }
}

export default LeagueLink