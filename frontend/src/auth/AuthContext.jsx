import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { logout as logoutRequest, refresh as refreshRequest } from '../api/auth'
import { clearAuthHandlers, setAuthHandlers } from '../api/authHandlers'
import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  saveTokens,
} from './authStorage'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(getAccessToken)
  const [refreshToken, setRefreshToken] = useState(getRefreshToken)

  const isAuthenticated = Boolean(accessToken)

  const login = useCallback((authResponse) => {
    saveTokens(authResponse)
    setAccessToken(authResponse.accessToken)
    setRefreshToken(authResponse.refreshToken)
  }, [])

  const logout = useCallback(async () => {
    const currentRefreshToken = getRefreshToken()
    const currentAccessToken = getAccessToken()

    if (currentRefreshToken) {
      try {
        await logoutRequest(currentRefreshToken, currentAccessToken)
      } catch {
        // Déconnexion locale même si l'API échoue
      }
    }

    clearTokens()
    setAccessToken(null)
    setRefreshToken(null)
  }, [])

  const refreshSession = useCallback(async () => {
    const currentRefreshToken = getRefreshToken()

    if (!currentRefreshToken) {
      return false
    }

    try {
      const authResponse = await refreshRequest(currentRefreshToken)
      login(authResponse)
      return true
    } catch {
      clearTokens()
      setAccessToken(null)
      setRefreshToken(null)
      return false
    }
  }, [login])

  useEffect(() => {
    setAuthHandlers({
      refresh: refreshSession,
      logout,
    })

    return () => {
      clearAuthHandlers()
    }
  }, [refreshSession, logout])

  const value = useMemo(
    () => ({
      accessToken,
      refreshToken,
      isAuthenticated,
      login,
      logout,
      refreshSession,
    }),
    [accessToken, refreshToken, isAuthenticated, login, logout, refreshSession],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth doit être utilisé dans un AuthProvider')
  }

  return context
}
