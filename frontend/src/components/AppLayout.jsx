import { useState } from 'react'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { Drawer, Grid, Layout, Menu, Typography, theme } from 'antd'
import { MenuOutlined } from '@ant-design/icons'
import { useAuth } from '../auth/AuthContext'

const { Header, Content, Footer } = Layout
const { Text } = Typography

const publicNavItems = [
  { key: '/', label: <Link to="/">Accueil</Link> },
  { key: '/cinemas', label: <Link to="/cinemas">Cinémas</Link> },
  { key: '/films', label: <Link to="/films">Films</Link> },
]

const guestNavItems = [
  { key: '/login', label: <Link to="/login">Connexion</Link> },
  { key: '/register', label: <Link to="/register">Inscription</Link> },
]

const adminNavItem = {
  key: '/admin',
  label: <Link to="/admin">Gestion</Link>,
}

const logoutNavItem = {
  key: 'logout',
  label: 'Déconnexion',
}

function getSelectedKey(pathname) {
  if (pathname.startsWith('/admin')) {
    return '/admin'
  }

  if (pathname.startsWith('/cinemas')) {
    return '/cinemas'
  }

  if (pathname.startsWith('/films')) {
    return '/films'
  }

  if (pathname.startsWith('/login')) {
    return '/login'
  }

  if (pathname.startsWith('/register')) {
    return '/register'
  }

  return '/'
}

/**
 * Calque commun : en-tête, navigation (publique / invité / admin), contenu et pied de page.
 */
function AppLayout() {
  const location = useLocation()
  const navigate = useNavigate()
  const { isAuthenticated, logout } = useAuth()
  const screens = Grid.useBreakpoint()
  const { token } = theme.useToken()
  const isMobile = !screens.md
  const [drawerOpen, setDrawerOpen] = useState(false)

  const selectedKey = getSelectedKey(location.pathname)

  const menuItems = isAuthenticated
    ? [...publicNavItems, adminNavItem, logoutNavItem]
    : [...publicNavItems, ...guestNavItems]

  const handleMenuClick = async ({ key }) => {
    if (key === 'logout') {
      await logout()
      navigate('/')
    }

    setDrawerOpen(false)
  }

  const menu = (
    <Menu
      mode={isMobile ? 'inline' : 'horizontal'}
      selectedKeys={[selectedKey]}
      items={menuItems}
      onClick={handleMenuClick}
      style={isMobile ? undefined : { flex: 1, minWidth: 0, borderBottom: 'none', fontSize: 16 }}
    />
  )

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: 24,
          height: isMobile ? 64 : 80,
          lineHeight: isMobile ? '64px' : '80px',
          paddingInline: isMobile ? 20 : 48,
          background: token.colorBgContainer,
          borderBottom: `1px solid ${token.colorBorderSecondary}`,
        }}
      >
        <Link to="/" style={{ color: 'inherit', textDecoration: 'none', flexShrink: 0 }}>
          <Text strong style={{ fontSize: isMobile ? 20 : 26, whiteSpace: 'nowrap' }}>
            Cinema Gestion
          </Text>
        </Link>

        {isMobile ? (
          <>
            <div style={{ flex: 1 }} />
            <MenuOutlined
              role="button"
              aria-label="Ouvrir le menu"
              onClick={() => setDrawerOpen(true)}
              style={{ fontSize: 24, cursor: 'pointer' }}
            />
            <Drawer
              title="Menu"
              placement="right"
              open={drawerOpen}
              onClose={() => setDrawerOpen(false)}
            >
              {menu}
            </Drawer>
          </>
        ) : (
          menu
        )}
      </Header>

      <Content style={{ padding: isMobile ? 20 : 40, background: token.colorBgLayout }}>
        <div
          style={{
            width: '100%',
            maxWidth: 1680,
            margin: '0 auto',
            background: token.colorBgElevated,
            padding: isMobile ? 24 : 48,
            borderRadius: 12,
            minHeight: isMobile ? 420 : 560,
            border: `1px solid ${token.colorBorderSecondary}`,
          }}
        >
          <Outlet />
        </div>
      </Content>

      <Footer
        style={{
          textAlign: 'center',
          background: token.colorBgContainer,
          borderTop: `1px solid ${token.colorBorderSecondary}`,
          padding: '28px 48px',
          fontSize: 15,
        }}
      >
        Cinema Gestion © {new Date().getFullYear()} — Trouvez votre séance
      </Footer>
    </Layout>
  )
}

export default AppLayout
