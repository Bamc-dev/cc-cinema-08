import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react'
import { useLocation } from 'react-router-dom'

const SelectedCinemaContext = createContext(null)

const CINEMA_PROGRAM_PATH = /^\/cinemas\/[^/]+$/

/**
 * Mémorise le cinéma choisi pour la page programmation, et le vide à la sortie de /cinemas/:id.
 * @param {{ children: import('react').ReactNode }} props
 */
export function SelectedCinemaProvider({ children }) {
  const location = useLocation()
  const previousPathRef = useRef(location.pathname)
  const [selectedCinema, setSelectedCinema] = useState(null)

  const selectCinema = useCallback((cinema) => {
    setSelectedCinema({
      id: cinema.id,
      name: cinema.name,
      city: cinema.city ?? null,
    })
  }, [])

  const clearSelectedCinema = useCallback(() => {
    setSelectedCinema(null)
  }, [])

  useEffect(() => {
    const previousPath = previousPathRef.current
    const currentPath = location.pathname

    const wasOnProgramPage = CINEMA_PROGRAM_PATH.test(previousPath)
    const isOnProgramPage = CINEMA_PROGRAM_PATH.test(currentPath)

    if (wasOnProgramPage && !isOnProgramPage) {
      clearSelectedCinema()
    }

    previousPathRef.current = currentPath
  }, [location.pathname, clearSelectedCinema])

  const value = useMemo(
    () => ({
      selectedCinema,
      selectCinema,
      clearSelectedCinema,
    }),
    [selectedCinema, selectCinema, clearSelectedCinema],
  )

  return (
    <SelectedCinemaContext.Provider value={value}>{children}</SelectedCinemaContext.Provider>
  )
}

/**
 * Accède au cinéma actuellement sélectionné (programmation publique).
 * @returns {{ selectedCinema: object|null, selectCinema: Function, clearSelectedCinema: Function }}
 */
export function useSelectedCinema() {
  const context = useContext(SelectedCinemaContext)

  if (!context) {
    throw new Error('useSelectedCinema doit être utilisé dans un SelectedCinemaProvider')
  }

  return context
}
