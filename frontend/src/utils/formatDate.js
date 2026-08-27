import dayjs from 'dayjs'

/**
 * Formate une date pour l'affichage, ou un tiret si absente.
 * @param {string|Date} date Date à formater.
 * @param {string} [format='DD/MM/YYYY'] Format dayjs.
 * @returns {string} Date formatée.
 */
export function formatDate(date, format = 'DD/MM/YYYY') {
  if (!date) {
    return '—'
  }

  return dayjs(date).format(format)
}

/**
 * Formate une heure pour l'affichage, ou un tiret si absente.
 * @param {string|Date} datetime Date-heure à formater.
 * @param {string} [format='HH:mm'] Format dayjs.
 * @returns {string} Heure formatée.
 */
export function formatTime(datetime, format = 'HH:mm') {
  if (!datetime) {
    return '—'
  }

  return dayjs(datetime).format(format)
}

/**
 * Formate une date-heure pour l'affichage, ou un tiret si absente.
 * @param {string|Date} datetime Date-heure à formater.
 * @param {string} [format='DD/MM/YYYY HH:mm'] Format dayjs.
 * @returns {string} Date-heure formatée.
 */
export function formatDateTime(datetime, format = 'DD/MM/YYYY HH:mm') {
  if (!datetime) {
    return '—'
  }

  return dayjs(datetime).format(format)
}
