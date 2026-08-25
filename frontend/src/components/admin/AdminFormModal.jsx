import { useEffect } from 'react'
import { Form, Modal } from 'antd'

/**
 * Modal + Form réutilisable pour create / edit admin.
 * Les champs (Form.Item) sont passés en children.
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
