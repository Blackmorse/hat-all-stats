import { Components, createTheme, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TableSortLabel, ThemeProvider } from "@mui/material";
import { useMemo, useState, type JSX } from "react";
import i18n from '../../i18n'
import TeamLink from "../links/TeamLink";
import LeagueUnitLink from "../links/LeagueUnitLink";
import { ChartData } from "../../leagueunit/seasonchart/LeagueUnitChartAndTable";

export const tableComponents: Components = {
	MuiTable: {
		styleOverrides: {
			root: {
				fontSize: '0.875rem', // small class equivalent
				borderCollapse: 'separate',
				borderSpacing: 0,
			}
		},
		defaultProps: {
			size: 'small', // equivalent to table-sm
		}
	},
	MuiTableHead: {
		styleOverrides: {
			root: {
				backgroundColor: '#ffffff', // White background for table header
				borderTop: '1px solid rgba(0, 0, 0, 0.1)', // Thin top border for visual distinction
			}
		}
	},
	MuiTableRow: {
		styleOverrides: {
			root: {
				'&:nth-of-type(odd)': {
					backgroundColor: 'rgba(122, 134, 10, 0.1)', // striped effect - matches $table-striped-bg from SCSS
				},
				'&:hover': {
					backgroundColor: 'rgba(0, 0, 0, 0.075)', // hover effect like Bootstrap
				}
			}
		}
	},
	MuiTableCell: {
		styleOverrides: {
			root: {
				padding: '0.5rem',
				borderBottom: '1px solid rgba(0, 0, 0, 0.1)',
			},
			head: {
				borderBottom: '2px solid rgba(0, 0, 0, 0.2)',
				fontWeight: 'bold',
				color: 'rgba(0, 0, 0, 0.87)',
				backgroundColor: '#ffffff', // White background for header cells
			}
		}
	},
	MuiTableContainer: {
		styleOverrides: {
			root: {
				borderRadius: '0.25rem', // table-rounded equivalent
				overflow: 'hidden',
				marginBottom: '1rem',
			}
		}
	}
};

export const positionsColumn = <T extends unknown>() => {
	return {
		title: '',
		titleAlignCenter: true,
		valueAlignCenter: true,
		value: (_: T, index: number) => index + 1,
	}
}

export const leagueUnitLinkColumn = <T extends ChartData>() => {
	return {
		title: i18n.t('table.league'),
		titleAlignCenter: true,
		valueAlignCenter: true,
		value: (thc: T, _: number) => <LeagueUnitLink
			id={thc.teamSortingKey.leagueUnitId}
			text={thc.teamSortingKey.leagueUnitName}
		/>,
	}
}

export const teamLinkColumn = <T extends ChartData>() => {
	return {
		title: i18n.t('table.team'),
		titleAlignCenter: false,
		valueAlignCenter: false,
		value: (thc: T, _: number) => <TeamLink
			id={thc.teamSortingKey.teamId}
			text={thc.teamSortingKey.teamName}
		/>,
	}
}

const theme = createTheme({
	components: {
		...tableComponents,
	},
});

export interface Column<Data> {
	title: string;
	titleAlignCenter: boolean;
	valueAlignCenter: boolean;
	value: (data: Data, index: number) => number | string | JSX.Element | undefined;
	sorting?: Sorting<Data>;
}

interface Sorting<Data> {
	value: (data: Data) => number | undefined;
}


const MaterialsOfflineTable = <Data extends unknown>({
	data,
	columns
}: {
	data: Data[],
	columns: Column<Data>[]
}) => {
	const [sorting, setSorting] = useState<[Column<Data> | null, 'asc' | 'desc']>([columns.find(c => c.sorting) ?? null, 'desc']);
	const [sortingColumn, direction] = sorting;

	const sortedData = useMemo(() => {
		if (sortingColumn === null || sortingColumn.sorting === undefined) {
			return data;
		}
		const sorted = [...data].sort((a, b) => {
			const aValue = sortingColumn.sorting!.value(a) ?? 0;
			const bValue = sortingColumn.sorting!.value(b) ?? 0;

			if (direction === 'asc') {
				return aValue - bValue;
			} else {
				return bValue - aValue;
			}
		})
		return sorted;
	}, [data, sortingColumn, direction]);

	return <ThemeProvider theme={theme}>
		<TableContainer>
			<Table>
				<TableHead>
					<TableRow>
						{
							columns.map((col, index) => (
								(col.sorting !== undefined) ?
									<TableCell key={index} align={col.titleAlignCenter ? 'center' : 'left'}>
										<TableSortLabel
											active={sortingColumn?.title === col.title}
											direction={direction}
											onClick={() => {
												const newDirection = sortingColumn === col && direction === 'desc' ? 'asc' : 'desc';
												setSorting([col, newDirection]);
											}}
										>
											{col.title}
										</TableSortLabel>
									</TableCell>
									:
									<TableCell key={index} align={col.titleAlignCenter ? 'center' : 'left'}>
										{col.title}
									</TableCell>
							))
						}
					</TableRow>
				</TableHead>
				<TableBody>
					{sortedData.map((row, rowIndex) => (
						<TableRow key={rowIndex}>
							{columns.map((col, colIndex) => (
								<TableCell key={colIndex} align={col.valueAlignCenter ? 'center' : 'left'}>
									{col.value(row, rowIndex)}
								</TableCell>
							))}
						</TableRow>
					))}
				</TableBody>
			</Table>

		</TableContainer>
	</ThemeProvider>
}

export default MaterialsOfflineTable;
