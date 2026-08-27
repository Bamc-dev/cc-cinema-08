import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
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
  const navigate = useNavigate()
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

  const redirectToLogin = useCallback(() => {
    const pathname = window.location.pathname
    if (pathname === '/login') {
      return
    }

    navigate('/login', {
      replace: true,
      state: { from: { pathname } },
    })
  }, [navigate])

  useEffect(() => {
    setAuthHandlers({
      refresh: refreshSession,
      logout,
      redirectToLogin,
    })

    return () => {
      clearAuthHandlers()
    }
  }, [refreshSession, logout, redirectToLogin])

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
