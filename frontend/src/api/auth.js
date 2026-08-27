import { apiFetch } from './client'

/**
 * Authentifie un utilisateur et récupère les jetons JWT.
 * @param {{ email: string, password: string }} credentials Identifiants de connexion.
 * @returns {Promise<{ accessToken: string, refreshToken: string }>} Réponse d'authentification.
 */
export function login(credentials) {
  return apiFetch('/auth/login', {
    method: 'POST',
    body: credentials,
    token: null,
  })
}

/**
 * Crée un compte utilisateur et récupère les jetons JWT.
 * @param {{ email: string, password: string }} credentials Email et mot de passe.
 * @returns {Promise<{ accessToken: string, refreshToken: string }>} Réponse d'authentification.
 */
export function register(credentials) {
  return apiFetch('/auth/register', {
    method: 'POST',
    body: credentials,
    token: null,
  })
}

/**
 * Renouvelle la paire de jetons à partir du refresh token.
 * @param {string} refreshToken Jeton de rafraîchissement actuel.
 * @returns {Promise<{ accessToken: string, refreshToken: string }>} Nouveaux jetons.
 */
export function refresh(refreshToken) {
  return apiFetch('/auth/refresh', {
    method: 'POST',
    body: { refreshToken },
    token: null,
  })
}

/**
 * Invalide le refresh token côté serveur (déconnexion).
 * @param {string} refreshToken Jeton à révoquer.
 * @param {string} [accessToken] Access token pour authentifier l'appel.
 * @returns {Promise<null>} Réponse vide en cas de succès.
 */
export function logout(refreshToken, accessToken) {
  return apiFetch('/auth/logout', {
    method: 'POST',
    body: { refreshToken },
    token: accessToken ?? undefined,
    isRetry: true,
  })
}

/**
 * Demande l'envoi d'un lien de réinitialisation de mot de passe.
 * @param {string} email Adresse email du compte.
 * @returns {Promise<{ expiresAt?: string }>} Infos du lien envoyé.
 */
export function forgotPassword(email) {
  return apiFetch('/auth/forgot-password', {
    method: 'POST',
    body: { email },
    token: null,
  })
}

/**
 * Définit un nouveau mot de passe à partir du jeton reçu par email.
 * @param {{ token: string, newPassword: string }} payload Jeton et nouveau mot de passe.
 * @returns {Promise<null>} Réponse vide en cas de succès.
 */
export function resetPassword(payload) {
  return apiFetch('/auth/reset-password', {
    method: 'POST',
    body: payload,
    token: null,
  })
}
