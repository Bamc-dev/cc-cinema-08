import { useMemo, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { Alert, Button, Form, Input, Typography, message } from 'antd'
import { ArrowLeftOutlined, LockOutlined } from '@ant-design/icons'
import { resetPassword as resetPasswordRequest } from '../../api/auth'
import { ApiError } from '../../api/client'

const { Title, Paragraph } = Typography

function mapResetError(error) {
  if (!(error instanceof ApiError)) {
    return 'Impossible de mettre à jour le mot de passe.'
  }

  const reason = (error.message || '').toLowerCase()

  if (reason.includes('expired')) {
    return 'Ce lien a expiré. Demandez un nouveau lien de réinitialisation.'
  }

  if (reason.includes('invalid') || reason.includes('token')) {
    return 'Ce lien est invalide ou a déjà été utilisé.'
  }

  if (error.status === 400) {
    return 'Le lien ou le nouveau mot de passe est invalide.'
  }

  return error.message || 'Impossible de mettre à jour le mot de passe.'
}

/**
 * Définit un nouveau mot de passe à partir du jeton reçu dans l'URL (?token=).
 */
function ResetPasswordPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)

  const tokenFromUrl = useMemo(
    () => (searchParams.get('token') ?? '').trim(),
    [searchParams],
  )

  const hasToken = tokenFromUrl.length > 0

  const handleSubmit = async (values) => {
    if (!hasToken) {
      message.error('Lien de réinitialisation manquant.')
      return
    }

    setLoading(true)

    try {
      await resetPasswordRequest({
        token: tokenFromUrl,
        newPassword: values.newPassword,
      })
      message.success('Mot de passe mis à jour. Vous pouvez vous connecter.')
      navigate('/login', { replace: true })
    } catch (error) {
      message.error(mapResetError(error))
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

      {!hasToken && (
        <Alert
          type="warning"
          showIcon
          style={{ maxWidth: 520, marginBottom: 24 }}
          message="Lien incomplet"
          description={
            <>
              Ouvrez le lien reçu par email, ou{' '}
              <Link to="/forgot-password">demandez un nouveau lien</Link>.
            </>
          }
        />
      )}

      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        style={{ maxWidth: 420, marginTop: hasToken ? 24 : 0 }}
        requiredMark={false}
        disabled={!hasToken}
      >
        <Form.Item
          label="Nouveau mot de passe"
          name="newPassword"
          rules={[
            { required: true, message: 'Mot de passe requis' },
            { min: 8, message: 'Minimum 8 caractères' },
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="Nouveau mot de passe"
            size="large"
            autoComplete="new-password"
          />
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
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="Confirmez le mot de passe"
            size="large"
            autoComplete="new-password"
          />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} size="large" block>
            Enregistrer le mot de passe
          </Button>
        </Form.Item>
      </Form>
    </>
  )
}

export default ResetPasswordPage
