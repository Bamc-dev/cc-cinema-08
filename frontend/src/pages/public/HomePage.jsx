import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Alert, Button, Col, Empty, Row, Spin, Typography } from 'antd'
import { BankOutlined, VideoCameraOutlined } from '@ant-design/icons'
import { getPublicMovies } from '../../api/public'
import { ApiError } from '../../api/client'
import MovieCard from '../../components/MovieCard'

const { Title, Paragraph } = Typography

const PREVIEW_LIMIT = 8

function HomePage() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false

    async function loadMovies() {
      setLoading(true)
      setError(null)

      try {
        const data = await getPublicMovies()
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
  }, [])

  const previewMovies = movies.slice(0, PREVIEW_LIMIT)

  return (
    <>
      <section
        style={{
          textAlign: 'center',
          padding: '24px 0 40px',
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
          marginBottom: 40,
        }}
      >
        <Title level={1} style={{ marginBottom: 16 }}>
          Bienvenue sur Cinema Gestion
        </Title>
        <Paragraph type="secondary" style={{ fontSize: 18, maxWidth: 640, margin: '0 auto 32px' }}>
          Découvrez les films à l&apos;affiche, explorez les cinémas et consultez la programmation
          du jour.
        </Paragraph>

        <div style={{ display: 'flex', gap: 16, justifyContent: 'center', flexWrap: 'wrap' }}>
          <Link to="/cinemas">
            <Button type="primary" size="large" icon={<BankOutlined />}>
              Voir les cinémas
            </Button>
          </Link>
          <Link to="/films">
            <Button size="large" icon={<VideoCameraOutlined />}>
              Tous les films
            </Button>
          </Link>
        </div>
      </section>

      <section>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: 16,
            flexWrap: 'wrap',
            marginBottom: 24,
          }}
        >
          <div>
            <Title level={2} style={{ marginBottom: 4 }}>
              Films disponibles
            </Title>
            <Paragraph type="secondary" style={{ marginBottom: 0 }}>
              Aperçu des films actuellement référencés
            </Paragraph>
          </div>

          {movies.length > PREVIEW_LIMIT && (
            <Link to="/films">
              <Button type="link">Voir tous les films ({movies.length})</Button>
            </Link>
          )}
        </div>

        {loading && (
          <div style={{ textAlign: 'center', padding: 48 }}>
            <Spin size="large" />
          </div>
        )}

        {!loading && error && (
          <Alert type="error" message="Erreur de chargement" description={error} showIcon />
        )}

        {!loading && !error && movies.length === 0 && (
          <Empty description="Aucun film disponible pour le moment" />
        )}

        {!loading && !error && previewMovies.length > 0 && (
          <>
            <Row gutter={[24, 24]}>
              {previewMovies.map((movie) => (
                <Col key={movie.id} xs={24} sm={12} lg={8} xl={6}>
                  <MovieCard movie={movie} />
                </Col>
              ))}
            </Row>

            {movies.length > PREVIEW_LIMIT && (
              <div style={{ textAlign: 'center', marginTop: 32 }}>
                <Link to="/films">
                  <Button size="large">Voir tous les films</Button>
                </Link>
              </div>
            )}
          </>
        )}
      </section>
    </>
  )
}

export default HomePage
