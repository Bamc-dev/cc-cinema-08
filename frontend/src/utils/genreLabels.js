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

export function formatGenre(genre) {
  return GENRE_LABELS[genre] ?? genre
}

export const MOVIE_GENRES = Object.keys(GENRE_LABELS)

export function getGenreOptions() {
  return MOVIE_GENRES.map((value) => ({
    label: GENRE_LABELS[value],
    value,
  }))
}
