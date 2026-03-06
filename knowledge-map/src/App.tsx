import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import TechStackMap from './pages/TechStackMap'
import Home from './pages/Home'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="tech-stack" element={<TechStackMap />} />
      </Route>
    </Routes>
  )
}

export default App
