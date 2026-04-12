export interface UserContext {
	userId: number,
	name: string,
	teams: {
		teamId: number,
		teamName: string
	}[]
}
