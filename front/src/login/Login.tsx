import { type JSX } from "react"
import { useEffect, useState } from "react"
import { useSearchParams } from "react-router-dom"
import Layout from "../common/layouts/Layout"
import WorldLeftLoadingMenu from "../world/WorldLeftLoadingMenu"
import WorldLeftMenu from "../world/WorldLeftMenu"
import WorldTopMenu from "../world/WorldTopMenu"
import { LoadingEnum } from "../common/enums/LoadingEnum"
import { getWorldData } from "../rest/clients/LevelDataClient"
import { Success } from "../rest/models/Https"
import WorldLevelDataProps from "../world/WorldLevelDataProps"
import { callback } from "../rest/clients/AuthClient"

export const Login = () => {
	const [error, setError] = useState<boolean>(false)
	const [levelProps, setLevelProps] = useState<WorldLevelDataProps | undefined>(undefined)

	const [searchParams] = useSearchParams();
	const oauthToken = searchParams.get('oauth_token');
	const oauthVerifier = searchParams.get('oauth_verifier');

	let errorPopup: JSX.Element | null = null
	if (!oauthToken || !oauthVerifier || error) {
		errorPopup = <div className="error_popup">
			<img src="/warning.gif" className="warning_img" alt="warning" />
			<span>
				Something wrong with the OAuth process. Please try again.
			</span>
		</div>
	}

	useEffect(() => {
		getWorldData((payload) => {
			if (payload.loadingEnum === LoadingEnum.OK) {
				setLevelProps((payload as Success<WorldLevelDataProps>).model)
				if (oauthToken && oauthVerifier) {
					callback(oauthToken, oauthVerifier).then(response => {
						if (response.status === 200) {
							localStorage.setItem('request_token', oauthToken)
							window.location.href = window.location.origin + '/team/' + response.data.teams[0].teamId
						} else {
							setError(true)
						}
					}).catch(() => {
						setError(true)
					})
				}
			} else {
				setError(true)
			}
		})
	}, [])
	return <Layout
		topMenu={<WorldTopMenu levelProps={levelProps} />}
		leftMenu={<>
			<WorldLeftLoadingMenu worldLevelDataProps={levelProps} />
			<WorldLeftMenu worldLevelDataProps={levelProps} />
		</>}
		content={<>{errorPopup}</>}
	/>
}
