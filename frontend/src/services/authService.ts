import { apiFetch } from "@/utilities/apiFetch"

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export interface AuthResponse {
    token: string
    userId: string
    name: string
    email: string
}

// Token helpers

export function getToken(): string | null {
    return localStorage.getItem('auth_token')
}

export function setToken(token: string): void {
    localStorage.setItem('auth_token', token)
}

export function clearToken(): void {
    localStorage.removeItem('auth_token')
    localStorage.removeItem('auth_user')
}

export function getStoredUser(): Omit<AuthResponse, 'token'> | null {
    const raw = localStorage.getItem('auth_user')
    return raw ? JSON.parse(raw) : null
}

function storeAuth(res: AuthResponse): void {
    setToken(res.token)
    const { token: _, ...user } = res
    localStorage.setItem('auth_user', JSON.stringify(user))
}

// API calls

export async function register(name: string, email: string, password: string): Promise<AuthResponse> {
    const res = await apiFetch<AuthResponse, { name: string, email: string, password: string }>('/api/auth/register', "POST", { name, email, password })
    storeAuth(res)
    return res
}

export async function login(email: string, password: string): Promise<AuthResponse> {
    const res = await apiFetch<AuthResponse, { email: string, password: string }>('/api/auth/login', "POST", { email, password })
    storeAuth(res)
    return res
}

export async function logout(): Promise<void> {
    const token = getToken()
    if (token) {
        await apiFetch(`${BASE_URL}/api/auth/logout`, 'POST').catch(() => {})
    }
    clearToken()
}

export function isAuthenticated(): boolean {
    // Get token
    const token = getToken()

    // If there is no token, the user is not authenticated
    if (!token) return false

    // Check if the token is expired. If it is, clear it and return false. If it's not, return true.
    try {
        const parts = token.split('.')
        if (parts.length !== 3) { clearToken(); return false }
        const rawPayload = parts[1] as string // Typescript got upset when I tried to inline this into the JSON.parse call
        const payload = JSON.parse(atob(rawPayload))
        const expiresAt = payload.exp * 1000 // Convert from ms to s
        if (Date.now() >= expiresAt) {
            clearToken()
            return false
        }
        return true
    } catch {
        clearToken()
        return false
    }
}
