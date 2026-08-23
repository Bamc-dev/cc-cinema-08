import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { Button, Form, Input, Typography, message } from 'antd'
import { useAuth } from '../../auth/AuthContext'
import { register as registerRequest } from '../../api/auth'
import { ApiError } from '../../api/client'

const { Title, Paragraph } = Typography

function RegisterPage() {
  const { login, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)

  if (isAuthenticated) {
    return <Navigate to="/admin" replace />
  }

  const handleSubmit = async (values) => {
    setLoading(true)

    try {
      const authResponse = await registerRequest({
        email: values.email,
        password: values.password,
      })
      login(authResponse)
      message.success('Compte créé avec succès')
      navigate('/admin', { replace: true })
    } catch (error) {
      let errorMessage = 'Impossible de créer le compte'

      if (error instanceof ApiError) {
        if (error.status === 409) {
          errorMessage = 'Cet email est déjà utilisé'
        } else {
          errorMessage = error.message
        }
      }

      message.error(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <Title level={2}>Inscription</Title>
      <Paragraph type="secondary">
        Créez un compte administrateur pour gérer les cinémas, films et séances.
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
          rules={[
            { required: true, message: 'Mot de passe requis' },
            { min: 8, message: 'Minimum 8 caractères' },
          ]}
        >
          <Input.Password placeholder="Votre mot de passe" size="large" />
        </Form.Item>

        <Form.Item
          label="Confirmer le mot de passe"
          name="confirmPassword"
          dependencies={['password']}
          rules={[
            { required: true, message: 'Confirmation requise' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve()
                }

                return Promise.reject(new Error('Les mots de passe ne correspondent pas'))
              },
            }),
          ]}
        >
          <Input.Password placeholder="Confirmez votre mot de passe" size="large" />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} size="large" block>
            Créer mon compte
          </Button>
        </Form.Item>
      </Form>

      <Paragraph type="secondary">
        Déjà un compte ? <Link to="/login">Se connecter</Link>
      </Paragraph>
    </>
  )
}

export default RegisterPage
