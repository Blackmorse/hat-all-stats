import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Tab, Tabs, Box } from '@mui/material';
import { TeamHatstatsChart } from '../../../rest/models/team/TeamHatstats';
import LeagueUnitLevelDataProps from '../../LeagueUnitLevelDataProps';
import RechartsSeasonChart from '../../../common/charts/RechartsSeasonChart';

interface ChartEntry {
	season: number,
	round: number,
	teams: Record<string, TeamHatstatsChart>
}

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

interface TeamHatstatsChartsProps {
    chartEntries: ChartEntry[];
    configsFactory: (fieldFunction: (thc: TeamHatstatsChart) => number | undefined) => any[];
    leagueUnitLevelProps: LeagueUnitLevelDataProps;
}

const TeamHatstatsCharts = (props: TeamHatstatsChartsProps) => {
    const [activeTab, setActiveTab] = useState(0);
	const { t } = useTranslation();

	const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
		setActiveTab(newValue);
	};

    return <>
        <Tabs
            value={activeTab}
            onChange={handleTabChange}
            aria-label="team stats tabs"
            sx={{ borderBottom: 1, borderColor: 'divider', mb: 2 }}

        >
            <Tab label={t('table.hatstats')} {...a11yProps(0)} />
            <Tab label={t('table.midfield')} {...a11yProps(1)} />
            <Tab label={t('table.defense')} {...a11yProps(2)} />
            <Tab label={t('table.attack')} {...a11yProps(3)} />
        </Tabs>

        <TabPanel value={activeTab} index={0}>
            <RechartsSeasonChart
                chartData={props.chartEntries}
                title={t('table.hatstats')}
                format={{ type: 'number', decimals: 0 }}
                fieldConfig={props.configsFactory(thc => thc.hatStats)}
                seasonOffset={props.leagueUnitLevelProps.seasonOffset()}
                legendPosition='right'
                showXAxisRounds
            />
        </TabPanel>
        <TabPanel value={activeTab} index={1}>
            <RechartsSeasonChart
                chartData={props.chartEntries}
                title={t('table.midfield')}
                format={{ type: 'number', decimals: 0 }}
                fieldConfig={props.configsFactory(thc => thc.midfield * 3)}
                seasonOffset={props.leagueUnitLevelProps.seasonOffset()}
                legendPosition='right'
                showXAxisRounds
            />
        </TabPanel>
        <TabPanel value={activeTab} index={2}>
            <RechartsSeasonChart
                chartData={props.chartEntries}
                title={t('table.defense')}
                format={{ type: 'number', decimals: 0 }}
                fieldConfig={props.configsFactory(thc => thc.defense)}
                seasonOffset={props.leagueUnitLevelProps.seasonOffset()}
                legendPosition='right'
                showXAxisRounds
            />
        </TabPanel>
        <TabPanel value={activeTab} index={3}>
            <RechartsSeasonChart
                chartData={props.chartEntries}
                title={t('table.attack')}
                format={{ type: 'number', decimals: 0 }}
                fieldConfig={props.configsFactory(thc => thc.attack)}
                seasonOffset={props.leagueUnitLevelProps.seasonOffset()}
                legendPosition='right'
                showXAxisRounds
            />
        </TabPanel>
    </>
}

export default TeamHatstatsCharts;
