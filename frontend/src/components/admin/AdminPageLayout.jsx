import { useEffect, useState } from 'react'
import { Button, Input, Space, Typography } from 'antd'
import { PlusOutlined, SearchOutlined } from '@ant-design/icons'
import AdminTable from './AdminTable'

const { Title, Paragraph } = Typography
const { Search } = Input

const SEARCH_DEBOUNCE_MS = 300

/**
 * Template page admin : titre + description + toolbar (search + Ajouter) + AdminTable.
 * @param {object} props
 * @param {string} props.title Titre de la page.
 * @param {string} [props.description] Sous-titre explicatif.
 * @param {string} [props.searchPlaceholder] Placeholder du champ de recherche.
 * @param {string} props.searchValue Valeur de recherche contrôlée (côté page).
 * @param {(value: string) => void} [props.onSearchChange] Appelé après debounce (300 ms).
 * @param {string} [props.createLabel='Ajouter'] Libellé du bouton de création.
 * @param {() => void} [props.onCreate] Ouvre le flux de création ; masque le bouton s'il est absent.
 * @param {object[]} props.columns Colonnes Ant Design Table (hors actions).
 * @param {object[]} props.dataSource Lignes de la page courante.
 * @param {string} [props.rowKey='id'] Clé de ligne.
 * @param {boolean} [props.loading=false] Chargement du tableau.
 * @param {number} [props.page=0] Index de page 0-based (comme l'API).
 * @param {number} [props.pageSize=10] Taille de page.
 * @param {number} [props.totalElements=0] Total backend pour la pagination.
 * @param {(page: number, pageSize: number) => void} props.onPageChange Changement de page / taille.
 * @param {object} [props.actionColumn] Colonne Actions (modifier / supprimer).
 * @param {import('react').ReactNode} [props.extraToolbar] Éléments supplémentaires à gauche du bouton Ajouter.
 */
function AdminPageLayout({
  title,
  description,
  searchPlaceholder = 'Rechercher...',
  searchValue,
  onSearchChange,
  createLabel = 'Ajouter',
  onCreate,
  columns,
  dataSource,
  rowKey = 'id',
  loading = false,
  page = 0,
  pageSize = 10,
  totalElements = 0,
  onPageChange,
  actionColumn,
  extraToolbar,
}) {
  const [localSearch, setLocalSearch] = useState(searchValue ?? '')

  useEffect(() => {
    setLocalSearch(searchValue ?? '')
  }, [searchValue])

  useEffect(() => {
    if (!onSearchChange) {
      return undefined
    }

    const timer = setTimeout(() => {
      onSearchChange(localSearch.trim())
    }, SEARCH_DEBOUNCE_MS)

    return () => clearTimeout(timer)
  }, [localSearch, onSearchChange])

  return (
    <>
      <Title level={2}>{title}</Title>
      {description && <Paragraph type="secondary">{description}</Paragraph>}

      <Space orientation="vertical" size="large" style={{ width: '100%', marginTop: 8 }}>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: 16,
            flexWrap: 'wrap',
          }}
        >
          <Search
            placeholder={searchPlaceholder}
            allowClear
            enterButton={<SearchOutlined />}
            size="large"
            value={localSearch}
            onChange={(event) => setLocalSearch(event.target.value)}
            style={{ flex: 1, minWidth: 240, maxWidth: 420 }}
          />

          <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
            {extraToolbar}
            {onCreate && (
              <Button type="primary" icon={<PlusOutlined />} size="large" onClick={onCreate}>
                {createLabel}
              </Button>
            )}
          </div>
        </div>

        <AdminTable
          columns={columns}
          dataSource={dataSource}
          rowKey={rowKey}
          loading={loading}
          page={page}
          pageSize={pageSize}
          totalElements={totalElements}
          onPageChange={onPageChange}
          actionColumn={actionColumn}
        />
      </Space>
    </>
  )
}

export default AdminPageLayout
