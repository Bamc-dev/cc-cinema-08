import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Alert,
  Button,
  DatePicker,
  Form,
  Input,
  Popconfirm,
  Select,
  Tag,
  message,
} from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { movieApi } from '../../api/admin'
import { ApiError } from '../../api/client'
import AdminFormModal from '../../components/admin/AdminFormModal'
import AdminPageLayout from '../../components/admin/AdminPageLayout'

const GENRE_LABELS = {
  ACTION: 'Action',
  ADVENTURE: 'Aventure',
  COMEDY: 'Comédie',
  DRAMA: 'Drame',
  FANTASY: 'Fantasy',
  HORROR: 'Horreur',
  MYSTERY: 'Mystère',
  ROMANCE: 'Romance',
  SCIENCE_FICTION: 'Science-fiction',
}

const GENRE_COLORS = {
  ACTION: 'red',
  ADVENTURE: 'orange',
  COMEDY: 'gold',
  DRAMA: 'purple',
  FANTASY: 'cyan',
  HORROR: 'volcano',
  MYSTERY: 'geekblue',
  ROMANCE: 'pink',
  SCIENCE_FICTION: 'blue',
}

const GENRE_OPTIONS = Object.entries(GENRE_LABELS).map(([value, label]) => ({ value, label }))

const EMPTY_FORM = {
  title: '',
  releaseDate: null,
  genre: null,
}

const COLUMNS = [
  { title: 'Réf.', dataIndex: 'id', key: 'id', width: 80 },
  { title: 'Titre', dataIndex: 'title', key: 'title' },
  {
    title: 'Genre',
    dataIndex: 'genre',
    key: 'genre',
    render: (genre) =>
      genre ? (
        <Tag color={GENRE_COLORS[genre] ?? 'default'}>{GENRE_LABELS[genre] ?? genre}</Tag>
      ) : null,
  },
  {
    title: 'Sortie',
    dataIndex: 'releaseDate',
    key: 'releaseDate',
    render: (date) => (date ? dayjs(date).format('DD/MM/YYYY') : '—'),
  },
]

function AdminMoviesPage() {
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

  const loadMovies = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      const data = await movieApi.list({
        page,
        size: pageSize,
        search: search || undefined,
      })

      setRows(data.content ?? [])
      setTotalElements(data.totalElements ?? 0)
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : 'Impossible de charger les films'
      setError(errorMessage)
      setRows([])
      setTotalElements(0)
    } finally {
      setLoading(false)
    }
  }, [page, pageSize, search])

  useEffect(() => {
    loadMovies()
  }, [loadMovies])

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
        title: values.title,
        releaseDate: values.releaseDate ? values.releaseDate.format('YYYY-MM-DD') : null,
        genre: values.genre,
        movieShowIds: [],
      }

      if (editingRow) {
        await movieApi.update(editingRow.id, { id: editingRow.id, ...payload })
        message.success('Film mis à jour')
      } else {
        await movieApi.create(payload)
        message.success('Film créé')
      }

      closeModal()
      await loadMovies()
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : "Impossible d'enregistrer le film"
      message.error(errorMessage)
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = useCallback(
    async (record) => {
      try {
        await movieApi.delete(record.id)
        message.success(`« ${record.title} » supprimé`)

        if (rows.length === 1 && page > 0) {
          setPage((current) => current - 1)
        } else {
          await loadMovies()
        }
      } catch (err) {
        const errorMessage =
          err instanceof ApiError ? err.message : 'Impossible de supprimer le film'
        message.error(errorMessage)
      }
    },
    [loadMovies, page, rows.length],
  )

  const formInitialValues = useMemo(
    () =>
      editingRow
        ? {
            title: editingRow.title ?? '',
            genre: editingRow.genre ?? null,
            releaseDate: editingRow.releaseDate ? dayjs(editingRow.releaseDate) : null,
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
            title="Supprimer ce film ?"
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
          Retour à l&apos;espace de gestion
        </Button>
      </Link>

      {error && (
        <Alert
          type="error"
          showIcon
          message="Impossible d'afficher les films"
          description={error}
          style={{ marginBottom: 24 }}
        />
      )}

      <AdminPageLayout
        title="Films"
        description="Gérez le catalogue des films diffusés."
        searchPlaceholder="Rechercher par titre ou genre..."
        searchValue={search}
        onSearchChange={handleSearchChange}
        createLabel="Ajouter un film"
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
        title={editingRow ? 'Modifier le film' : 'Ajouter un film'}
        initialValues={formInitialValues}
        loading={saving}
        onCancel={closeModal}
        onSubmit={handleSubmit}
        okText={editingRow ? 'Mettre à jour' : 'Créer'}
      >
        <Form.Item
          label="Titre"
          name="title"
          rules={[{ required: true, message: 'Titre requis' }]}
        >
          <Input placeholder="Inception" size="large" />
        </Form.Item>

        <Form.Item
          label="Genre"
          name="genre"
          rules={[{ required: true, message: 'Genre requis' }]}
        >
          <Select
            placeholder="Choisir un genre"
            size="large"
            options={GENRE_OPTIONS}
            optionFilterProp="label"
          />
        </Form.Item>

        <Form.Item
          label="Date de sortie"
          name="releaseDate"
          rules={[{ required: true, message: 'Date de sortie requise' }]}
        >
          <DatePicker
            format="DD/MM/YYYY"
            placeholder="01/01/2024"
            size="large"
            style={{ width: '100%' }}
          />
        </Form.Item>
      </AdminFormModal>
    </>
  )
}

export default AdminMoviesPage
