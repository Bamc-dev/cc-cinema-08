let refreshHandler = null
let logoutHandler = null
let redirectToLoginHandler = null
let refreshInFlight = null

/**
 * Enregistre les callbacks d'auth utilisés par le client HTTP (évite un cycle d'imports).
 * @param {object} handlers Callbacks fournis par AuthProvider.
 * @param {() => Promise<boolean>} handlers.refresh Tente un refresh de session.
 * @param {() => Promise<void>} handlers.logout Déconnexion locale (et API).
 * @param {() => void} handlers.redirectToLogin Redirige vers /login.
 */
export function setAuthHandlers({ refresh, logout, redirectToLogin }) {
  refreshHandler = refresh
  logoutHandler = logout
  redirectToLoginHandler = redirectToLogin
}

/**
 * Réinitialise les callbacks et le refresh en cours (démontage du AuthProvider).
 */
export function clearAuthHandlers() {
  refreshHandler = null
  logoutHandler = null
  redirectToLoginHandler = null
  refreshInFlight = null
}

const NO_REFRESH_PATHS = [
  '/auth/login',
  '/auth/register',
  '/auth/refresh',
  '/auth/forgot-password',
  '/auth/reset-password',
]

/**
 * Indique si un 401/403 sur ce chemin peut déclencher un refresh de jeton.
 * @param {string} path Chemin de la requête API.
 * @returns {boolean} false pour les endpoints d'authentification eux-mêmes.
 */
export function shouldAttemptRefresh(path) {
  return !NO_REFRESH_PATHS.some((authPath) => path.startsWith(authPath))
}

/**
 * @param {number} status Code HTTP.
 * @returns {boolean} true pour 401 ou 403.
 */
export function isAuthFailureStatus(status) {
  return status === 401 || status === 403
}

/**
 * Lance un unique refresh concurrent (dédupliqué via refreshInFlight).
 * @returns {Promise<boolean>} true si de nouveaux jetons ont été obtenus.
 */
export async function tryRefreshTokens() {
  if (!refreshHandler) {
    return false
  }

  if (!refreshInFlight) {
    refreshInFlight = refreshHandler().finally(() => {
      refreshInFlight = null
    })
  }

  return refreshInFlight
}

/**
 * Exécute le callback de déconnexion s'il est enregistré.
 * @returns {Promise<void>}
 */
export async function triggerLogout() {
  if (logoutHandler) {
    await logoutHandler()
  }
}

/**
 * Redirige vers /login via le callback enregistré par AuthProvider.
 */
export function redirectToLogin() {
  if (redirectToLoginHandler) {
    redirectToLoginHandler()
  }
}
