import { useTranslation } from "react-i18next";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import Section from "../common/sections/HookSection";
import { teamSalaryTsiChart } from "../rest/clients/LeagueUnitClient";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { TeamSalaryTSIChart } from "../rest/models/team/TeamSalaryTSI";
import { commasSeparated } from "../common/Formatters";
import { Box } from "@mui/material";


export const TeamSalaryTsiSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Box mt={2}>
		<Section title={t('menu.player_salary_tsi')}
			element={<LeagueUnitChartAndTable<TeamSalaryTSIChart>
				initialRequestParams={{
					season: props.levelDataProps.currentSeason(),
					playedInLastMatch: false,
					excludeZeroTsi: true,
				}}
				levelDataProps={props.levelDataProps}
				executeRequestCallback={teamSalaryTsiChart}
				charts={[
					{
						field: (data: TeamSalaryTSIChart) => data.tsi,
						fieldFormatted: (data: TeamSalaryTSIChart) => commasSeparated(data.tsi),
						label: t('table.tsi')
					},
					{
						field: (data: TeamSalaryTSIChart) => data.salary,
						fieldFormatted: (data: TeamSalaryTSIChart) => commasSeparated(data.salary),
						label: t('table.salary') + ', ' + props.levelDataProps.currency()
					},
					{
						field: (data: TeamSalaryTSIChart) => data.playersCount,
						label: t('menu.players')
					},
					{
						field: (data: TeamSalaryTSIChart) => data.avgSalary,
						fieldFormatted: (data: TeamSalaryTSIChart) => commasSeparated(data.avgSalary),
						label: t('table.average_salary') + ', ' + props.levelDataProps.currency()
					},
					{
						field: (data: TeamSalaryTSIChart) => data.avgTsi,
						fieldFormatted: (data: TeamSalaryTSIChart) => commasSeparated(data.avgTsi),
						label: t('table.average_tsi')
					},
					{
						field: (data: TeamSalaryTSIChart) => data.salaryPerTsi,
						fieldFormatted: (data: TeamSalaryTSIChart) => commasSeparated(data.salaryPerTsi),
						label: t('table.salary_per_tsi') + ', ' + props.levelDataProps.currency()
					},
				]}
			/>
			}
		/>
	</Box>
}
