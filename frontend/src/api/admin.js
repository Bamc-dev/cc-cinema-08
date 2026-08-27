import { apiFetch } from './client'

function buildListPath(resource, page, size, search) {
  const params = search ? `?search=${encodeURIComponent(search)}` : ''
  return `/api/${resource}/list/${page}/${size}${params}`
}

/**
 * Liste paginée d'une ressource admin (GET /api/{resource}/list/{page}/{size}).
 * @param {string} resource Nom de ressource (cinema, room, movie, movie-show, schedule).
 * @param {object} [options] Pagination et recherche.
 * @param {number} [options.page=0] Index de page 0-based.
 * @param {number} [options.size=10] Taille de page.
 * @param {string} [options.search] Filtre texte optionnel.
 * @returns {Promise<{ content: object[], totalElements: number }>} PageDTO backend.
 */
export function listResource(resource, { page = 0, size = 10, search } = {}) {
  return apiFetch(buildListPath(resource, page, size, search || undefined))
}

/**
 * Récupère une ressource admin par identifiant.
 * @param {string} resource Nom de ressource.
 * @param {string|number} id Identifiant.
 * @returns {Promise<object>} Entité trouvée.
 */
export function findResource(resource, id) {
  return apiFetch(`/api/${resource}/find/${id}`)
}

/**
 * Crée une ressource admin.
 * @param {string} resource Nom de ressource.
 * @param {object} body Payload CRUD.
 * @returns {Promise<object>} Entité créée.
 */
export function createResource(resource, body) {
  return apiFetch(`/api/${resource}/admin/create`, {
    method: 'POST',
    body,
  })
}

/**
 * Met à jour une ressource admin.
 * @param {string} resource Nom de ressource.
 * @param {string|number} id Identifiant.
 * @param {object} body Payload CRUD (inclut généralement l'id).
 * @returns {Promise<object>} Entité mise à jour.
 */
export function updateResource(resource, id, body) {
  return apiFetch(`/api/${resource}/admin/update/${id}`, {
    method: 'PUT',
    body,
  })
}

/**
 * Supprime une ressource admin.
 * @param {string} resource Nom de ressource.
 * @param {string|number} id Identifiant.
 * @returns {Promise<null>} Réponse vide en cas de succès.
 */
export function deleteResource(resource, id) {
  return apiFetch(`/api/${resource}/admin/delete/${id}`, {
    method: 'DELETE',
  })
}

/** Facade CRUD pour les cinémas admin. */
export const cinemaApi = {
  list: (options) => listResource('cinema', options),
  find: (id) => findResource('cinema', id),
  create: (body) => createResource('cinema', body),
  update: (id, body) => updateResource('cinema', id, body),
  delete: (id) => deleteResource('cinema', id),
}

/** Facade CRUD pour les salles admin. */
export const roomApi = {
  list: (options) => listResource('room', options),
  find: (id) => findResource('room', id),
  create: (body) => createResource('room', body),
  update: (id, body) => updateResource('room', id, body),
  delete: (id) => deleteResource('room', id),
}

/** Facade CRUD pour les films admin. */
export const movieApi = {
  list: (options) => listResource('movie', options),
  find: (id) => findResource('movie', id),
  create: (body) => createResource('movie', body),
  update: (id, body) => updateResource('movie', id, body),
  delete: (id) => deleteResource('movie', id),
}

/** Facade CRUD pour les séances (film + salle + prix) admin. */
export const movieShowApi = {
  list: (options) => listResource('movie-show', options),
  find: (id) => findResource('movie-show', id),
  create: (body) => createResource('movie-show', body),
  update: (id, body) => updateResource('movie-show', id, body),
  delete: (id) => deleteResource('movie-show', id),
}

/** Facade CRUD pour les horaires de diffusion admin. */
export const scheduleApi = {
  list: (options) => listResource('schedule', options),
  find: (id) => findResource('schedule', id),
  create: (body) => createResource('schedule', body),
  update: (id, body) => updateResource('schedule', id, body),
  delete: (id) => deleteResource('schedule', id),
}
