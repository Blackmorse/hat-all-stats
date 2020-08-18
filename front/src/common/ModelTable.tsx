import React from 'react';
import { LeagueProps } from '../league/League'
import './ModelTable.css'
import StatisticsParameters from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import PageNavigator from '../common/PageNavigator'

interface ModelTableState<T> {
    entities?: Array<T>,
    statisticsParameters: StatisticsParameters,
    isLastPage: boolean
}

abstract class ModelTable<Model> extends React.Component<LeagueProps, ModelTableState<Model>> {

    constructor(props: LeagueProps) {
        super(props)
        this.state={
            isLastPage: false,
            statisticsParameters: {page: 0}
        }
    }

    abstract fetchEntities(leagueId: number, statisticsParameters: StatisticsParameters, callback: (restTableData: RestTableData<Model>) => void): void

    abstract columnHeaders(): JSX.Element

    abstract columnValues(index: number, model: Model): JSX.Element

    componentDidMount() {
        this.fetchEntities(this.props.leagueId, 
            {page: this.state.statisticsParameters.page},
            restTableData => {
                this.setState({
                                entities: restTableData.entities,
                                statisticsParameters: this.state.statisticsParameters,
                                isLastPage: restTableData.isLastPage
                            })}
                            )
    }

    pageSelected(pageNumber: number) {
        this.fetchEntities(this.props.leagueId, 
                            {page: pageNumber}, 
                            restTableData => {
                                this.setState({
                                entities: restTableData.entities,
                                statisticsParameters: {page: pageNumber},
                                isLastPage: restTableData.isLastPage
                            })})
    }

    render() {
        let navigatorProps = {
            pageNumber: this.state.statisticsParameters.page,
            isLastPage: this.state.isLastPage
        }
        return <section className="statistics_section">
            <header className="statistics_header"><span className="statistics_header_triangle">&#x25BC;</span></header>
            <table className="statistics_table">
                <thead>
                    {this.columnHeaders()}
                </thead>
                <tbody>
                    {this.state.entities?.map((entity, index) => 
                        this.columnValues(16 * this.state.statisticsParameters.page + index, entity))}
                </tbody>
            </table>

            <PageNavigator 
                pageNumber={navigatorProps.pageNumber} 
                isLastPage={navigatorProps.isLastPage} 
                linkAction={(pageNumber) => this.pageSelected(pageNumber)}/> 
        </section>
    }
}

export default ModelTable;