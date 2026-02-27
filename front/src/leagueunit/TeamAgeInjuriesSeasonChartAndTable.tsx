import { useTranslation } from "react-i18next";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import { Box } from "@mui/material";
import Section from "../common/sections/HookSection";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { TeamAgeInjuryChart } from "../rest/models/team/TeamAgeInjury";
import { teamAgeInjuriesChart } from "../rest/clients/LeagueUnitClient";
import { ageFormatter, injuryFormatter } from "../common/Formatters";

export const TeamAgeInjuriesSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Box mt={2}>
		<Section title={t('menu.team_age_injury')}
			element={<LeagueUnitChartAndTable<TeamAgeInjuryChart>
				initialRequestParams={{
					season: props.levelDataProps.currentSeason(),
				}}
				levelDataProps={props.levelDataProps}
				executeRequestCallback={teamAgeInjuriesChart}
				charts={[
					{
						field: (data: TeamAgeInjuryChart) => data.age,
						fieldFormatted: (data: TeamAgeInjuryChart) => ageFormatter(data.age),
						label: t('table.age'),
						format: { type: 'age' }
					},
					{
						field: (data: TeamAgeInjuryChart) => data.injury,
						fieldFormatted: (data: TeamAgeInjuryChart) => injuryFormatter(data.injury),
						label: t('table.total_injury_weeks')
					},
					{
						field: (data: TeamAgeInjuryChart) => data.injuryCount,
						label: t('table.total_injury_number_abbr')
					},
				]}
			/>}
		/>
	</Box>
}
