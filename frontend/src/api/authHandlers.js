let refreshHandler = null
let logoutHandler = null
let redirectToLoginHandler = null
let refreshInFlight = null

export function setAuthHandlers({ refresh, logout, redirectToLogin }) {
  refreshHandler = refresh
  logoutHandler = logout
  redirectToLoginHandler = redirectToLogin
}

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

export function shouldAttemptRefresh(path) {
  return !NO_REFRESH_PATHS.some((authPath) => path.startsWith(authPath))
}

export function isAuthFailureStatus(status) {
  return status === 401 || status === 403
}

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

export async function triggerLogout() {
  if (logoutHandler) {
    await logoutHandler()
  }
}

export function redirectToLogin() {
  if (redirectToLoginHandler) {
    redirectToLoginHandler()
  }
}
