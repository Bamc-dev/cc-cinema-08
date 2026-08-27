import { Table } from 'antd'

/**
 * Table admin branchée sur le PageDTO backend :
 * { content, totalElements, totalPages, pageSize }
 *
 * page : index 0-based (comme GET /list/{page}/{size})
 * @param {object} props
 * @param {object[]} props.columns Colonnes Ant Design (hors actions).
 * @param {object[]} [props.dataSource=[]] Lignes affichées.
 * @param {string} [props.rowKey='id'] Clé de ligne.
 * @param {boolean} [props.loading=false] Indicateur de chargement.
 * @param {number} [props.page=0] Index de page 0-based (converti en current 1-based pour Ant Design).
 * @param {number} [props.pageSize=10] Taille de page.
 * @param {number} [props.totalElements=0] Total d'éléments (pagination).
 * @param {(page: number, pageSize: number) => void} [props.onPageChange] Reçoit un index 0-based.
 * @param {object} [props.actionColumn] Colonne ajoutée à droite (actions).
 * @param {object} [props.scroll] Option scroll du Table.
 * @param {string} [props.size='middle'] Densité du tableau.
 */
function AdminTable({
  columns,
  dataSource = [],
  rowKey = 'id',
  loading = false,
  page = 0,
  pageSize = 10,
  totalElements = 0,
  onPageChange,
  actionColumn,
  scroll = { x: true },
  size = 'middle',
}) {
  const tableColumns = actionColumn ? [...columns, actionColumn] : columns

  return (
    <Table
      columns={tableColumns}
      dataSource={dataSource}
      rowKey={rowKey}
      loading={loading}
      scroll={scroll}
      size={size}
      pagination={{
        current: page + 1,
        pageSize,
        total: totalElements,
        showSizeChanger: true,
        pageSizeOptions: [5, 10, 20, 50],
        showTotal: (total) => `${total} résultat${total > 1 ? 's' : ''}`,
        onChange: (nextPage, nextPageSize) => {
          onPageChange?.(nextPage - 1, nextPageSize)
        },
      }}
      locale={{
        emptyText: 'Aucun élément pour le moment',
      }}
    />
  )
}

export default AdminTable
