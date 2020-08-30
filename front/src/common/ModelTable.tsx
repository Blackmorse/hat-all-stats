import React from 'react';
import { LeagueProps } from '../league/League'
import './ModelTable.css'
import StatisticsParameters, { SortingDirection, StatsTypeEnum, StatsType } from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import PageNavigator from '../common/PageNavigator'
import Cookies from 'js-cookie'
import PageSizeSelector from './selectors/PageSizeSelector'
import StatsTypeSelector from './selectors/StatsTypeSelector'
import { Translation } from 'react-i18next'
import '../i18n'

interface ModelTableState<T> {
    entities?: Array<T>,
    statisticsParameters: StatisticsParameters,
    isLastPage: boolean,
}

abstract class ModelTable<Model> extends React.Component<LeagueProps, ModelTableState<Model>> {
    private sectionTitle: string;
    private statsTypes: Array<StatsTypeEnum>

    constructor(props: LeagueProps, sectionTitle: string, 
            defaultSortingField: string, defaultStatsType: StatsType,
            statsTypes: Array<StatsTypeEnum>) {
        super(props)
        this.sectionTitle = sectionTitle
        this.statsTypes = statsTypes
        
        let pageSizeString = Cookies.get('hattid_page_size')
        let pageSize = (pageSizeString == null) ? 16 : Number(pageSizeString)
        this.state={
            isLastPage: true,
            statisticsParameters: {
                page: 0,
                pageSize: pageSize,
                sortingField: defaultSortingField,
                sortingDirection: SortingDirection.DESC,
                statsType: defaultStatsType
            }
        }

        this.pageSizeChanged=this.pageSizeChanged.bind(this);
        this.sortingChanged=this.sortingChanged.bind(this);
        this.statTypeChanged=this.statTypeChanged.bind(this);
    }

    abstract fetchEntities(leagueId: number, statisticsParameters: StatisticsParameters, callback: (restTableData: RestTableData<Model>) => void): void

    abstract columnHeaders(): JSX.Element

    abstract columnValues(index: number, model: Model): JSX.Element

    update(statisticsParameters: StatisticsParameters) {
        this.fetchEntities(this.props.leagueData.leagueId,
            statisticsParameters,
            restTableData => this.setState({
                entities: restTableData.entities,
                statisticsParameters: statisticsParameters,
                isLastPage: restTableData.isLastPage
            }))
    }

    componentDidMount() {
        this.update(this.state.statisticsParameters)
    }

    pageSelected(pageNumber: number) {
        let newStatisticsParameters = Object.assign({}, this.state.statisticsParameters)
        newStatisticsParameters.page = pageNumber

        this.update(newStatisticsParameters)
    }

    pageSizeChanged(pageSize: number) {
        let newStatisticsParameters = Object.assign({}, this.state.statisticsParameters)
        newStatisticsParameters.pageSize = pageSize
        newStatisticsParameters.page = 0

        Cookies.set('hattid_page_size', pageSize.toString(), { sameSite: "Lax" })

        this.update(newStatisticsParameters)
    }

    sortingChanged(sortingField: string) {
        let newStatisticsParameters = Object.assign({}, this.state.statisticsParameters)
        
        let newSortingDirection: SortingDirection
        if( this.state.statisticsParameters.sortingField === sortingField ) {
            if ( this.state.statisticsParameters.sortingDirection === SortingDirection.DESC ) {
                newSortingDirection = SortingDirection.ASC
            } else {
                newSortingDirection = SortingDirection.DESC
            }
        } else {
            newSortingDirection = SortingDirection.DESC
        }

        newStatisticsParameters.sortingField = sortingField
        newStatisticsParameters.sortingDirection = newSortingDirection
        this.update(newStatisticsParameters)
    }

    statTypeChanged(statType: StatsType) {
        let newStatisticsParameters = Object.assign({}, this.state.statisticsParameters)
        newStatisticsParameters.statsType = statType

        this.update(newStatisticsParameters)
    }

    render() {
        let navigatorProps = {
            pageNumber: this.state.statisticsParameters.page,
            isLastPage: this.state.isLastPage
        }
        return <Translation> 
        { (t, { i18n }) =>
        <>
            <header className="content_header">{t(this.sectionTitle)}</header>
            <div className="content_body">
                <section className="statistics_section">               
                    
                    <header className="statistics_header"><span className="statistics_header_triangle">&#x25BC;</span></header>
                    
                    <div className="table_settings_div">
                        <StatsTypeSelector  statsTypes={this.statsTypes}
                            currentRound={this.props.leagueData.currentRound}
                            rounds={this.props.leagueData.rounds}
                            selectedStatType={this.state.statisticsParameters.statsType}
                            onChanged={this.statTypeChanged}
                            />
                        <PageSizeSelector 
                            selectedSize={this.state.statisticsParameters.pageSize}
                            linkAction={this.pageSizeChanged}/>
                    </div>
                    <table className="statistics_table">
                        <thead>
                            {this.columnHeaders()}
                        </thead>
                        <tbody>
                            {this.state.entities?.map((entity, index) => 
                                this.columnValues(this.state.statisticsParameters.pageSize * this.state.statisticsParameters.page + index, entity))}
                        </tbody>
                    </table>

                    <PageNavigator 
                        pageNumber={navigatorProps.pageNumber} 
                        isLastPage={navigatorProps.isLastPage} 
                        linkAction={(pageNumber) => this.pageSelected(pageNumber)}/> 
                </section>
            </div>
        </>}
        </Translation>
    }
}

export default ModelTable;