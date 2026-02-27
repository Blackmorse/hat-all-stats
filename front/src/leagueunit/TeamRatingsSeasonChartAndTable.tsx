import { useTranslation } from "react-i18next";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import { Box } from "@mui/material";
import Section from "../common/sections/HookSection";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { teamRatingsChart } from "../rest/clients/LeagueUnitClient";
import { TeamRatingChart } from "../rest/models/team/TeamRating";
import { ratingFormatter } from "../common/Formatters";


export const TeamRatingsSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Box mt={2}>
		<Section title={t('menu.team_ratings')}
			element={<LeagueUnitChartAndTable<TeamRatingChart>
				initialRequestParams={{
					season: props.levelDataProps.currentSeason(),
					playedInLastMatch: false,
					excludeZeroTsi: true,
				}}
				levelDataProps={props.levelDataProps}
				executeRequestCallback={teamRatingsChart}
				charts={[
					{
						field: (data: TeamRatingChart) => data.rating,
						fieldFormatted: (data: TeamRatingChart) => ratingFormatter(data.rating),
						label: t('table.rating'),
						format: { type: 'rating' }
					},
					{
						field: (data: TeamRatingChart) => data.ratingEndOfMatch,
						fieldFormatted: (data: TeamRatingChart) => ratingFormatter(data.ratingEndOfMatch),
						label: t('table.rating_end_of_match'),
						format: { type: 'rating' }
					},
				]}
			/>}

		/>

	</Box>
}
