import { apiFetch } from './client'

/**
 * Liste publique des films, éventuellement filtrée par titre.
 * @param {string} [query] Texte de recherche (paramètre q).
 * @returns {Promise<object[]>} Films à l'affiche.
 */
export function getPublicMovies(query) {
  const params = query ? `?q=${encodeURIComponent(query)}` : ''
  return apiFetch(`/api/public/movies${params}`)
}

/**
 * Liste publique des cinémas, éventuellement filtrée par nom ou ville.
 * @param {string} [query] Texte de recherche (paramètre q).
 * @returns {Promise<object[]>} Cinémas disponibles.
 */
export function getPublicCinemas(query) {
  const params = query ? `?q=${encodeURIComponent(query)}` : ''
  return apiFetch(`/api/public/cinemas${params}`)
}

/**
 * Programmation du jour pour un cinéma (films + séances).
 * @param {string|number} cinemaId Identifiant du cinéma.
 * @returns {Promise<object[]>} Films avec leurs horaires du jour.
 */
export function getPublicCinemaToday(cinemaId) {
  return apiFetch(`/api/public/cinemas/${cinemaId}/today`)
}

/**
 * Séances d'un film, filtrables par date et par cinéma/ville.
 * @param {string|number} movieId Identifiant du film.
 * @param {object} [filters] Filtres optionnels.
 * @param {string} [filters.date] Date au format YYYY-MM-DD.
 * @param {string} [filters.query] Recherche cinéma ou ville (paramètre q).
 * @returns {Promise<object[]>} Séances correspondantes.
 */
export function getPublicMovieShowtimes(movieId, { date, query } = {}) {
  const params = new URLSearchParams()

  if (date) {
    params.set('date', date)
  }

  if (query) {
    params.set('q', query)
  }

  const queryString = params.toString()
  return apiFetch(`/api/public/movies/${movieId}/showtimes${queryString ? `?${queryString}` : ''}`)
}
