import { getAccessToken } from '../auth/authStorage'
import { shouldAttemptRefresh, triggerLogout, tryRefreshTokens } from './authHandlers'

const API_BASE_URL = import.meta.env.VITE_API_URL ?? ''

export class ApiError extends Error {
  constructor(status, message, data = null) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.data = data
  }
}

async function parseResponse(response) {
  if (response.ok) {
    if (response.status === 204) {
      return null
    }

    return response.json()
  }

  let data = null

  try {
    data = await response.json()
  } catch {
    // Réponse non JSON (ex. 401 sans body)
  }

  const message = data?.message ?? data?.error ?? response.statusText ?? 'Erreur API'

  throw new ApiError(response.status, message, data)
}

export async function apiFetch(path, options = {}) {
  const { token, body, headers = {}, isRetry = false, ...rest } = options

  const accessToken = token === undefined ? getAccessToken() : token

  const requestHeaders = {
    'Content-Type': 'application/json',
    ...headers,
  }

  if (accessToken) {
    requestHeaders.Authorization = `Bearer ${accessToken}`
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...rest,
    headers: requestHeaders,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (response.status === 401 && !isRetry && shouldAttemptRefresh(path) && accessToken) {
    const refreshed = await tryRefreshTokens()

    if (refreshed) {
      return apiFetch(path, { ...options, token: getAccessToken(), isRetry: true })
    }

    await triggerLogout()
  }

  return parseResponse(response)
}
