import { flushSync } from 'react-dom'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Typography } from 'antd'
import { BankOutlined, EnvironmentOutlined } from '@ant-design/icons'
import { useSelectedCinema } from '../context/SelectedCinemaContext'

const { Text, Title } = Typography

function formatAddress(cinema) {
  const parts = [cinema.number, cinema.street, cinema.city].filter(Boolean)
  return parts.join(', ')
}

function CinemaCard({ cinema }) {
  const navigate = useNavigate()
  const { selectCinema } = useSelectedCinema()

  const handleOpenProgram = () => {
    flushSync(() => {
      selectCinema(cinema)
    })
    navigate(`/cinemas/${cinema.id}`)
  }

  return (
    <Card
      hoverable
      style={{ height: '100%' }}
      styles={{ body: { display: 'flex', flexDirection: 'column', gap: 16, height: '100%' } }}
    >
      <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
        <BankOutlined style={{ fontSize: 28, marginTop: 4, opacity: 0.85 }} />
        <div style={{ flex: 1, minWidth: 0 }}>
          <Title level={4} style={{ marginBottom: 8 }}>
            {cinema.name}
          </Title>
          <Text type="secondary">
            <EnvironmentOutlined style={{ marginRight: 8 }} />
            {formatAddress(cinema)}
          </Text>
        </div>
      </div>

      <Button type="primary" block onClick={handleOpenProgram} style={{ marginTop: 'auto' }}>
        Voir la programmation
      </Button>
    </Card>
  )
}

export default CinemaCard
