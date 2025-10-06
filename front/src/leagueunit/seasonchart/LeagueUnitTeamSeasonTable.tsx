import MaterialsOfflineTable, { leagueUnitLinkColumn, positionsColumn, teamLinkColumn } from "../../common/tables/MaterialsOfflineTable";
import LeagueUnitLevelDataProps from "../LeagueUnitLevelDataProps";
import { ChartConfig, ChartData } from "./LeagueUnitChartAndTable";


interface Props<Data extends ChartData> {
	dataToShow: Record<string, Data>;
    charts: ChartConfig<Data>[]
}

const LeagueUnitTeamSeasonTable = <Data extends ChartData>(props: Props<Data>) => {
	const { dataToShow, charts } = props;

	const valueColumns = charts.map(chart => ({
		title: chart.label,
		titleAlignCenter: true,
		valueAlignCenter: true,
		value: (thc: Data, _: number) => {
			return (chart.fieldFormatted) ? chart.fieldFormatted(thc) : chart.field(thc);
		},
		sorting: {
			value: (thc: Data) => {
				return chart.field(thc) ?? 0;
			},
		},
	}));


	return <MaterialsOfflineTable<Data>
		data={Object.values(dataToShow)}
		columns={[
			positionsColumn<Data>(),
			teamLinkColumn<Data>(),
			leagueUnitLinkColumn<Data>(),
			...valueColumns
		]}
	/>;
}

export default LeagueUnitTeamSeasonTable;
