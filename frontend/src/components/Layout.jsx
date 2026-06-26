import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useLanguage } from '../context/LanguageContext'
import { useState, useEffect } from 'react'
import { notificationService } from '../services/api'

function Layout() {
  const { user, logout } = useAuth()
  const { t, language, changeLanguage } = useLanguage()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [unreadCount, setUnreadCount] = useState(0)

  useEffect(() => {
    loadUnreadCount()
    const interval = setInterval(loadUnreadCount, 30000)
    return () => clearInterval(interval)
  }, [])

  const loadUnreadCount = async () => {
    try {
      const res = await notificationService.getUnreadCount()
      setUnreadCount(res.data)
    } catch (err) {
      setUnreadCount(0)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const menuItems = [
    { path: '/app', icon: 'bi-speedometer2', label: t('dashboard'), end: true },
    { path: '/app/missions', icon: 'bi-bullseye', label: t('missions') },
    { path: '/app/volunteers', icon: 'bi-people', label: t('volunteers') },
    { path: '/app/beneficiaries', icon: 'bi-heart', label: t('beneficiaries') },
    { path: '/app/donations', icon: 'bi-cash-coin', label: t('donations') },
    { path: '/app/convoys', icon: 'bi-truck', label: t('convoys') },
    { path: '/app/events', icon: 'bi-calendar-event', label: t('events') },
    { path: '/app/map', icon: 'bi-geo-alt', label: 'Carte' },
    { path: '/app/reports', icon: 'bi-file-earmark-bar-graph', label: t('reports') },
    { path: '/app/notifications', icon: 'bi-bell', label: t('notifications'), badge: unreadCount }
  ]

  return (
    <div>
      <nav className="navbar navbar-expand navbar-dark navbar-custom fixed-top px-3">
        <button className="btn btn-link text-white d-md-none me-2" onClick={() => setSidebarOpen(!sidebarOpen)}>
          <i className="bi bi-list fs-4"></i>
        </button>
        <a className="navbar-brand fw-bold" href="/app">
          <i className="bi bi-heart-pulse me-2"></i>
          {t('platformTitle')}
        </a>
        <div className="ms-auto d-flex align-items-center gap-3">
          <div className="dropdown">
            <button className="btn btn-sm btn-outline-light dropdown-toggle" data-bs-toggle="dropdown">
              <i className="bi bi-translate me-1"></i>
              {language.toUpperCase()}
            </button>
            <ul className="dropdown-menu dropdown-menu-end">
              <li><button className="dropdown-item" onClick={() => changeLanguage('fr')}>Français</button></li>
              <li><button className="dropdown-item" onClick={() => changeLanguage('ar')}>العربية</button></li>
              <li><button className="dropdown-item" onClick={() => changeLanguage('en')}>English</button></li>
            </ul>
          </div>
          <div className="dropdown">
            <button className="btn btn-sm btn-outline-light dropdown-toggle" data-bs-toggle="dropdown">
              <i className="bi bi-person-circle me-1"></i>
              {user?.firstName}
            </button>
            <ul className="dropdown-menu dropdown-menu-end">
              <li><span className="dropdown-item-text fw-bold">{user?.firstName} {user?.lastName}</span></li>
              <li><span className="dropdown-item-text text-muted small">{user?.email}</span></li>
              <li><hr className="dropdown-divider" /></li>
              <li><button className="dropdown-item text-danger" onClick={handleLogout}><i className="bi bi-box-arrow-right me-2"></i>{t('logout')}</button></li>
            </ul>
          </div>
        </div>
      </nav>

      <div className={`sidebar ${sidebarOpen ? 'show' : ''}`}>
        <nav className="nav flex-column pt-3">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.end}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              onClick={() => setSidebarOpen(false)}
            >
              <i className={`bi ${item.icon}`}></i>
              <span>{item.label}</span>
              {item.badge > 0 && <span className="badge bg-danger ms-auto">{item.badge}</span>}
            </NavLink>
          ))}
        </nav>
      </div>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}

export default Layout
