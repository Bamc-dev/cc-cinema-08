import { getAccessToken } from '../auth/authStorage'
import {
  isAuthFailureStatus,
  redirectToLogin,
  shouldAttemptRefresh,
  triggerLogout,
  tryRefreshTokens,
} from './authHandlers'

const API_BASE_URL = import.meta.env.VITE_API_URL ?? ''

/**
 * Erreur HTTP levée par le client API (statut, message et corps de réponse).
 */
export class ApiError extends Error {
  /**
   * @param {number} status Code HTTP de la réponse.
   * @param {string} message Message d'erreur affichable.
   * @param {object|null} [data=null] Corps JSON de la réponse, s'il existe.
   */
  constructor(status, message, data = null) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.data = data
  }
}

/**
 * Interprète une Response fetch : JSON en succès, ApiError en échec.
 * @param {Response} response Réponse HTTP brute.
 * @returns {Promise<object|null>} Corps JSON, ou null pour un 204.
 */
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

/**
 * Effectue un appel HTTP vers l'API, avec JWT et refresh automatique.
 * @param {string} path Chemin relatif (ex. /api/public/movies).
 * @param {object} [options={}] Options fetch étendues.
 * @param {string|null} [options.token] Access token ; undefined = token stocké, null = aucun.
 * @param {object} [options.body] Corps JSON à sérialiser.
 * @param {boolean} [options.isRetry=false] true si déjà retenté après un refresh.
 * @returns {Promise<object|null>} Corps JSON de la réponse.
 */
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

  if (
    isAuthFailureStatus(response.status) &&
    !isRetry &&
    shouldAttemptRefresh(path) &&
    accessToken
  ) {
    const refreshed = await tryRefreshTokens()

    if (refreshed) {
      return apiFetch(path, { ...options, token: getAccessToken(), isRetry: true })
    }

    await triggerLogout()
    redirectToLogin()
    throw new ApiError(response.status, 'Session expirée. Veuillez vous reconnecter.')
  }

  return parseResponse(response)
}
