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
      <Title level={2}>Films</Title>
      <Paragraph type="secondary">
        Recherchez par titre et filtrez par genre, puis consultez les séances disponibles.
      </Paragraph>

      <Search
        placeholder="Rechercher par titre..."
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
          <Text strong>Genres</Text>
          {selectedGenres.length > 0 && (
            <Button type="link" size="small" onClick={() => setSelectedGenres([])}>
              Réinitialiser
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
        <Alert type="error" message="Erreur de chargement" description={error} showIcon />
      )}

      {!loading && !error && filteredMovies.length === 0 && (
        <Empty
          description={
            hasActiveFilters
              ? 'Aucun film ne correspond à vos critères'
              : 'Aucun film disponible pour le moment'
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
