import { Box } from "@mui/material";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import Section from "../common/sections/HookSection";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { teamFanclubFlagsChart } from "../rest/clients/LeagueUnitClient";
import { useTranslation } from "react-i18next";
import { TeamFanclubFlagsChart } from "../rest/models/team/TeamFanclubFlags";


export const TeamFanclubFlagsSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Box mt={2}>
		<Section title={t('menu.fanclub_flags')}
			element={<LeagueUnitChartAndTable<TeamFanclubFlagsChart>
				initialRequestParams={{
					season: props.levelDataProps.currentSeason()
				}}
				levelDataProps={props.levelDataProps}
				executeRequestCallback={teamFanclubFlagsChart}
				charts={[
					{
						field: (data: TeamFanclubFlagsChart) => data.fanclubSize,
						fieldFormatted: (data: TeamFanclubFlagsChart) => data.fanclubSize,
						label: t('table.fanclub_size')
					},
					{
						field: (data: TeamFanclubFlagsChart) => data.homeFlags,
						fieldFormatted: (data: TeamFanclubFlagsChart) => data.homeFlags,
						label: t('table.home_flags')
					},
					{
						field: (data: TeamFanclubFlagsChart) => data.awayFlags,
						fieldFormatted: (data: TeamFanclubFlagsChart) => data.awayFlags,
						label: t('table.away_flags')
					},
					{
						field: (data: TeamFanclubFlagsChart) => data.allFlags,
						fieldFormatted: (data: TeamFanclubFlagsChart) => data.allFlags,
						label: t('table.all_flags')
					},
				]}
			/>
			}
		/>
	</Box>
}

