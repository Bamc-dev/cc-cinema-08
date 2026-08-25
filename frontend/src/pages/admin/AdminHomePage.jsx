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
    description: 'CRUD cinémas — liste, création, modification, suppression',
    path: '/admin/cinemas',
    icon: <BankOutlined style={{ fontSize: 28 }} />,
    ready: true,
  },
  {
    key: 'rooms',
    title: 'Salles',
    description: 'CRUD salles — liste, recherche, création, modification et suppression',
    path: '/admin/rooms',
    icon: <HomeOutlined style={{ fontSize: 28 }} />,
    ready: true,
  },
  {
    key: 'movies',
    title: 'Films',
    description: 'CRUD films — liste, recherche, création, modification et suppression',
    path: '/admin/movies',
    icon: <VideoCameraOutlined style={{ fontSize: 28 }} />,
    ready: true,
  },
  {
    key: 'movie-shows',
    title: 'Séances',
    description: 'À venir (Phase 4.7)',
    path: '/admin/movie-shows',
    icon: <PlayCircleOutlined style={{ fontSize: 28 }} />,
    ready: false,
  },
  {
    key: 'schedules',
    title: 'Horaires',
    description: 'À venir (Phase 4.9)',
    path: '/admin/schedules',
    icon: <CalendarOutlined style={{ fontSize: 28 }} />,
    ready: false,
  },
]

function AdminHomePage() {
  return (
    <>
      <Title level={2}>Administration</Title>
      <Paragraph type="secondary">
        Gérez les ressources du cinéma. Commencez par les cinémas, puis salles, films, séances et
        horaires.
      </Paragraph>

      <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
        {ADMIN_SECTIONS.map((section) => {
          const card = (
            <Card
              hoverable={section.ready}
              style={{ height: '100%', opacity: section.ready ? 1 : 0.55 }}
            >
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
          )

          return (
            <Col key={section.key} xs={24} sm={12} lg={8}>
              {section.ready ? <Link to={section.path}>{card}</Link> : card}
            </Col>
          )
        })}
      </Row>
    </>
  )
}

export default AdminHomePage
