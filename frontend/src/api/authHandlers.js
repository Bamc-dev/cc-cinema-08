let refreshHandler = null
let logoutHandler = null
let refreshInFlight = null

export function setAuthHandlers({ refresh, logout }) {
  refreshHandler = refresh
  logoutHandler = logout
}

export function clearAuthHandlers() {
  refreshHandler = null
  logoutHandler = null
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
