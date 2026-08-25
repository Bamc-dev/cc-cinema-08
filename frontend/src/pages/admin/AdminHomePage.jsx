import { Link } from 'react-router-dom'
import { Card, Col, Row, Typography } from 'antd'
import {
  BankOutlined,
  CalendarOutlined,
  HomeOutlined,
  PlayCircleOutlined,
  VideoCameraOutlined,
} from '@ant-design/icons'

const { Title, Paragraph, Text } = Typography

const ADMIN_SECTIONS = [
  {
    key: 'cinemas',
    title: 'Cinémas',
    description: 'Vos établissements : nom, ville et adresse',
    path: '/admin/cinemas',
    icon: <BankOutlined style={{ fontSize: 28 }} />,
  },
  {
    key: 'rooms',
    title: 'Salles',
    description: 'Salles de projection et leur capacité',
    path: '/admin/rooms',
    icon: <HomeOutlined style={{ fontSize: 28 }} />,
  },
  {
    key: 'movies',
    title: 'Films',
    description: 'Catalogue : titres, genres et dates de sortie',
    path: '/admin/movies',
    icon: <VideoCameraOutlined style={{ fontSize: 28 }} />,
  },
  {
    key: 'movie-shows',
    title: 'Séances',
    description: 'Associer un film à une salle et définir le prix',
    path: '/admin/movie-shows',
    icon: <PlayCircleOutlined style={{ fontSize: 28 }} />,
  },
  {
    key: 'schedules',
    title: 'Horaires',
    description: 'Planifier les créneaux de diffusion',
    path: '/admin/schedules',
    icon: <CalendarOutlined style={{ fontSize: 28 }} />,
  },
]

function AdminHomePage() {
  return (
    <>
      <Title level={2}>Espace de gestion</Title>
      <Paragraph type="secondary">
        Gérez vos cinémas, salles, films et la programmation des séances.
      </Paragraph>

      <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
        {ADMIN_SECTIONS.map((section) => (
          <Col key={section.key} xs={24} sm={12} lg={8}>
            <Link to={section.path}>
              <Card hoverable style={{ height: '100%' }}>
                <div style={{ display: 'flex', gap: 16, alignItems: 'flex-start' }}>
                  {section.icon}
                  <div>
                    <Text strong style={{ fontSize: 18, display: 'block' }}>
                      {section.title}
                    </Text>
                    <Text type="secondary">{section.description}</Text>
                  </div>
                </div>
              </Card>
            </Link>
          </Col>
        ))}
      </Row>
    </>
  )
}

export default AdminHomePage
