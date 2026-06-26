import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { eventService } from '../services/api'
import Swal from 'sweetalert2'

function Events() {
  const { t } = useLanguage()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ title: '', description: '', eventDate: '', location: '', city: '', region: '', organizer: '', maxParticipants: '', status: 'UPCOMING' })

  const statuses = ['UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED']
  const statusLabels = { UPCOMING: t('upcoming'), ONGOING: t('ongoing'), COMPLETED: t('completed'), CANCELLED: t('cancelled') }
  const statusColors = { UPCOMING: 'info', ONGOING: 'success', COMPLETED: 'secondary', CANCELLED: 'danger' }
  const regions = ['Rabat-Salé-Kénitra', 'Casablanca-Settat', 'Marrakech-Safi', 'Fès-Meknès', 'Tanger-Tétouan-Al Hoceïma', 'Souss-Massa', 'Oriental', 'Béni Mellal-Khénifra', 'Drâa-Tafilalet', 'Laâyoune-Sakia El Hamra', 'Dakhla-Oued Ed-Dahab']

  useEffect(() => { loadData() }, [])

  const loadData = async () => {
    try {
      const res = await eventService.getAll()
      setItems(Array.isArray(res.data) ? res.data : res.data.content || [])
    } catch (err) {
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editing) {
        await eventService.update(editing.id, form)
        Swal.fire({ icon: 'success', title: 'Événement modifié', timer: 1500, showConfirmButton: false })
      } else {
        await eventService.create(form)
        Swal.fire({ icon: 'success', title: 'Événement créé', timer: 1500, showConfirmButton: false })
      }
      setShowModal(false)
      setEditing(null)
      resetForm()
      loadData()
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || 'Erreur' })
    }
  }

  const handleEdit = (item) => {
    setEditing(item)
    setForm({ title: item.title || '', description: item.description || '', eventDate: item.eventDate || '', location: item.location || '', city: item.city || '', region: item.region || '', organizer: item.organizer || '', maxParticipants: item.maxParticipants || '', status: item.status || 'UPCOMING' })
    setShowModal(true)
  }

  const handleDelete = async (id) => {
    const result = await Swal.fire({ title: 'Supprimer cet événement ?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545', confirmButtonText: 'Supprimer', cancelButtonText: 'Annuler' })
    if (result.isConfirmed) {
      try { await eventService.delete(id); loadData() } catch (err) { Swal.fire({ icon: 'error', title: 'Erreur' }) }
    }
  }

  const resetForm = () => setForm({ title: '', description: '', eventDate: '', location: '', city: '', region: '', organizer: '', maxParticipants: '', status: 'UPCOMING' })

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h2><i className="bi bi-calendar-event me-2"></i>{t('events')}</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setEditing(null); setShowModal(true) }}>
          <i className="bi bi-plus-lg me-1"></i>{t('add')}
        </button>
      </div>

      <div className="row g-3">
        {items.length === 0 ? (
          <div className="col-12"><div className="empty-state"><i className="bi bi-calendar-event"></i><p>{t('noData')}</p></div></div>
        ) : (
          items.map(item => (
            <div key={item.id} className="col-md-6 col-lg-4">
              <div className="card card-custom h-100">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <h6 className="fw-bold mb-0">{item.title}</h6>
                    <span className={`badge bg-${statusColors[item.status]}`}>{statusLabels[item.status]}</span>
                  </div>
                  <p className="text-muted small mb-2">{item.description}</p>
                  <div className="mb-1"><small><i className="bi bi-calendar me-1"></i>{item.eventDate}</small></div>
                  <div className="mb-1"><small><i className="bi bi-geo-alt me-1"></i>{item.city}, {item.region}</small></div>
                  <div className="mb-1"><small><i className="bi bi-person me-1"></i>{item.organizer}</small></div>
                  {item.maxParticipants && (
                    <div className="mb-2">
                      <div className="progress" style={{ height: '6px' }}>
                        <div className="progress-bar bg-success" style={{ width: `${Math.min(100, ((item.currentParticipants || 0) / item.maxParticipants) * 100)}%` }}></div>
                      </div>
                      <small className="text-muted">{item.currentParticipants || 0}/{item.maxParticipants} participants</small>
                    </div>
                  )}
                  <div className="btn-group btn-group-sm">
                    <button className="btn btn-outline-primary" onClick={() => handleEdit(item)}><i className="bi bi-pencil me-1"></i>{t('edit')}</button>
                    <button className="btn btn-outline-danger" onClick={() => handleDelete(item.id)}><i className="bi bi-trash me-1"></i>{t('delete')}</button>
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">{editing ? t('edit') : t('add')} Événement</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-12">
                      <label className="form-label">{t('title')}</label>
                      <input type="text" className="form-control" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
                    </div>
                    <div className="col-12">
                      <label className="form-label">{t('description')}</label>
                      <textarea className="form-control" rows="2" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}></textarea>
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">Date</label>
                      <input type="date" className="form-control" value={form.eventDate} onChange={(e) => setForm({ ...form, eventDate: e.target.value })} required />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">{t('city')}</label>
                      <input type="text" className="form-control" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">{t('region')}</label>
                      <select className="form-select" value={form.region} onChange={(e) => setForm({ ...form, region: e.target.value })}>
                        <option value="">Sélectionner</option>
                        {regions.map(r => <option key={r} value={r}>{r}</option>)}
                      </select>
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">Lieu</label>
                      <input type="text" className="form-control" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">Organisateur</label>
                      <input type="text" className="form-control" value={form.organizer} onChange={(e) => setForm({ ...form, organizer: e.target.value })} />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">Max participants</label>
                      <input type="number" className="form-control" value={form.maxParticipants} onChange={(e) => setForm({ ...form, maxParticipants: e.target.value })} />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">{t('status')}</label>
                      <select className="form-select" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                        {statuses.map(s => <option key={s} value={s}>{statusLabels[s]}</option>)}
                      </select>
                    </div>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>{t('cancel')}</button>
                  <button type="submit" className="btn btn-primary"><i className="bi bi-check-lg me-1"></i>{t('save')}</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Events
