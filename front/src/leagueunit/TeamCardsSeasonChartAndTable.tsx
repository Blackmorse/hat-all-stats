import { useTranslation } from "react-i18next";
import Section from "../common/sections/HookSection";
import { TeamCardsChart } from "../rest/models/team/TeamCards";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { teamCardsChart } from "../rest/clients/LeagueUnitClient";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";
import { Box } from "@mui/material";


const TeamCardsSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Box mt={2}>
		<Section title={t('menu.team_cards')}
			element={<LeagueUnitChartAndTable<TeamCardsChart>
				initialRequestParams={{ season: props.levelDataProps.currentSeason() }}
				levelDataProps={props.levelDataProps}
				executeRequestCallback={teamCardsChart}
				charts={[
					{
						field: (data: TeamCardsChart) => data.yellowCards,
						label: t('table.yellow_cards'),
					},
					{
						field: (data: TeamCardsChart) => data.redCards,
						label: t('table.red_cards'),
					}
				]}
			/>} />
	</Box>
}

export default TeamCardsSeasonChartAndTable;
