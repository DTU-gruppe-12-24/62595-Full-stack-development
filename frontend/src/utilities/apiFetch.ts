export async function apiFetch<ResultType, BodyType = undefined>(url: string, method: "GET" | "POST" | "PUT" | "DELETE" = "GET", body?: BodyType) : Promise<ResultType> {
	return fetch(
			url,
			{
				method,
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
				},
				body: body ? JSON.stringify(body) : undefined
			}
		)
		.then(async (response) => {
			if (response.status < 200 || response.status > 299) throw new Error(await response.text());
			return response.json();
		})
		.then((result) => {
			return result;
		})
		.catch((error) => {
			throw error;
		});
}