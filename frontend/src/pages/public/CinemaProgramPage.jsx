import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Alert, Button, Card, Empty, Spin, Table, Tag, Typography } from 'antd'
import { ArrowLeftOutlined } from '@ant-design/icons'
import { getPublicCinemaToday } from '../../api/public'
import { ApiError } from '../../api/client'
import { useSelectedCinema } from '../../context/SelectedCinemaContext'
import { formatTime } from '../../utils/formatDate'
import { formatGenre } from '../../utils/genreLabels'

const { Title, Paragraph } = Typography

function formatPrice(price) {
  if (price == null) {
    return '—'
  }

  return `${price.toFixed(2)} €`
}

const showtimeColumns = [
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
    render: (_, record) => `Salle #${record.roomId} (${record.roomCapacity} places)`,
  },
]

function isSameCinema(selectedCinema, cinemaId) {
  return selectedCinema && String(selectedCinema.id) === String(cinemaId)
}

function CinemaProgramPage() {
  const { cinemaId } = useParams()
  const navigate = useNavigate()
  const { selectedCinema } = useSelectedCinema()
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const hasValidSelection = isSameCinema(selectedCinema, cinemaId)

  useEffect(() => {
    if (!hasValidSelection) {
      navigate('/cinemas', { replace: true })
    }
  }, [hasValidSelection, navigate])

  useEffect(() => {
    if (!hasValidSelection) {
      return undefined
    }

    let cancelled = false

    async function loadProgram() {
      setLoading(true)
      setError(null)

      try {
        const data = await getPublicCinemaToday(cinemaId)
        if (!cancelled) {
          setMovies(data)
        }
      } catch (err) {
        if (!cancelled) {
          const errorMessage =
            err instanceof ApiError ? err.message : 'Impossible de charger la programmation'
          setError(errorMessage)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadProgram()

    return () => {
      cancelled = true
    }
  }, [cinemaId, hasValidSelection])

  if (!hasValidSelection) {
    return null
  }

  return (
    <>
      <Button
        type="link"
        icon={<ArrowLeftOutlined />}
        style={{ paddingLeft: 0, marginBottom: 16 }}
        onClick={() => navigate('/cinemas')}
      >
        Retour aux cinémas
      </Button>

      <Title level={2}>{selectedCinema.name}</Title>
      <Paragraph type="secondary" style={{ marginBottom: 32 }}>
        Programmation du jour — séances prévues aujourd&apos;hui
        {selectedCinema.city ? ` · ${selectedCinema.city}` : ''}
      </Paragraph>

      {loading && (
        <div style={{ textAlign: 'center', padding: 48 }}>
          <Spin size="large" />
        </div>
      )}

      {!loading && error && (
        <Alert type="error" message="Erreur de chargement" description={error} showIcon />
      )}

      {!loading && !error && movies.length === 0 && (
        <Empty description="Aucune séance prévue aujourd'hui dans ce cinéma" />
      )}

      {!loading && !error && movies.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {movies.map((movie) => (
            <Card
              key={movie.id}
              title={
                <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
                  <span>{movie.title}</span>
                  <Tag color="blue">{formatGenre(movie.genre)}</Tag>
                </div>
              }
            >
              <Table
                columns={showtimeColumns}
                dataSource={movie.showtimes}
                rowKey="scheduleId"
                pagination={false}
                scroll={{ x: true }}
                size="middle"
              />
            </Card>
          ))}
        </div>
      )}
    </>
  )
}

export default CinemaProgramPage
