import ax from 'axios';
import { UserContext } from '../models/UserContext';

const axios = ax.create({ baseURL: import.meta.env.VITE_HATTID_SERVER_URL })

export async function userContext(requestToken: string) {
	return await axios.get<UserContext>(`/api/oauth/userContext?request_token=${requestToken}`);
}

export async function requestToken() {
	return await axios.get<string>(`/api/oauth/requestToken`);
}

export async function callback(oauthToken: string, oauthVerifier: string) {
	return await axios.get<UserContext>(`/api/oauth/callback?oauth_token=${oauthToken}&oauth_verifier=${oauthVerifier}`);

}
