import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Alert,
  Button,
  DatePicker,
  Form,
  InputNumber,
  Popconfirm,
  Select,
  message,
} from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { cinemaApi, roomApi } from '../../api/admin'
import { ApiError } from '../../api/client'
import AdminFormModal from '../../components/admin/AdminFormModal'
import AdminPageLayout from '../../components/admin/AdminPageLayout'

const EMPTY_FORM = {
  capacity: null,
  constructionDate: null,
  cinemaId: null,
}

function AdminRoomsPage() {
  const [rows, setRows] = useState([])
  const [cinemas, setCinemas] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [totalElements, setTotalElements] = useState(0)
  const [search, setSearch] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingRow, setEditingRow] = useState(null)
  const [saving, setSaving] = useState(false)

  const cinemaNames = useMemo(
    () => Object.fromEntries(cinemas.map((cinema) => [cinema.id, cinema.name])),
    [cinemas],
  )

  const cinemaOptions = useMemo(
    () =>
      cinemas.map((cinema) => ({
        value: cinema.id,
        label: `${cinema.name} (${cinema.city})`,
      })),
    [cinemas],
  )

  useEffect(() => {
    async function loadCinemas() {
      try {
        const data = await cinemaApi.list({ page: 0, size: 100 })
        setCinemas(data.content ?? [])
      } catch {
        setCinemas([])
      }
    }

    loadCinemas()
  }, [])

  const columns = useMemo(
    () => [
      { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
      {
        title: 'Capacité',
        dataIndex: 'capacity',
        key: 'capacity',
        render: (value) => `${value} places`,
      },
      {
        title: 'Cinéma',
        dataIndex: 'cinemaId',
        key: 'cinemaId',
        render: (cinemaId) => cinemaNames[cinemaId] ?? `#${cinemaId}`,
      },
    ],
    [cinemaNames],
  )

  const loadRooms = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      const data = await roomApi.list({
        page,
        size: pageSize,
        search: search || undefined,
      })

      setRows(data.content ?? [])
      setTotalElements(data.totalElements ?? 0)
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : 'Impossible de charger les salles'
      setError(errorMessage)
      setRows([])
      setTotalElements(0)
    } finally {
      setLoading(false)
    }
  }, [page, pageSize, search])

  useEffect(() => {
    loadRooms()
  }, [loadRooms])

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
        capacity: values.capacity,
        constructionDate: values.constructionDate.format('YYYY-MM-DD'),
        cinemaId: values.cinemaId,
        movieShowIds: [],
      }

      if (editingRow) {
        await roomApi.update(editingRow.id, {
          id: editingRow.id,
          ...payload,
        })
        message.success('Salle mise à jour')
      } else {
        await roomApi.create(payload)
        message.success('Salle créée')
      }

      closeModal()
      await loadRooms()
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : 'Impossible d\'enregistrer la salle'
      message.error(errorMessage)
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = useCallback(
    async (record) => {
      const label = `${record.capacity} places — ${cinemaNames[record.cinemaId] ?? `#${record.cinemaId}`}`

      try {
        await roomApi.delete(record.id)
        message.success(`« ${label} » supprimée`)

        if (rows.length === 1 && page > 0) {
          setPage((current) => current - 1)
        } else {
          await loadRooms()
        }
      } catch (err) {
        const errorMessage =
          err instanceof ApiError ? err.message : 'Impossible de supprimer la salle'
        message.error(errorMessage)
      }
    },
    [cinemaNames, loadRooms, page, rows.length],
  )

  const formInitialValues = useMemo(
    () =>
      editingRow
        ? {
            capacity: editingRow.capacity ?? null,
            cinemaId: editingRow.cinemaId ?? null,
            constructionDate: null,
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
            title="Supprimer cette salle ?"
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
        title="Salles"
        description="CRUD salles — recherche, création, modification et suppression."
        searchPlaceholder="Rechercher (capacité ou nom du cinéma)..."
        searchValue={search}
        onSearchChange={handleSearchChange}
        createLabel="Ajouter une salle"
        onCreate={openCreate}
        columns={columns}
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
        title={editingRow ? 'Modifier la salle' : 'Ajouter une salle'}
        initialValues={formInitialValues}
        loading={saving}
        onCancel={closeModal}
        onSubmit={handleSubmit}
        okText={editingRow ? 'Mettre à jour' : 'Créer'}
      >
        <Form.Item
          label="Capacité"
          name="capacity"
          rules={[{ required: true, message: 'Capacité requise' }]}
        >
          <InputNumber min={1} placeholder="350" size="large" style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          label="Date de construction"
          name="constructionDate"
          rules={[{ required: true, message: 'Date requise' }]}
        >
          <DatePicker
            format="DD/MM/YYYY"
            placeholder="15/03/1998"
            size="large"
            style={{ width: '100%' }}
            disabledDate={(current) => current && current > dayjs().endOf('day')}
          />
        </Form.Item>

        <Form.Item
          label="Cinéma"
          name="cinemaId"
          rules={[{ required: true, message: 'Cinéma requis' }]}
        >
          <Select
            showSearch
            placeholder="Choisir un cinéma"
            size="large"
            options={cinemaOptions}
            optionFilterProp="label"
          />
        </Form.Item>
      </AdminFormModal>
    </>
  )
}

export default AdminRoomsPage
