import React from 'react';
import { LeagueProps } from '../league/League'
import './ModelTable.css'

interface ModelTableState<T> {
    entities?: Array<T>
}

abstract class ModelTable<Model> extends React.Component<LeagueProps, ModelTableState<Model>> {

    constructor(props: LeagueProps) {
        super(props)
        this.state={}
    }

    abstract fetchEntities(leagueId: number, callback: (entities: Array<Model>) => void): void

    abstract columnHeaders(): JSX.Element

    abstract columnValues(index: number, model: Model): JSX.Element

    componentDidMount() {
        this.fetchEntities(this.props.leagueId, entities => this.setState({entities: entities}))
    }

    render() {
        return <section className="statistics_section">
            <header className="statistics_header"><span className="statistics_header_triangle">&#x25BC;</span></header>
            <table className="statistics_table">
                {this.columnHeaders()}
                <tbody>
                    {this.state.entities?.map((entity, index) => this.columnValues(index, entity))}
                </tbody>
            </table>
        </section>
    }
}

export default ModelTable;