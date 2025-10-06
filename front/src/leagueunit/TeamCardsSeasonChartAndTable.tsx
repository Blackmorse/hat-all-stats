import { useTranslation } from "react-i18next";
import Section from "../common/sections/HookSection";
import { TeamCardsChart } from "../rest/models/team/TeamCards";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import LeagueUnitChartAndTable from "./seasonchart/LeagueUnitChartAndTable";
import { teamCardsChart } from "../rest/clients/LeagueUnitClient";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";


const TeamCardsSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();

	return <Section title={t('menu.team_cards')}
		element={<LeagueUnitChartAndTable<TeamCardsChart>
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
}

export default TeamCardsSeasonChartAndTable;
