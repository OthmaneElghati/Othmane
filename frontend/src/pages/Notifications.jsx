import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { notificationService } from '../services/api'
import Swal from 'sweetalert2'

function Notifications() {
  const { t } = useLanguage()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('all')

  useEffect(() => { loadData() }, [])

  const loadData = async () => {
    try {
      const res = await notificationService.getAll()
      setItems(Array.isArray(res.data) ? res.data : res.data.content || [])
    } catch (err) {
      console.error('Failed to load notifications:', err)
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  const markAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id)
      setItems(items.map(i => i.id === id ? { ...i, read: true } : i))
    } catch (err) {
      console.error('Failed to mark notification as read:', err)
    }
  }

  const markAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead()
      setItems(items.map(i => ({ ...i, read: true })))
      Swal.fire({ icon: 'success', title: 'Toutes les notifications lues', timer: 1500, showConfirmButton: false })
    } catch (err) {
      console.error('Failed to mark all notifications as read:', err)
      Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de marquer les notifications comme lues' })
    }
  }

  const deleteNotification = async (id) => {
    try {
      await notificationService.delete(id)
      setItems(items.filter(i => i.id !== id))
    } catch (err) {
      console.error('Failed to delete notification:', err)
      Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de supprimer la notification' })
    }
  }

  const getIcon = (type) => {
    const icons = { MISSION: 'bi-bullseye text-danger', DONATION: 'bi-cash-coin text-success', VOLUNTEER: 'bi-person-check text-primary', EVENT: 'bi-calendar-event text-info', CONVOY: 'bi-truck text-warning', SYSTEM: 'bi-bell text-secondary' }
    return icons[type] || 'bi-bell text-secondary'
  }

  const getTimeAgo = (date) => {
    if (!date) return ''
    const diff = new Date() - new Date(date)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)
    if (days > 0) return `il y a ${days}j`
    if (hours > 0) return `il y a ${hours}h`
    return 'récent'
  }

  const unreadCount = items.filter(i => !i.read).length
  const filtered = filter === 'unread' ? items.filter(i => !i.read) : filter === 'read' ? items.filter(i => i.read) : items

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2">
        <div>
          <h2><i className="bi bi-bell me-2"></i>{t('notifications')}</h2>
          {unreadCount > 0 && <span className="badge bg-danger ms-2">{unreadCount} non lue(s)</span>}
        </div>
        {unreadCount > 0 && (
          <button className="btn btn-outline-primary btn-sm" onClick={markAllAsRead}>
            <i className="bi bi-check-all me-1"></i>Tout marquer comme lu
          </button>
        )}
      </div>

      <div className="card card-custom mb-3">
        <div className="card-body py-2">
          <div className="btn-group btn-group-sm">
            <button className={`btn ${filter === 'all' ? 'btn-primary' : 'btn-outline-primary'}`} onClick={() => setFilter('all')}>Toutes ({items.length})</button>
            <button className={`btn ${filter === 'unread' ? 'btn-primary' : 'btn-outline-primary'}`} onClick={() => setFilter('unread')}>Non lues ({unreadCount})</button>
            <button className={`btn ${filter === 'read' ? 'btn-primary' : 'btn-outline-primary'}`} onClick={() => setFilter('read')}>Lues ({items.length - unreadCount})</button>
          </div>
        </div>
      </div>

      {filtered.length === 0 ? (
        <div className="empty-state"><i className="bi bi-bell-slash"></i><p>Aucune notification</p></div>
      ) : (
        <div className="list-group">
          {filtered.map(item => (
            <div key={item.id} className={`list-group-item list-group-item-action d-flex align-items-start gap-3 ${!item.read ? 'bg-light border-start border-primary border-3' : ''}`}>
              <div className="pt-1">
                <i className={`bi ${getIcon(item.type)} fs-5`}></i>
              </div>
              <div className="flex-grow-1">
                <div className="d-flex justify-content-between align-items-center">
                  <h6 className={`mb-0 ${!item.read ? 'fw-bold' : ''}`}>{item.title}</h6>
                  <small className="text-muted">{getTimeAgo(item.createdAt)}</small>
                </div>
                <p className="text-muted small mb-0 mt-1">{item.message}</p>
              </div>
              <div className="btn-group btn-group-sm">
                {!item.read && (
                  <button className="btn btn-outline-success" onClick={() => markAsRead(item.id)} title="Marquer lu">
                    <i className="bi bi-check"></i>
                  </button>
                )}
                <button className="btn btn-outline-danger" onClick={() => deleteNotification(item.id)} title="Supprimer">
                  <i className="bi bi-x"></i>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Notifications
