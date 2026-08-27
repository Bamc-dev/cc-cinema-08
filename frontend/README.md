# Frontend — Cinema Gestion

SPA Vite + React 19 + Ant Design. Consomme l’API Spring Boot (port 5151).

## Scripts

```bash
npm install
npm run dev      # http://localhost:5173
npm run build
npm run preview
npm run lint
```

## Configuration

| Variable | Rôle |
|----------|------|
| `VITE_API_URL` | URL du backend **sans** slash final. Vide en dev = requêtes relatives, proxy Vite vers `http://localhost:5151`. |

Fichiers : `.env.example`, `.env.development`. Proxy défini dans `vite.config.js` (`/api` et `/auth`).

## Routes

| Chemin | Accès | Page |
|--------|-------|------|
| `/` | Public | Accueil |
| `/cinemas` | Public | Liste des cinémas |
| `/cinemas/:cinemaId` | Public | Programme du jour (sélection via contexte) |
| `/films` | Public | Liste des films |
| `/films/:movieId` | Public | Horaires d’un film (sélection via contexte) |
| `/login`, `/register` | Invité | Connexion / inscription |
| `/forgot-password`, `/reset-password` | Public | Mot de passe oublié (`?token=` sur reset) |
| `/admin` | JWT | Hub admin |
| `/admin/cinemas`, `/rooms`, `/movies`, `/movie-shows`, `/schedules` | JWT | CRUD |

`ProtectedRoute` redirige vers `/login` s’il n’y a pas d’access token. Pas de RBAC par rôle côté UI.

Les pages détail cinéma / film exigent une sélection préalable (`SelectedCinemaContext` / `SelectedMovieContext`). Un rechargement direct de l’URL renvoie à la liste.

## Auth et API

- `auth/AuthContext.jsx` : session (login, logout, refresh), enregistrement des handlers pour le client HTTP
- `auth/authStorage.js` : access / refresh tokens dans `localStorage`
- `api/client.js` (`apiFetch`) : Bearer, parse JSON, retry après 401/403 via refresh (single-flight), sinon logout
- `api/public.js`, `api/auth.js`, `api/admin.js` : appels REST

## Dossiers `src/`

```
api/          client HTTP + endpoints
auth/         contexte, garde, stockage tokens
components/   layout, cartes, shell admin
context/      cinéma / film sélectionnés
pages/        public, auth, admin
utils/        dates, libellés de genres
```
