import { useEffect, useState } from 'react'
import { Alert, Col, Empty, Input, Row, Spin, Typography } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import { getPublicCinemas } from '../../api/public'
import { ApiError } from '../../api/client'
import CinemaCard from '../../components/CinemaCard'

const { Title, Paragraph } = Typography
const { Search } = Input

const SEARCH_DEBOUNCE_MS = 300

function CinemasPage() {
  const [cinemas, setCinemas] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search.trim())
    }, SEARCH_DEBOUNCE_MS)

    return () => clearTimeout(timer)
  }, [search])

  useEffect(() => {
    let cancelled = false

    async function loadCinemas() {
      setLoading(true)
      setError(null)

      try {
        const data = await getPublicCinemas(debouncedSearch || undefined)
        if (!cancelled) {
          setCinemas(data)
        }
      } catch (err) {
        if (!cancelled) {
          const errorMessage =
            err instanceof ApiError ? err.message : 'Impossible de charger les cinémas'
          setError(errorMessage)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadCinemas()

    return () => {
      cancelled = true
    }
  }, [debouncedSearch])

  return (
    <>
      <Title level={2}>Cinémas</Title>
      <Paragraph type="secondary">
        Recherchez un cinéma par nom ou ville, puis consultez sa programmation du jour.
      </Paragraph>

      <Search
        placeholder="Rechercher par nom ou ville..."
        allowClear
        enterButton={<SearchOutlined />}
        size="large"
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        style={{ maxWidth: 480, marginTop: 24, marginBottom: 32 }}
      />

      {loading && (
        <div style={{ textAlign: 'center', padding: 48 }}>
          <Spin size="large" />
        </div>
      )}

      {!loading && error && (
        <Alert type="error" message="Erreur de chargement" description={error} showIcon />
      )}

      {!loading && !error && cinemas.length === 0 && (
        <Empty
          description={
            debouncedSearch
              ? `Aucun cinéma trouvé pour « ${debouncedSearch} »`
              : 'Aucun cinéma disponible pour le moment'
          }
        />
      )}

      {!loading && !error && cinemas.length > 0 && (
        <Row gutter={[24, 24]}>
          {cinemas.map((cinema) => (
            <Col key={cinema.id} xs={24} sm={12} lg={8}>
              <CinemaCard cinema={cinema} />
            </Col>
          ))}
        </Row>
      )}
    </>
  )
}

export default CinemasPage
