import { apiFetch } from './client'

function buildListPath(resource, page, size, search) {
  const params = search ? `?search=${encodeURIComponent(search)}` : ''
  return `/api/${resource}/list/${page}/${size}${params}`
}

export function listResource(resource, { page = 0, size = 10, search } = {}) {
  return apiFetch(buildListPath(resource, page, size, search || undefined))
}

export function findResource(resource, id) {
  return apiFetch(`/api/${resource}/find/${id}`)
}

export function createResource(resource, body) {
  return apiFetch(`/api/${resource}/admin/create`, {
    method: 'POST',
    body,
  })
}

export function updateResource(resource, id, body) {
  return apiFetch(`/api/${resource}/admin/update/${id}`, {
    method: 'PUT',
    body,
  })
}

export function deleteResource(resource, id) {
  return apiFetch(`/api/${resource}/admin/delete/${id}`, {
    method: 'DELETE',
  })
}

export const cinemaApi = {
  list: (options) => listResource('cinema', options),
  find: (id) => findResource('cinema', id),
  create: (body) => createResource('cinema', body),
  update: (id, body) => updateResource('cinema', id, body),
  delete: (id) => deleteResource('cinema', id),
}

export const roomApi = {
  list: (options) => listResource('room', options),
  find: (id) => findResource('room', id),
  create: (body) => createResource('room', body),
  update: (id, body) => updateResource('room', id, body),
  delete: (id) => deleteResource('room', id),
}

export const movieApi = {
  list: (options) => listResource('movie', options),
  find: (id) => findResource('movie', id),
  create: (body) => createResource('movie', body),
  update: (id, body) => updateResource('movie', id, body),
  delete: (id) => deleteResource('movie', id),
}
