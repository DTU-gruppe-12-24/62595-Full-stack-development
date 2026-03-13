import type { User } from '@/model/User';
import { getToken, getStoredUser } from '@/services/authService'

export async function apiFetch<ResultType, BodyType = undefined>(url: string, method: "GET" | "POST" | "PUT" | "DELETE" = "GET", body?: BodyType) : Promise<ResultType> {
	const token = getToken()

	return fetch(
		url,
		{
			method,
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				...(token ? { Authorization: `Bearer ${token}` } : {}),
			},
			body: body ? JSON.stringify(body) : undefined
		}
	)
		.then(async (response) => {
			if (response.status < 200 || response.status > 299) throw new Error(await response.text());
			const contentType = response.headers.get("content-type");
			if (contentType == "application/json") return response.json();
			else return response.text();
		})
		.then((result) => {
			return result;
		})
		.catch((error) => {
			throw error;
		});
}

export function getMyUser() {
    const storedUser = getStoredUser();
    if (!storedUser) return null;
    return {
        id: storedUser.userId,
        name: storedUser.name,
        email: storedUser.email,
        passwordHash: ""
    } as User;
}