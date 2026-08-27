import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react'
import { useLocation } from 'react-router-dom'

const SelectedMovieContext = createContext(null)

const MOVIE_SHOWTIMES_PATH = /^\/films\/[^/]+$/

/**
 * Mémorise le film choisi pour la page séances, et le vide à la sortie de /films/:id.
 * @param {{ children: import('react').ReactNode }} props
 */
export function SelectedMovieProvider({ children }) {
  const location = useLocation()
  const previousPathRef = useRef(location.pathname)
  const [selectedMovie, setSelectedMovie] = useState(null)

  const selectMovie = useCallback((movie) => {
    setSelectedMovie({
      id: movie.id,
      title: movie.title,
      genre: movie.genre ?? null,
      releaseDate: movie.releaseDate ?? null,
    })
  }, [])

  const clearSelectedMovie = useCallback(() => {
    setSelectedMovie(null)
  }, [])

  useEffect(() => {
    const previousPath = previousPathRef.current
    const currentPath = location.pathname

    const wasOnShowtimesPage = MOVIE_SHOWTIMES_PATH.test(previousPath)
    const isOnShowtimesPage = MOVIE_SHOWTIMES_PATH.test(currentPath)

    if (wasOnShowtimesPage && !isOnShowtimesPage) {
      clearSelectedMovie()
    }

    previousPathRef.current = currentPath
  }, [location.pathname, clearSelectedMovie])

  const value = useMemo(
    () => ({
      selectedMovie,
      selectMovie,
      clearSelectedMovie,
    }),
    [selectedMovie, selectMovie, clearSelectedMovie],
  )

  return (
    <SelectedMovieContext.Provider value={value}>{children}</SelectedMovieContext.Provider>
  )
}

/**
 * Accède au film actuellement sélectionné (séances publiques).
 * @returns {{ selectedMovie: object|null, selectMovie: Function, clearSelectedMovie: Function }}
 */
export function useSelectedMovie() {
  const context = useContext(SelectedMovieContext)

  if (!context) {
    throw new Error('useSelectedMovie doit être utilisé dans un SelectedMovieProvider')
  }

  return context
}
