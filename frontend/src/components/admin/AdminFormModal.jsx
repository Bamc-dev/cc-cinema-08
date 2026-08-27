import { useEffect } from 'react'
import { Form, Modal } from 'antd'

/**
 * Modal + Form réutilisable pour create / edit admin.
 * Les champs (Form.Item) sont passés en children.
 * @param {object} props
 * @param {boolean} props.open Contrôle l'affichage de la modal.
 * @param {string} props.title Titre de la modal.
 * @param {object} [props.initialValues={}] Valeurs initiales du formulaire (rechargées à l'ouverture).
 * @param {boolean} [props.loading=false] État de soumission (bouton OK).
 * @param {() => void} props.onCancel Fermeture sans enregistrer.
 * @param {(values: object) => void|Promise<void>} props.onSubmit Handler onFinish du Form.
 * @param {import('react').ReactNode} props.children Champs Form.Item.
 * @param {number} [props.width=520] Largeur de la modal.
 * @param {string} [props.okText='Enregistrer'] Libellé du bouton de validation.
 * @param {string} [props.cancelText='Annuler'] Libellé du bouton d'annulation.
 */
function AdminFormModal({
  open,
  title,
  initialValues = {},
  loading = false,
  onCancel,
  onSubmit,
  children,
  width = 520,
  okText = 'Enregistrer',
  cancelText = 'Annuler',
}) {
  const [form] = Form.useForm()

  useEffect(() => {
    if (open) {
      form.setFieldsValue(initialValues)
    }
  }, [open, form, initialValues])

  const handleOk = () => {
    form.submit()
  }

  return (
    <Modal
      open={open}
      title={title}
      onCancel={onCancel}
      onOk={handleOk}
      confirmLoading={loading}
      okText={okText}
      cancelText={cancelText}
      width={width}
      destroyOnHidden
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={onSubmit}
        requiredMark={false}
        style={{ marginTop: 16 }}
      >
        {children}
      </Form>
    </Modal>
  )
}

export default AdminFormModal
