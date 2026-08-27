import { useEffect, useMemo, useState } from 'react'
import { Alert, Button, Col, Empty, Input, Row, Spin, Tag, Typography } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import { getPublicMovies } from '../../api/public'
import { ApiError } from '../../api/client'
import MovieCard from '../../components/MovieCard'
import { getGenreOptions } from '../../utils/genreLabels'

const { Title, Paragraph, Text } = Typography
const { Search } = Input
const { CheckableTag } = Tag

const SEARCH_DEBOUNCE_MS = 300
const genreOptions = getGenreOptions()

/**
 * Catalogue public des films : recherche par titre et filtres par genre.
 */
function MoviesPage() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')
  const [selectedGenres, setSelectedGenres] = useState([])

  const toggleGenre = (genre) => {
    setSelectedGenres((current) =>
      current.includes(genre) ? current.filter((value) => value !== genre) : [...current, genre],
    )
  }

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search.trim())
    }, SEARCH_DEBOUNCE_MS)

    return () => clearTimeout(timer)
  }, [search])

  useEffect(() => {
    let cancelled = false

    async function loadMovies() {
      setLoading(true)
      setError(null)

      try {
        const data = await getPublicMovies(debouncedSearch || undefined)
        if (!cancelled) {
          setMovies(data)
        }
      } catch (err) {
        if (!cancelled) {
          const errorMessage =
            err instanceof ApiError ? err.message : 'Impossible de charger les films'
          setError(errorMessage)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadMovies()

    return () => {
      cancelled = true
    }
  }, [debouncedSearch])

  const filteredMovies = useMemo(() => {
    if (selectedGenres.length === 0) {
      return movies
    }

    return movies.filter((movie) => selectedGenres.includes(movie.genre))
  }, [movies, selectedGenres])

  const hasActiveFilters = debouncedSearch.length > 0 || selectedGenres.length > 0

  return (
    <>
      <Title level={2}>Films à l&apos;affiche</Title>
      <Paragraph type="secondary">
        Recherchez un titre, filtrez par genre, puis consultez les séances.
      </Paragraph>

      <Search
        placeholder="Titre du film..."
        allowClear
        enterButton={<SearchOutlined />}
        size="large"
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        style={{ maxWidth: 480, marginTop: 24, marginBottom: 24 }}
      />

      <div style={{ marginBottom: 32 }}>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: 16,
            marginBottom: 12,
            flexWrap: 'wrap',
          }}
        >
          <Text strong>Filtrer par genre</Text>
          {selectedGenres.length > 0 && (
            <Button type="link" size="small" onClick={() => setSelectedGenres([])}>
              Effacer les filtres
            </Button>
          )}
        </div>

        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 10 }}>
          {genreOptions.map(({ label, value }) => (
            <CheckableTag
              key={value}
              checked={selectedGenres.includes(value)}
              onChange={() => toggleGenre(value)}
              style={{
                padding: '6px 16px',
                fontSize: 14,
                borderRadius: 20,
                cursor: 'pointer',
              }}
            >
              {label}
            </CheckableTag>
          ))}
        </div>
      </div>

      {loading && (
        <div style={{ textAlign: 'center', padding: 48 }}>
          <Spin size="large" />
        </div>
      )}

      {!loading && error && (
        <Alert
          type="error"
          message="Impossible d'afficher les films"
          description="Réessayez dans un instant."
          showIcon
        />
      )}

      {!loading && !error && filteredMovies.length === 0 && (
        <Empty
          description={
            hasActiveFilters
              ? 'Aucun film ne correspond à votre recherche. Modifiez les filtres ou le titre.'
              : 'Aucun film à l’affiche pour le moment.'
          }
        />
      )}

      {!loading && !error && filteredMovies.length > 0 && (
        <Row gutter={[24, 24]}>
          {filteredMovies.map((movie) => (
            <Col key={movie.id} xs={24} sm={12} lg={8} xl={6}>
              <MovieCard movie={movie} showShowtimesAction />
            </Col>
          ))}
        </Row>
      )}
    </>
  )
}

export default MoviesPage
