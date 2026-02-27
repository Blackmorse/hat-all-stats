import { Box } from "@mui/material";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import Section from "../common/sections/HookSection";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { teamPowerRatingsChart } from "../rest/clients/LeagueUnitClient";
import { TeamPowerRatingChart } from "../rest/models/team/TeamPowerRating";
import { useTranslation } from "react-i18next";


export const TeamPowerRatingsSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Box mt={2}>
		<Section title={t('team_charts.tab.power_rating')}
			element={<LeagueUnitChartAndTable<TeamPowerRatingChart>
				initialRequestParams={{
					season: props.levelDataProps.currentSeason()
				}}
				levelDataProps={props.levelDataProps}
				executeRequestCallback={teamPowerRatingsChart}
				charts={[
					{
						field: (data: TeamPowerRatingChart) => data.powerRating,
						fieldFormatted: (data: TeamPowerRatingChart) => data.powerRating,
						label: t('team_charts.tab.power_rating')
					},
				]}
			/>
			}
		/>
	</Box>
}
