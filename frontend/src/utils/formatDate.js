import dayjs from 'dayjs'

export function formatDate(date, format = 'DD/MM/YYYY') {
  if (!date) {
    return '—'
  }

  return dayjs(date).format(format)
}

export function formatTime(datetime, format = 'HH:mm') {
  if (!datetime) {
    return '—'
  }

  return dayjs(datetime).format(format)
}

export function formatDateTime(datetime, format = 'DD/MM/YYYY HH:mm') {
  if (!datetime) {
    return '—'
  }

  return dayjs(datetime).format(format)
}
