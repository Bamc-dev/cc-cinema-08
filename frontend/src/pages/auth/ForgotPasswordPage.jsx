import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Alert, Button, Form, Input, Typography, message } from 'antd'
import { ArrowLeftOutlined, MailOutlined } from '@ant-design/icons'
import { forgotPassword as forgotPasswordRequest } from '../../api/auth'
import { ApiError } from '../../api/client'
import { formatDateTime } from '../../utils/formatDate'

const { Title, Paragraph, Text } = Typography

function mapForgotError(error) {
  if (!(error instanceof ApiError)) {
    return 'Impossible d’envoyer le lien pour le moment.'
  }

  if (error.status === 404) {
    return 'Aucun compte n’est associé à cet email.'
  }

  if (error.status === 400) {
    return 'Veuillez saisir une adresse email valide.'
  }

  return error.message || 'Impossible d’envoyer le lien pour le moment.'
}

function ForgotPasswordPage() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [resetInfo, setResetInfo] = useState(null)

  const handleSubmit = async (values) => {
    setLoading(true)
    setResetInfo(null)

    try {
      const response = await forgotPasswordRequest(values.email.trim())
      setResetInfo(response)
      message.success('Email envoyé')
      form.resetFields()
    } catch (error) {
      message.error(mapForgotError(error))
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
        Indiquez l&apos;email de votre compte. Vous recevrez un lien pour choisir un nouveau mot de
        passe.
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
          <Input
            prefix={<MailOutlined />}
            placeholder="vous@exemple.fr"
            size="large"
            autoComplete="email"
          />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} size="large" block>
            Envoyer le lien
          </Button>
        </Form.Item>
      </Form>

      {resetInfo && (
        <Alert
          type="success"
          showIcon
          style={{ maxWidth: 560, marginTop: 8 }}
          message="Vérifiez votre boîte mail"
          description={
            <>
              <Paragraph style={{ marginBottom: 8 }}>
                Un email contenant un lien de réinitialisation vient d&apos;être envoyé.
              </Paragraph>
              {resetInfo.expiresAt && (
                <Paragraph type="secondary" style={{ marginBottom: 0 }}>
                  Le lien est valable jusqu&apos;au {formatDateTime(resetInfo.expiresAt)}.
                </Paragraph>
              )}
            </>
          }
        />
      )}
    </>
  )
}

export default ForgotPasswordPage
