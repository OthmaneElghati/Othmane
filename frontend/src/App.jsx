import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import { LanguageProvider } from './context/LanguageContext'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Register from './pages/Register'
import ForgotPassword from './pages/ForgotPassword'
import Dashboard from './pages/Dashboard'
import Missions from './pages/Missions'
import Volunteers from './pages/Volunteers'
import Beneficiaries from './pages/Beneficiaries'
import Donations from './pages/Donations'
import Convoys from './pages/Convoys'
import Events from './pages/Events'
import Reports from './pages/Reports'
import Notifications from './pages/Notifications'
import MapPage from './pages/MapPage'
import Layout from './components/Layout'

function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth()
  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>
  return isAuthenticated() ? children : <Navigate to="/login" />
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/app" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<Dashboard />} />
        <Route path="missions" element={<Missions />} />
        <Route path="volunteers" element={<Volunteers />} />
        <Route path="beneficiaries" element={<Beneficiaries />} />
        <Route path="donations" element={<Donations />} />
        <Route path="convoys" element={<Convoys />} />
        <Route path="events" element={<Events />} />
        <Route path="reports" element={<Reports />} />
        <Route path="notifications" element={<Notifications />} />
        <Route path="map" element={<MapPage />} />
      </Route>
    </Routes>
  )
}

function App() {
  return (
    <LanguageProvider>
      <AuthProvider>
        <Router>
          <AppRoutes />
        </Router>
      </AuthProvider>
    </LanguageProvider>
  )
}

export default App
