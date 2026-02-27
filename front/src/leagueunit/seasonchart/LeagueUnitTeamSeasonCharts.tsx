import { Box, Tab, Tabs } from "@mui/material";
import TeamSortingKey from "../../rest/models/team/TeamSortingKey";
import LeagueUnitLevelDataProps from "../LeagueUnitLevelDataProps";
import { useState } from "react";
import { ChartConfig, ChartData } from "./LeagueUnitChartAndTable";
import RechartsSeasonChart, { ChartFormat } from "../../common/charts/RechartsSeasonChart";

interface TabPanelProps {
	children?: React.ReactNode;
	index: number;
	value: number;
}

function TabPanel(props: TabPanelProps) {
	const { children, value, index, ...other } = props;

	return (
		<Box
			role="tabpanel"
			hidden={value !== index}
			id={`team-stats-tabpanel-${index}`}
			aria-labelledby={`team-stats-tab-${index}`}
			{...other}
		>
			{value === index && (
				<Box sx={{ p: 3 }}>{children}</Box>
			)}
		</Box>
	);
}

function a11yProps(index: number) {
	return {
		id: `team-stats-tab-${index}`,
		'aria-controls': `team-stats-tabpanel-${index}`,
	};
}


interface ChartEntry<Data extends { round: number } & { teamSortingKey: TeamSortingKey }> {
	season: number,
	round: number,
	teams: Record<string, Data>
}

interface Props<Data extends ChartData> {
	chartEntries: ChartEntry<Data>[];
	leagueUnitLevelProps: LeagueUnitLevelDataProps;
	charts: ChartConfig<Data>[];
	format?: ChartFormat;
}

const chartColors = [
	'#0a720a', // green
	'#1e90ff', // blue
	'#ff6347', // tomato
	'#ffd700', // gold
	'#8a2be2', // blue violet
	'#ff69b4', // hot pink
	'#20b2aa', // light sea green
	'#ff8c00'  // dark orange
];


const LeagueUnitTeamsSeasonCharts = <Data extends ChartData>(props: Props<Data>) => {
	const { chartEntries, leagueUnitLevelProps, charts } = props;

	const [activeTab, setActiveTab] = useState(0);

	const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
		setActiveTab(newValue);
	};

	return <>
		<Tabs
			value={activeTab}
			onChange={handleTabChange}
			aria-label="team stats tabs"
			sx={{ borderBottom: 1, borderColor: 'divider', mb: 2 }}
		> {charts.map((chart, index) => (
			<Tab label={chart.label} key={index} {...a11yProps(index)} />
		))}

		</Tabs>
		{charts.map((chart, index) => {
			const names = [...new Set(chartEntries.flatMap(cd => Object.keys(cd.teams)))];


			const configs = names.map((name, i) => {
				return {
					fieldFunction: (entry: ChartEntry<Data>) => {
						return (entry.teams[name]) ? chart.field(entry.teams[name]) : undefined
					},
					label: name,
					color: chartColors[i % chartColors.length],
					strokeWidth: 1
				}
			})
			return <TabPanel value={activeTab} index={index} key={index}>
				<RechartsSeasonChart<ChartEntry<Data>>
					chartData={chartEntries}
					title={chart.label}
					format={chart.format ?? { type: 'number', decimals: 0 }}
					fieldConfig={configs}
					seasonOffset={leagueUnitLevelProps.seasonOffset()}
					currencyName={leagueUnitLevelProps.currency()}
					currencyRate={leagueUnitLevelProps.currencyRate()}
					legendPosition='right'
					showXAxisRounds
				/>
			</TabPanel>
		})}
	</>
}


export default LeagueUnitTeamsSeasonCharts;
