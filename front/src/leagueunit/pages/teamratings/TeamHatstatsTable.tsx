import MaterialsOfflineTable, { leagueUnitLinkColumn, positionsColumn, teamLinkColumn } from '../../../common/tables/MaterialsOfflineTable';
import { TeamHatstatsChart } from '../../../rest/models/team/TeamHatstats';
import { useTranslation } from 'react-i18next';
import { loddarStats } from '../../../common/Formatters';

interface TeamHatstatsTableProps {
    data: TeamHatstatsChart[];
}



const TeamHatstatsTable = (props: TeamHatstatsTableProps) => {
    const { t } = useTranslation();

    return <MaterialsOfflineTable<TeamHatstatsChart> data={props.data} columns={[
        positionsColumn<TeamHatstatsChart>(),
        teamLinkColumn<TeamHatstatsChart>(),
        leagueUnitLinkColumn<TeamHatstatsChart>(),
        {
            title: t('table.hatstats'),
            titleAlignCenter: true,
            valueAlignCenter: true,
            value: (thc, _) => thc.hatStats,
            sorting: {
                value: thc => thc.hatStats,
            }
        },
        {
            title: t('table.midfield'),
            titleAlignCenter: true,
            valueAlignCenter: true,
            value: (thc, _) => thc.midfield * 3,
            sorting: {
                value: thc => thc.midfield * 3,
            }
        },
        {
            title: t('table.defense'),
            titleAlignCenter: true,
            valueAlignCenter: true,
            value: (thc, _) => thc.defense,
            sorting: {
                value: thc => thc.defense,
            }
        },
        {
            title: t('table.attack'),
            titleAlignCenter: true,
            valueAlignCenter: true,
            value: (thc, _) => thc.attack,
            sorting: {
                value: thc => thc.attack,
            }
        },
        {
            title: t('table.loddar_stats'),
            titleAlignCenter: true,
            valueAlignCenter: true,
            value: (thc, _) => loddarStats(thc.loddarStats),
            sorting: {
                value: thc => thc.loddarStats,
            },
        },
    ]}

    />
}

export default TeamHatstatsTable;
