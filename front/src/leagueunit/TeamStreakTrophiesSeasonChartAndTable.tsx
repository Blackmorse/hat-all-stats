import { Box } from "@mui/material";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import Section from "../common/sections/HookSection";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { teamStreakTrophiesChart } from "../rest/clients/LeagueUnitClient";
import { useTranslation } from "react-i18next";
import { TeamStreakTrophiesChart } from "../rest/models/team/TeamStreakTrophies";


export const TeamStreakTrophiesSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Box mt={2}>
		<Section title={t('menu.streak_trophies')}
			element={<LeagueUnitChartAndTable<TeamStreakTrophiesChart>
				initialRequestParams={{
					season: props.levelDataProps.currentSeason()
				}}
				levelDataProps={props.levelDataProps}
				executeRequestCallback={teamStreakTrophiesChart}
				charts={[
					{
						field: (data: TeamStreakTrophiesChart) => data.trophiesNumber,
						fieldFormatted: (data: TeamStreakTrophiesChart) => data.trophiesNumber,
						label: t('table.trophies')
					},
					{
						field: (data: TeamStreakTrophiesChart) => data.numberOfVictories,
						fieldFormatted: (data: TeamStreakTrophiesChart) => data.numberOfVictories,
						label: t('table.victories')
					},
					{
						field: (data: TeamStreakTrophiesChart) => data.numberOfUndefeated,
						fieldFormatted: (data: TeamStreakTrophiesChart) => data.numberOfUndefeated,
						label: t('table.undefeated')
					},
				]}
			/>
			}
		/>
	</Box>
}

