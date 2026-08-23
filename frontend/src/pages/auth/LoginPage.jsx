import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { Button, Form, Input, Typography, message } from 'antd'
import { useAuth } from '../../auth/AuthContext'
import { login as loginRequest } from '../../api/auth'
import { ApiError } from '../../api/client'

const { Title, Paragraph } = Typography

function LoginPage() {
  const { login, isAuthenticated } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)

  const redirectPath = location.state?.from?.pathname ?? '/admin'

  if (isAuthenticated) {
    return <Navigate to={redirectPath} replace />
  }

  const handleSubmit = async (values) => {
    setLoading(true)

    try {
      const authResponse = await loginRequest(values)
      login(authResponse)
      message.success('Connexion réussie')
      navigate(redirectPath, { replace: true })
    } catch (error) {
      const errorMessage =
        error instanceof ApiError ? error.message : 'Impossible de se connecter'

      message.error(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <Title level={2}>Connexion</Title>
      <Paragraph type="secondary">
        Connectez-vous pour accéder à l&apos;espace administration.
      </Paragraph>

      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        style={{ maxWidth: 420, marginTop: 24 }}
        requiredMark={false}
      >
        <Form.Item
          label="Email"
          name="email"
          rules={[
            { required: true, message: 'Email requis' },
            { type: 'email', message: 'Email invalide' },
          ]}
        >
          <Input placeholder="admin@cinema.local" size="large" />
        </Form.Item>

        <Form.Item
          label="Mot de passe"
          name="password"
          rules={[{ required: true, message: 'Mot de passe requis' }]}
        >
          <Input.Password placeholder="Votre mot de passe" size="large" />
        </Form.Item>

        <div style={{ textAlign: 'right', marginBottom: 16 }}>
          <Link to="/forgot-password">Mot de passe oublié ?</Link>
        </div>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} size="large" block>
            Se connecter
          </Button>
        </Form.Item>
      </Form>

      <Paragraph type="secondary">
        Pas encore de compte ? <Link to="/register">S&apos;inscrire</Link>
      </Paragraph>
    </>
  )
}

export default LoginPage
