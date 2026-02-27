import { useTranslation } from "react-i18next";
import Section from "../common/sections/HookSection"
import LeagueUnitChartAndTable, { genericAvgMerge, genericMaxMerge } from "./seasonchart/LeagueUnitChartAndTable";
import { TeamHatstatsChart } from "../rest/models/team/TeamHatstats";
import { teamHatstatsChart } from "../rest/clients/LeagueUnitClient";
import LeagueUnitLevelDataProps from "./LeagueUnitLevelDataProps";
import { loddarStats } from "../common/Formatters";
import { LevelDataPropsWrapper } from "../common/LevelDataProps";


const TeamHatstatsSeasonChartAndTable = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
	const { t } = useTranslation();
    const teamHatstatsChartNumericFields: (keyof TeamHatstatsChart)[] = ['hatStats', 'midfield', 'defense', 'attack', 'loddarStats'];

	return <Section title={t('team_charts.rating.title')}
                element={<LeagueUnitChartAndTable<TeamHatstatsChart>
                    initialRequestParams={{ season: props.levelDataProps.currentSeason() }}
                    levelDataProps={props.levelDataProps}
                    executeRequestCallback={teamHatstatsChart}
                    avgMerger={(data) => genericAvgMerge(data, teamHatstatsChartNumericFields)}
                    maxMerger={(data) => genericMaxMerge(data, teamHatstatsChartNumericFields)}
                    charts={[
                        {
                            field: (data: TeamHatstatsChart) => data.hatStats,
                            label: t('table.hatstats'),
                        },
                        {
                            field: (data: TeamHatstatsChart) => data.midfield * 3,
                            label: t('table.midfield'),
                        },
                        {
                            field: (data: TeamHatstatsChart) => data.attack,
                            label: t('table.attack'),
                        },
                        {
                            field: (data: TeamHatstatsChart) => data.defense,
                            label: t('table.defense'),
                        },
                        {
                            field: (data: TeamHatstatsChart) => data.loddarStats,
                            fieldFormatted: (data: TeamHatstatsChart) => loddarStats(data.loddarStats),
                            label: t('table.loddar_stats'),
                        },
                    ]}
                />} />
}

export default TeamHatstatsSeasonChartAndTable;
