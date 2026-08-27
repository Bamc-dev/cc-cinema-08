import { flushSync } from 'react-dom'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Tag, Typography } from 'antd'
import { CalendarOutlined, VideoCameraOutlined } from '@ant-design/icons'
import { useSelectedMovie } from '../context/SelectedMovieContext'
import { formatDate } from '../utils/formatDate'
import { formatGenre } from '../utils/genreLabels'

const { Text } = Typography

/**
 * Carte d'un film : titre, genre, date de sortie, et éventuellement lien vers les séances.
 * @param {object} props
 * @param {object} props.movie Film public (id, title, genre, releaseDate).
 * @param {boolean} [props.showShowtimesAction=false] Affiche le bouton « Voir les séances ».
 */
function MovieCard({ movie, showShowtimesAction = false }) {
  const navigate = useNavigate()
  const { selectMovie } = useSelectedMovie()

  const handleOpenShowtimes = () => {
    flushSync(() => {
      selectMovie(movie)
    })
    navigate(`/films/${movie.id}`)
  }

  return (
    <Card
      hoverable
      style={{ height: '100%' }}
      styles={{ body: { display: 'flex', flexDirection: 'column', gap: 12, height: '100%' } }}
    >
      <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
        <VideoCameraOutlined style={{ fontSize: 28, marginTop: 4, opacity: 0.85 }} />
        <div style={{ flex: 1, minWidth: 0 }}>
          <Text strong style={{ fontSize: 18, display: 'block' }}>
            {movie.title}
          </Text>
          <Tag color="blue" style={{ marginTop: 8 }}>
            {formatGenre(movie.genre)}
          </Tag>
        </div>
      </div>

      <Text type="secondary" style={{ marginTop: showShowtimesAction ? 0 : 'auto' }}>
        <CalendarOutlined style={{ marginRight: 8 }} />
        Sortie : {formatDate(movie.releaseDate)}
      </Text>

      {showShowtimesAction && (
        <Button type="primary" block onClick={handleOpenShowtimes} style={{ marginTop: 'auto' }}>
          Voir les séances
        </Button>
      )}
    </Card>
  )
}

export default MovieCard
