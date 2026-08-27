const GENRE_LABELS = {
  ACTION: 'Action',
  ADVENTURE: 'Aventure',
  COMEDY: 'Comédie',
  DRAMA: 'Drame',
  FANTASY: 'Fantastique',
  HORROR: 'Horreur',
  MYSTERY: 'Mystère',
  ROMANCE: 'Romance',
  SCIENCE_FICTION: 'Science-fiction',
}

/**
 * Traduit un code de genre API (enum) en libellé français.
 * @param {string} genre Code genre (ex. SCIENCE_FICTION).
 * @returns {string} Libellé affiché, ou le code brut si inconnu.
 */
export function formatGenre(genre) {
  return GENRE_LABELS[genre] ?? genre
}

/** Codes de genre reconnus par l'API (clés de GENRE_LABELS). */
export const MOVIE_GENRES = Object.keys(GENRE_LABELS)

/**
 * Options { label, value } pour les filtres et formulaires de genre.
 * @returns {{ label: string, value: string }[]}
 */
export function getGenreOptions() {
  return MOVIE_GENRES.map((value) => ({
    label: GENRE_LABELS[value],
    value,
  }))
}
