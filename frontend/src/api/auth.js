import { apiFetch } from './client'

export function login(credentials) {
  return apiFetch('/auth/login', {
    method: 'POST',
    body: credentials,
    token: null,
  })
}

export function register(credentials) {
  return apiFetch('/auth/register', {
    method: 'POST',
    body: credentials,
    token: null,
  })
}

export function refresh(refreshToken) {
  return apiFetch('/auth/refresh', {
    method: 'POST',
    body: { refreshToken },
    token: null,
  })
}

export function logout(refreshToken, accessToken) {
  return apiFetch('/auth/logout', {
    method: 'POST',
    body: { refreshToken },
    token: accessToken ?? undefined,
    isRetry: true,
  })
}

export function forgotPassword(email) {
  return apiFetch('/auth/forgot-password', {
    method: 'POST',
    body: { email },
    token: null,
  })
}

export function resetPassword(payload) {
  return apiFetch('/auth/reset-password', {
    method: 'POST',
    body: payload,
    token: null,
  })
}
