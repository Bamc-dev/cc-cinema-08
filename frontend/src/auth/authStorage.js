const ACCESS_TOKEN_KEY = 'cinema_access_token'
const REFRESH_TOKEN_KEY = 'cinema_refresh_token'

/**
 * @returns {string|null} Access token JWT stocké, ou null.
 */
export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY)
}

/**
 * @returns {string|null} Refresh token stocké, ou null.
 */
export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

/**
 * Persiste la paire de jetons dans le localStorage.
 * @param {{ accessToken: string, refreshToken: string }} tokens Jetons renvoyés par l'API auth.
 */
export function saveTokens({ accessToken, refreshToken }) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
}

/**
 * Efface les jetons du localStorage (déconnexion locale).
 */
export function clearTokens() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}
