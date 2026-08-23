import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Alert, Button, Form, Input, Typography, message } from 'antd'
import { ArrowLeftOutlined } from '@ant-design/icons'
import { forgotPassword as forgotPasswordRequest } from '../../api/auth'
import { ApiError } from '../../api/client'
import { formatDateTime } from '../../utils/formatDate'

const { Title, Paragraph, Text } = Typography

function ForgotPasswordPage() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [resetInfo, setResetInfo] = useState(null)

  const handleSubmit = async (values) => {
    setLoading(true)
    setResetInfo(null)

    try {
      const response = await forgotPasswordRequest(values.email)
      setResetInfo(response)
      message.success('Demande envoyée')
    } catch (error) {
      const errorMessage =
        error instanceof ApiError ? error.message : 'Impossible de traiter la demande'

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

      <Title level={2}>Mot de passe oublié</Title>
      <Paragraph type="secondary">
        Saisissez votre email pour recevoir un lien de réinitialisation.
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

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} size="large" block>
            Envoyer le lien
          </Button>
        </Form.Item>
      </Form>

      {resetInfo && (
        <Alert
          type="info"
          showIcon
          style={{ maxWidth: 560, marginTop: 24 }}
          message="Token de réinitialisation (mode dev)"
          description={
            <>
              <Paragraph style={{ marginBottom: 8 }}>{resetInfo.message}</Paragraph>
              <Text type="secondary">
                Expire le : {formatDateTime(resetInfo.expiresAt)}
              </Text>
              <Paragraph style={{ marginTop: 12, marginBottom: 0 }}>
                <Link to={`/reset-password?token=${resetInfo.resetToken}`}>
                  Réinitialiser mon mot de passe
                </Link>
              </Paragraph>
            </>
          }
        />
      )}
    </>
  )
}

export default ForgotPasswordPage
