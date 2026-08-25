import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { Alert, Button, Form, Input, Popconfirm, message } from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons'
import { cinemaApi } from '../../api/admin'
import { ApiError } from '../../api/client'
import AdminFormModal from '../../components/admin/AdminFormModal'
import AdminPageLayout from '../../components/admin/AdminPageLayout'

const COLUMNS = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: 'Nom', dataIndex: 'name', key: 'name' },
  { title: 'Ville', dataIndex: 'city', key: 'city' },
]

const EMPTY_FORM = {
  name: '',
  city: '',
  street: '',
  number: '',
}

function AdminCinemasPage() {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [totalElements, setTotalElements] = useState(0)
  const [search, setSearch] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingRow, setEditingRow] = useState(null)
  const [saving, setSaving] = useState(false)

  const loadCinemas = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      const data = await cinemaApi.list({
        page,
        size: pageSize,
        search: search || undefined,
      })

      setRows(data.content ?? [])
      setTotalElements(data.totalElements ?? 0)
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : 'Impossible de charger les cinémas'
      setError(errorMessage)
      setRows([])
      setTotalElements(0)
    } finally {
      setLoading(false)
    }
  }, [page, pageSize, search])

  useEffect(() => {
    loadCinemas()
  }, [loadCinemas])

  const handlePageChange = (nextPage, nextPageSize) => {
    setPage(nextPage)
    setPageSize(nextPageSize)
  }

  const handleSearchChange = useCallback((value) => {
    setSearch(value)
    setPage(0)
  }, [])

  const openCreate = () => {
    setEditingRow(null)
    setModalOpen(true)
  }

  const openEdit = (record) => {
    setEditingRow(record)
    setModalOpen(true)
  }

  const closeModal = () => {
    setModalOpen(false)
    setEditingRow(null)
  }

  const handleSubmit = async (values) => {
    setSaving(true)

    try {
      const payload = {
        name: values.name,
        city: values.city,
        street: values.street,
        number: values.number,
        roomIds: [],
      }

      if (editingRow) {
        await cinemaApi.update(editingRow.id, {
          id: editingRow.id,
          ...payload,
        })
        message.success('Cinéma mis à jour')
      } else {
        await cinemaApi.create(payload)
        message.success('Cinéma créé')
      }

      closeModal()
      await loadCinemas()
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : "Impossible d'enregistrer le cinéma"
      message.error(errorMessage)
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = useCallback(
    async (record) => {
      try {
        await cinemaApi.delete(record.id)
        message.success(`« ${record.name} » supprimé`)

        if (rows.length === 1 && page > 0) {
          setPage((current) => current - 1)
        } else {
          await loadCinemas()
        }
      } catch (err) {
        const errorMessage =
          err instanceof ApiError ? err.message : 'Impossible de supprimer le cinéma'
        message.error(errorMessage)
      }
    },
    [loadCinemas, page, rows.length],
  )

  const formInitialValues = useMemo(
    () =>
      editingRow
        ? {
            name: editingRow.name ?? '',
            city: editingRow.city ?? '',
            street: '',
            number: '',
          }
        : EMPTY_FORM,
    [editingRow],
  )

  const actionColumn = useMemo(
    () => ({
      title: 'Actions',
      key: 'actions',
      width: 220,
      render: (_, record) => (
        <>
          <Button type="link" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            Modifier
          </Button>
          <Popconfirm
            title="Supprimer ce cinéma ?"
            description="Cette action est définitive."
            okText="Supprimer"
            cancelText="Annuler"
            okButtonProps={{ danger: true }}
            onConfirm={() => handleDelete(record)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              Supprimer
            </Button>
          </Popconfirm>
        </>
      ),
    }),
    [handleDelete],
  )

  return (
    <>
      <Link to="/admin">
        <Button type="link" icon={<ArrowLeftOutlined />} style={{ paddingLeft: 0, marginBottom: 8 }}>
          Retour à l&apos;administration
        </Button>
      </Link>

      {error && (
        <Alert
          type="error"
          showIcon
          message="Erreur de chargement"
          description={error}
          style={{ marginBottom: 24 }}
        />
      )}

      <AdminPageLayout
        title="Cinémas"
        description="CRUD cinémas — recherche, création, modification et suppression."
        searchPlaceholder="Rechercher (nom, ville, rue)..."
        searchValue={search}
        onSearchChange={handleSearchChange}
        createLabel="Ajouter un cinéma"
        onCreate={openCreate}
        columns={COLUMNS}
        dataSource={rows}
        loading={loading}
        page={page}
        pageSize={pageSize}
        totalElements={totalElements}
        onPageChange={handlePageChange}
        actionColumn={actionColumn}
      />

      <AdminFormModal
        open={modalOpen}
        title={editingRow ? 'Modifier le cinéma' : 'Ajouter un cinéma'}
        initialValues={formInitialValues}
        loading={saving}
        onCancel={closeModal}
        onSubmit={handleSubmit}
        okText={editingRow ? 'Mettre à jour' : 'Créer'}
      >
        <Form.Item
          label="Nom"
          name="name"
          rules={[{ required: true, message: 'Nom requis' }]}
        >
          <Input placeholder="Grand Rex" size="large" />
        </Form.Item>

        <Form.Item
          label="Ville"
          name="city"
          rules={[{ required: true, message: 'Ville requise' }]}
        >
          <Input placeholder="Paris" size="large" />
        </Form.Item>

        <Form.Item
          label="Rue"
          name="street"
          rules={[{ required: true, message: 'Rue requise' }]}
        >
          <Input placeholder="Grands Boulevards" size="large" />
        </Form.Item>

        <Form.Item
          label="Numéro"
          name="number"
          rules={[{ required: true, message: 'Numéro requis' }]}
        >
          <Input placeholder="1" size="large" />
        </Form.Item>
      </AdminFormModal>
    </>
  )
}

export default AdminCinemasPage
