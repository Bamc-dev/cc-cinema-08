import { Table } from 'antd'

/**
 * Table admin branchée sur le PageDTO backend :
 * { content, totalElements, totalPages, pageSize }
 *
 * page : index 0-based (comme GET /list/{page}/{size})
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
        showTotal: (total) => `${total} élément${total > 1 ? 's' : ''}`,
        onChange: (nextPage, nextPageSize) => {
          onPageChange?.(nextPage - 1, nextPageSize)
        },
      }}
    />
  )
}

export default AdminTable
