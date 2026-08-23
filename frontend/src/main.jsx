import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { ConfigProvider, theme } from 'antd'
import frFR from 'antd/locale/fr_FR'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'
import './index.css'
import App from './App.jsx'

dayjs.locale('fr')

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ConfigProvider
      locale={frFR}
      theme={{
        algorithm: theme.darkAlgorithm,
        token: {
          colorPrimary: '#4096ff',
          borderRadius: 8,
          fontSize: 16,
          fontSizeLG: 18,
          fontSizeHeading1: 40,
          fontSizeHeading2: 32,
          fontSizeHeading3: 26,
          controlHeight: 44,
          paddingContentHorizontal: 28,
        },
      }}
    >
      <App />
    </ConfigProvider>
  </StrictMode>,
)
