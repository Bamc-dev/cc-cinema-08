import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Alert, Button, DatePicker, Empty, Input, Spin, Table, Tag, Typography } from 'antd'
import { ArrowLeftOutlined, SearchOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { getPublicMovieShowtimes } from '../../api/public'
import { ApiError } from '../../api/client'
import { useSelectedMovie } from '../../context/SelectedMovieContext'
import { formatTime } from '../../utils/formatDate'
import { formatGenre } from '../../utils/genreLabels'

const { Title, Paragraph } = Typography
const { Search } = Input

const SEARCH_DEBOUNCE_MS = 300

function formatPrice(price) {
  if (price == null) {
    return '—'
  }

  return `${price.toFixed(2)} €`
}

function isSameMovie(selectedMovie, movieId) {
  return selectedMovie && String(selectedMovie.id) === String(movieId)
}

const showtimeColumns = [
  {
    title: 'Cinéma',
    dataIndex: 'cinemaName',
    key: 'cinemaName',
  },
  {
    title: 'Ville',
    dataIndex: 'cinemaCity',
    key: 'cinemaCity',
  },
  {
    title: 'Début',
    dataIndex: 'startTime',
    key: 'startTime',
    render: (value) => formatTime(value),
  },
  {
    title: 'Fin',
    dataIndex: 'endTime',
    key: 'endTime',
    render: (value) => formatTime(value),
  },
  {
    title: 'Prix',
    dataIndex: 'price',
    key: 'price',
    render: (value) => formatPrice(value),
  },
  {
    title: 'Salle',
    key: 'room',
    render: (_, record) => `Salle n°${record.roomId} (${record.roomCapacity} places)`,
  },
]

/**
 * Séances d'un film : filtre par date et par cinéma/ville.
 * Exige un film sélectionné (via MovieCard) correspondant à l'id d'URL.
 */
function MovieShowtimesPage() {
  const { movieId } = useParams()
  const navigate = useNavigate()
  const { selectedMovie } = useSelectedMovie()
  const [showtimes, setShowtimes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedDate, setSelectedDate] = useState(dayjs())
  const [cinemaSearch, setCinemaSearch] = useState('')
  const [debouncedCinemaSearch, setDebouncedCinemaSearch] = useState('')

  const hasValidSelection = isSameMovie(selectedMovie, movieId)

  useEffect(() => {
    if (!hasValidSelection) {
      navigate('/films', { replace: true })
    }
  }, [hasValidSelection, navigate])

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedCinemaSearch(cinemaSearch.trim())
    }, SEARCH_DEBOUNCE_MS)

    return () => clearTimeout(timer)
  }, [cinemaSearch])

  useEffect(() => {
    if (!hasValidSelection) {
      return undefined
    }

    let cancelled = false

    async function loadShowtimes() {
      setLoading(true)
      setError(null)

      try {
        const data = await getPublicMovieShowtimes(movieId, {
          date: selectedDate.format('YYYY-MM-DD'),
          query: debouncedCinemaSearch || undefined,
        })

        if (!cancelled) {
          setShowtimes(data)
        }
      } catch (err) {
        if (!cancelled) {
          const errorMessage =
            err instanceof ApiError ? err.message : 'Impossible de charger les séances'
          setError(errorMessage)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadShowtimes()

    return () => {
      cancelled = true
    }
  }, [movieId, selectedDate, debouncedCinemaSearch, hasValidSelection])

  if (!hasValidSelection) {
    return null
  }

  return (
    <>
      <Button
        type="link"
        icon={<ArrowLeftOutlined />}
        style={{ paddingLeft: 0, marginBottom: 16 }}
        onClick={() => navigate('/films')}
      >
        Retour aux films
      </Button>

      <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap', marginBottom: 8 }}>
        <Title level={2} style={{ marginBottom: 0 }}>
          {selectedMovie.title}
        </Title>
        {selectedMovie.genre && <Tag color="blue">{formatGenre(selectedMovie.genre)}</Tag>}
      </div>

      <Paragraph type="secondary" style={{ marginBottom: 32 }}>
        Choisissez une date et, si besoin, filtrez par cinéma ou ville.
      </Paragraph>

      <div
        style={{
          display: 'flex',
          gap: 16,
          flexWrap: 'wrap',
          marginBottom: 32,
          alignItems: 'center',
        }}
      >
        <DatePicker
          value={selectedDate}
          onChange={(value) => setSelectedDate(value ?? dayjs())}
          format="DD/MM/YYYY"
          size="large"
          allowClear={false}
        />

        <Search
          placeholder="Cinéma ou ville..."
          allowClear
          enterButton={<SearchOutlined />}
          size="large"
          value={cinemaSearch}
          onChange={(event) => setCinemaSearch(event.target.value)}
          style={{ flex: 1, minWidth: 260, maxWidth: 480 }}
        />
      </div>

      {loading && (
        <div style={{ textAlign: 'center', padding: 48 }}>
          <Spin size="large" />
        </div>
      )}

      {!loading && error && (
        <Alert
          type="error"
          message="Impossible d'afficher les séances"
          description="Réessayez dans un instant."
          showIcon
        />
      )}

      {!loading && !error && showtimes.length === 0 && (
        <Empty description="Aucune séance pour cette date. Essayez un autre jour ou un autre cinéma." />
      )}

      {!loading && !error && showtimes.length > 0 && (
        <Table
          columns={showtimeColumns}
          dataSource={showtimes}
          rowKey="scheduleId"
          pagination={false}
          scroll={{ x: true }}
          size="middle"
        />
      )}
    </>
  )
}

export default MovieShowtimesPage
