import { useEffect, useState } from 'react'
import { Button, Input, Space, Typography } from 'antd'
import { PlusOutlined, SearchOutlined } from '@ant-design/icons'
import AdminTable from './AdminTable'

const { Title, Paragraph } = Typography
const { Search } = Input

const SEARCH_DEBOUNCE_MS = 300

/**
 * Template page admin : titre + description + toolbar (search + Ajouter) + AdminTable.
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
