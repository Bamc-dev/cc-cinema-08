import { Typography } from 'antd'

const { Title, Paragraph } = Typography

function AdminHomePage() {
  return (
    <>
      <Title level={2}>Administration</Title>
      <Paragraph type="secondary">
        Espace admin (CRUD cinémas, salles, films, séances, horaires) — à venir (Phase 4).
      </Paragraph>
    </>
  )
}

export default AdminHomePage
