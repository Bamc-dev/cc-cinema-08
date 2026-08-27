import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Alert,
  Button,
  DatePicker,
  Form,
  Popconfirm,
  Select,
  message,
} from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { movieApi, movieShowApi, scheduleApi } from '../../api/admin'
import { ApiError } from '../../api/client'
import AdminFormModal from '../../components/admin/AdminFormModal'
import AdminPageLayout from '../../components/admin/AdminPageLayout'
import { formatDateTime } from '../../utils/formatDate'

const EMPTY_FORM = {
  movieShowId: null,
  startTime: null,
  endTime: null,
}

function toApiDateTime(value) {
  return value ? value.format('YYYY-MM-DDTHH:mm:ss') : null
}

/**
 * CRUD admin des horaires de diffusion (début / fin) rattachés à une séance.
 */
function AdminSchedulesPage() {
  const [rows, setRows] = useState([])
  const [movieShows, setMovieShows] = useState([])
  const [movieTitles, setMovieTitles] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [totalElements, setTotalElements] = useState(0)
  const [search, setSearch] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingRow, setEditingRow] = useState(null)
  const [saving, setSaving] = useState(false)

  const movieShowLabels = useMemo(
    () =>
      Object.fromEntries(
        movieShows.map((show) => {
          const title = movieTitles[show.movieId] ?? `Film n°${show.movieId}`
          const price =
            show.price != null ? `${Number(show.price).toFixed(2)} €` : '—'
          return [show.id, `${title} — salle n°${show.roomId} — ${price}`]
        }),
      ),
    [movieShows, movieTitles],
  )

  const movieShowOptions = useMemo(
    () =>
      movieShows.map((show) => ({
        value: show.id,
        label: movieShowLabels[show.id] ?? `Séance n°${show.id}`,
      })),
    [movieShows, movieShowLabels],
  )

  useEffect(() => {
    async function loadLookups() {
      try {
        const [showsData, moviesData] = await Promise.all([
          movieShowApi.list({ page: 0, size: 100 }),
          movieApi.list({ page: 0, size: 100 }),
        ])

        setMovieShows(showsData.content ?? [])
        setMovieTitles(
          Object.fromEntries(
            (moviesData.content ?? []).map((movie) => [movie.id, movie.title]),
          ),
        )
      } catch {
        setMovieShows([])
        setMovieTitles({})
      }
    }

    loadLookups()
  }, [])

  const columns = useMemo(
    () => [
      { title: 'Réf.', dataIndex: 'id', key: 'id', width: 80 },
      {
        title: 'Séance',
        dataIndex: 'movieShowId',
        key: 'movieShowId',
        render: (movieShowId) =>
          movieShowLabels[movieShowId] ?? `Séance n°${movieShowId}`,
      },
      {
        title: 'Début',
        dataIndex: 'startTime',
        key: 'startTime',
        render: (value) => formatDateTime(value),
      },
      {
        title: 'Fin',
        dataIndex: 'endTime',
        key: 'endTime',
        render: (value) => formatDateTime(value),
      },
    ],
    [movieShowLabels],
  )

  const loadSchedules = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      const data = await scheduleApi.list({
        page,
        size: pageSize,
        search: search || undefined,
      })

      setRows(data.content ?? [])
      setTotalElements(data.totalElements ?? 0)
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : 'Impossible de charger les horaires'
      setError(errorMessage)
      setRows([])
      setTotalElements(0)
    } finally {
      setLoading(false)
    }
  }, [page, pageSize, search])

  useEffect(() => {
    loadSchedules()
  }, [loadSchedules])

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
        movieShowId: values.movieShowId,
        startTime: toApiDateTime(values.startTime),
        endTime: toApiDateTime(values.endTime),
      }

      if (editingRow) {
        await scheduleApi.update(editingRow.id, {
          id: editingRow.id,
          ...payload,
        })
        message.success('Horaire mis à jour')
      } else {
        await scheduleApi.create(payload)
        message.success('Horaire créé')
      }

      closeModal()
      await loadSchedules()
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : "Impossible d'enregistrer l'horaire"
      message.error(errorMessage)
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = useCallback(
    async (record) => {
      const label = formatDateTime(record.startTime)

      try {
        await scheduleApi.delete(record.id)
        message.success(`Horaire du ${label} supprimé`)

        if (rows.length === 1 && page > 0) {
          setPage((current) => current - 1)
        } else {
          await loadSchedules()
        }
      } catch (err) {
        const errorMessage =
          err instanceof ApiError ? err.message : "Impossible de supprimer l'horaire"
        message.error(errorMessage)
      }
    },
    [loadSchedules, page, rows.length],
  )

  const formInitialValues = useMemo(
    () =>
      editingRow
        ? {
            movieShowId: editingRow.movieShowId ?? null,
            startTime: editingRow.startTime ? dayjs(editingRow.startTime) : null,
            endTime: editingRow.endTime ? dayjs(editingRow.endTime) : null,
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
            title="Supprimer cet horaire ?"
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
          message="Impossible d'afficher les horaires"
          description={error}
          style={{ marginBottom: 24 }}
        />
      )}

      <AdminPageLayout
        title="Horaires"
        description="Planifiez les créneaux de diffusion pour chaque séance."
        searchPlaceholder="Rechercher une date (ex. 2026-08-22)..."
        searchValue={search}
        onSearchChange={handleSearchChange}
        createLabel="Ajouter un horaire"
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
        title={editingRow ? "Modifier l'horaire" : 'Ajouter un horaire'}
        initialValues={formInitialValues}
        loading={saving}
        onCancel={closeModal}
        onSubmit={handleSubmit}
        okText={editingRow ? 'Mettre à jour' : 'Créer'}
      >
        <Form.Item
          label="Séance"
          name="movieShowId"
          rules={[{ required: true, message: 'Séance requise' }]}
        >
          <Select
            showSearch
            placeholder="Choisir une séance"
            size="large"
            options={movieShowOptions}
            optionFilterProp="label"
          />
        </Form.Item>

        <Form.Item
          label="Début"
          name="startTime"
          rules={[{ required: true, message: 'Date/heure de début requise' }]}
        >
          <DatePicker
            showTime={{ format: 'HH:mm' }}
            format="DD/MM/YYYY HH:mm"
            placeholder="22/08/2026 14:00"
            size="large"
            style={{ width: '100%' }}
          />
        </Form.Item>

        <Form.Item
          label="Fin"
          name="endTime"
          dependencies={['startTime']}
          rules={[
            { required: true, message: 'Date/heure de fin requise' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                const start = getFieldValue('startTime')
                if (!value || !start || value.isAfter(start)) {
                  return Promise.resolve()
                }
                return Promise.reject(new Error('La fin doit être après le début'))
              },
            }),
          ]}
        >
          <DatePicker
            showTime={{ format: 'HH:mm' }}
            format="DD/MM/YYYY HH:mm"
            placeholder="22/08/2026 16:30"
            size="large"
            style={{ width: '100%' }}
          />
        </Form.Item>
      </AdminFormModal>
    </>
  )
}

export default AdminSchedulesPage
