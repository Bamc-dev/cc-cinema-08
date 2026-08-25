import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import { SelectedCinemaProvider } from './context/SelectedCinemaContext'
import { SelectedMovieProvider } from './context/SelectedMovieContext'
import ProtectedRoute from './auth/ProtectedRoute'
import AppLayout from './components/AppLayout'
import AdminHomePage from './pages/admin/AdminHomePage'
import AdminCinemasPage from './pages/admin/AdminCinemasPage'
import AdminMoviesPage from './pages/admin/AdminMoviesPage'
import AdminRoomsPage from './pages/admin/AdminRoomsPage'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage'
import ResetPasswordPage from './pages/auth/ResetPasswordPage'
import CinemasPage from './pages/public/CinemasPage'
import CinemaProgramPage from './pages/public/CinemaProgramPage'
import HomePage from './pages/public/HomePage'
import MoviesPage from './pages/public/MoviesPage'
import MovieShowtimesPage from './pages/public/MovieShowtimesPage'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <SelectedCinemaProvider>
          <SelectedMovieProvider>
            <Routes>
            <Route element={<AppLayout />}>
              <Route path="/" element={<HomePage />} />
              <Route path="/cinemas" element={<CinemasPage />} />
              <Route path="/cinemas/:cinemaId" element={<CinemaProgramPage />} />
              <Route path="/films" element={<MoviesPage />} />
              <Route path="/films/:movieId" element={<MovieShowtimesPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/forgot-password" element={<ForgotPasswordPage />} />
              <Route path="/reset-password" element={<ResetPasswordPage />} />

              <Route element={<ProtectedRoute />}>
                <Route path="/admin" element={<AdminHomePage />} />
                <Route path="/admin/cinemas" element={<AdminCinemasPage />} />
                <Route path="/admin/rooms" element={<AdminRoomsPage />} />
                <Route path="/admin/movies" element={<AdminMoviesPage />} />
              </Route>
            </Route>
            </Routes>
          </SelectedMovieProvider>
        </SelectedCinemaProvider>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
