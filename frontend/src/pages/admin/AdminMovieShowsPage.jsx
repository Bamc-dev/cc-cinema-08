import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Alert,
  Button,
  Form,
  InputNumber,
  Popconfirm,
  Select,
  message,
} from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons'
import { cinemaApi, movieApi, movieShowApi, roomApi } from '../../api/admin'
import { ApiError } from '../../api/client'
import AdminFormModal from '../../components/admin/AdminFormModal'
import AdminPageLayout from '../../components/admin/AdminPageLayout'

const EMPTY_FORM = {
  price: null,
  movieId: null,
  roomId: null,
}

function formatPrice(price) {
  if (price == null) return '—'
  return `${Number(price).toFixed(2)} €`
}

/**
 * CRUD admin des séances : association film / salle et prix (les horaires se gèrent à part).
 */
function AdminMovieShowsPage() {
  const [rows, setRows] = useState([])
  const [movies, setMovies] = useState([])
  const [rooms, setRooms] = useState([])
  const [cinemaNames, setCinemaNames] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [totalElements, setTotalElements] = useState(0)
  const [search, setSearch] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingRow, setEditingRow] = useState(null)
  const [saving, setSaving] = useState(false)

  const movieTitles = useMemo(
    () => Object.fromEntries(movies.map((movie) => [movie.id, movie.title])),
    [movies],
  )

  const roomLabels = useMemo(
    () =>
      Object.fromEntries(
        rooms.map((room) => {
          const cinema = cinemaNames[room.cinemaId] ?? `Cinéma n°${room.cinemaId}`
          return [room.id, `${cinema} — ${room.capacity} places`]
        }),
      ),
    [rooms, cinemaNames],
  )

  const movieOptions = useMemo(
    () => movies.map((movie) => ({ value: movie.id, label: movie.title })),
    [movies],
  )

  const roomOptions = useMemo(
    () =>
      rooms.map((room) => ({
        value: room.id,
        label: roomLabels[room.id] ?? `Salle n°${room.id}`,
      })),
    [rooms, roomLabels],
  )

  useEffect(() => {
    async function loadLookups() {
      try {
        const [moviesData, roomsData, cinemasData] = await Promise.all([
          movieApi.list({ page: 0, size: 100 }),
          roomApi.list({ page: 0, size: 100 }),
          cinemaApi.list({ page: 0, size: 100 }),
        ])

        setMovies(moviesData.content ?? [])
        setRooms(roomsData.content ?? [])
        setCinemaNames(
          Object.fromEntries(
            (cinemasData.content ?? []).map((cinema) => [cinema.id, cinema.name]),
          ),
        )
      } catch {
        setMovies([])
        setRooms([])
        setCinemaNames({})
      }
    }

    loadLookups()
  }, [])

  const columns = useMemo(
    () => [
      { title: 'Réf.', dataIndex: 'id', key: 'id', width: 80 },
      {
        title: 'Film',
        dataIndex: 'movieId',
        key: 'movieId',
        render: (movieId) => movieTitles[movieId] ?? `Film n°${movieId}`,
      },
      {
        title: 'Salle',
        dataIndex: 'roomId',
        key: 'roomId',
        render: (roomId) => roomLabels[roomId] ?? `Salle n°${roomId}`,
      },
      {
        title: 'Prix',
        dataIndex: 'price',
        key: 'price',
        width: 120,
        render: formatPrice,
      },
      {
        title: 'Créneaux',
        dataIndex: 'scheduleIds',
        key: 'scheduleIds',
        width: 120,
        render: (ids) => (ids?.length ? ids.length : 0),
      },
    ],
    [movieTitles, roomLabels],
  )

  const loadMovieShows = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      const data = await movieShowApi.list({
        page,
        size: pageSize,
        search: search || undefined,
      })

      setRows(data.content ?? [])
      setTotalElements(data.totalElements ?? 0)
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : 'Impossible de charger les séances'
      setError(errorMessage)
      setRows([])
      setTotalElements(0)
    } finally {
      setLoading(false)
    }
  }, [page, pageSize, search])

  useEffect(() => {
    loadMovieShows()
  }, [loadMovieShows])

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
        price: values.price,
        movieId: values.movieId,
        roomId: values.roomId,
        scheduleIds: editingRow?.scheduleIds ?? [],
      }

      if (editingRow) {
        await movieShowApi.update(editingRow.id, {
          id: editingRow.id,
          ...payload,
        })
        message.success('Séance mise à jour')
      } else {
        await movieShowApi.create(payload)
        message.success('Séance créée')
      }

      closeModal()
      await loadMovieShows()
    } catch (err) {
      const errorMessage =
        err instanceof ApiError ? err.message : "Impossible d'enregistrer la séance"
      message.error(errorMessage)
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = useCallback(
    async (record) => {
      const label = movieTitles[record.movieId] ?? `Séance n°${record.id}`

      try {
        await movieShowApi.delete(record.id)
        message.success(`« ${label} » supprimée`)

        if (rows.length === 1 && page > 0) {
          setPage((current) => current - 1)
        } else {
          await loadMovieShows()
        }
      } catch (err) {
        const errorMessage =
          err instanceof ApiError ? err.message : 'Impossible de supprimer la séance'
        message.error(errorMessage)
      }
    },
    [loadMovieShows, movieTitles, page, rows.length],
  )

  const formInitialValues = useMemo(
    () =>
      editingRow
        ? {
            price: editingRow.price ?? null,
            movieId: editingRow.movieId ?? null,
            roomId: editingRow.roomId ?? null,
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
            title="Supprimer cette séance ?"
            description="Cette séance et ses créneaux ne pourront plus être utilisés."
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
          message="Impossible d'afficher les séances"
          description={error}
          style={{ marginBottom: 24 }}
        />
      )}

      <AdminPageLayout
        title="Séances"
        description="Associez un film à une salle et définissez le prix. Les horaires se planifient ensuite."
        searchPlaceholder="Rechercher par film ou prix..."
        searchValue={search}
        onSearchChange={handleSearchChange}
        createLabel="Ajouter une séance"
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
        title={editingRow ? 'Modifier la séance' : 'Ajouter une séance'}
        initialValues={formInitialValues}
        loading={saving}
        onCancel={closeModal}
        onSubmit={handleSubmit}
        okText={editingRow ? 'Mettre à jour' : 'Créer'}
      >
        <Form.Item
          label="Film"
          name="movieId"
          rules={[{ required: true, message: 'Film requis' }]}
        >
          <Select
            showSearch
            placeholder="Choisir un film"
            size="large"
            options={movieOptions}
            optionFilterProp="label"
          />
        </Form.Item>

        <Form.Item
          label="Salle"
          name="roomId"
          rules={[{ required: true, message: 'Salle requise' }]}
        >
          <Select
            showSearch
            placeholder="Choisir une salle"
            size="large"
            options={roomOptions}
            optionFilterProp="label"
          />
        </Form.Item>

        <Form.Item
          label="Prix (€)"
          name="price"
          rules={[{ required: true, message: 'Prix requis' }]}
        >
          <InputNumber
            min={0}
            step={0.5}
            precision={2}
            placeholder="12.50"
            size="large"
            style={{ width: '100%' }}
          />
        </Form.Item>
      </AdminFormModal>
    </>
  )
}

export default AdminMovieShowsPage
