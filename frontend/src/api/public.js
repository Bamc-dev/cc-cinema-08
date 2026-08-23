import { apiFetch } from './client'

export function getPublicMovies(query) {
  const params = query ? `?q=${encodeURIComponent(query)}` : ''
  return apiFetch(`/api/public/movies${params}`)
}

export function getPublicCinemas(query) {
  const params = query ? `?q=${encodeURIComponent(query)}` : ''
  return apiFetch(`/api/public/cinemas${params}`)
}

export function getPublicCinemaToday(cinemaId) {
  return apiFetch(`/api/public/cinemas/${cinemaId}/today`)
}

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
