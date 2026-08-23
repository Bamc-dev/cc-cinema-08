import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { Button, Form, Input, Typography, message } from 'antd'
import { ArrowLeftOutlined } from '@ant-design/icons'
import { resetPassword as resetPasswordRequest } from '../../api/auth'
import { ApiError } from '../../api/client'

const { Title, Paragraph } = Typography

function ResetPasswordPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)

  const tokenFromUrl = searchParams.get('token') ?? ''

  const handleSubmit = async (values) => {
    setLoading(true)

    try {
      await resetPasswordRequest({
        token: values.token,
        newPassword: values.newPassword,
      })
      message.success('Mot de passe mis à jour')
      navigate('/login', { replace: true })
    } catch (error) {
      const errorMessage =
        error instanceof ApiError ? error.message : 'Impossible de réinitialiser le mot de passe'

      message.error(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <Link to="/login">
        <Button type="link" icon={<ArrowLeftOutlined />} style={{ paddingLeft: 0, marginBottom: 16 }}>
          Retour à la connexion
        </Button>
      </Link>

      <Title level={2}>Nouveau mot de passe</Title>
      <Paragraph type="secondary">
        Choisissez un nouveau mot de passe pour votre compte.
      </Paragraph>

      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{ token: tokenFromUrl }}
        style={{ maxWidth: 420, marginTop: 24 }}
        requiredMark={false}
      >
        <Form.Item
          label="Token de réinitialisation"
          name="token"
          rules={[{ required: true, message: 'Token requis' }]}
        >
          <Input placeholder="Token reçu par email" size="large" />
        </Form.Item>

        <Form.Item
          label="Nouveau mot de passe"
          name="newPassword"
          rules={[
            { required: true, message: 'Mot de passe requis' },
            { min: 8, message: 'Minimum 8 caractères' },
          ]}
        >
          <Input.Password placeholder="Nouveau mot de passe" size="large" />
        </Form.Item>

        <Form.Item
          label="Confirmer le mot de passe"
          name="confirmPassword"
          dependencies={['newPassword']}
          rules={[
            { required: true, message: 'Confirmation requise' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('newPassword') === value) {
                  return Promise.resolve()
                }

                return Promise.reject(new Error('Les mots de passe ne correspondent pas'))
              },
            }),
          ]}
        >
          <Input.Password placeholder="Confirmez le mot de passe" size="large" />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} size="large" block>
            Mettre à jour le mot de passe
          </Button>
        </Form.Item>
      </Form>
    </>
  )
}

export default ResetPasswordPage
