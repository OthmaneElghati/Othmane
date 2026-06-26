import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { convoyService } from '../services/api'
import Swal from 'sweetalert2'

function Convoys() {
  const { t } = useLanguage()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [filterStatus, setFilterStatus] = useState('')
  const [form, setForm] = useState({ name: '', departureCity: '', destinationCity: '', departureLatitude: '', departureLongitude: '', destinationLatitude: '', destinationLongitude: '', status: 'PENDING', cargo: '', description: '' })

  const statuses = ['PENDING', 'IN_TRANSIT', 'DELIVERED', 'DELAYED']
  const statusLabels = { PENDING: t('pending'), IN_TRANSIT: t('inTransit'), DELIVERED: t('delivered'), DELAYED: t('delayed') }
  const statusColors = { PENDING: 'warning', IN_TRANSIT: 'primary', DELIVERED: 'success', DELAYED: 'danger' }

  useEffect(() => { loadData() }, [])

  const loadData = async () => {
    try {
      const res = await convoyService.getAll()
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
        await convoyService.update(editing.id, form)
        Swal.fire({ icon: 'success', title: 'Convoi modifié', timer: 1500, showConfirmButton: false })
      } else {
        await convoyService.create(form)
        Swal.fire({ icon: 'success', title: 'Convoi créé', timer: 1500, showConfirmButton: false })
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
    setForm({ name: item.name || '', departureCity: item.departureCity || '', destinationCity: item.destinationCity || '', departureLatitude: item.departureLatitude || '', departureLongitude: item.departureLongitude || '', destinationLatitude: item.destinationLatitude || '', destinationLongitude: item.destinationLongitude || '', status: item.status || 'PENDING', cargo: item.cargo || '', description: item.description || '' })
    setShowModal(true)
  }

  const handleDelete = async (id) => {
    const result = await Swal.fire({ title: 'Supprimer ce convoi ?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545', confirmButtonText: 'Supprimer', cancelButtonText: 'Annuler' })
    if (result.isConfirmed) {
      try { await convoyService.delete(id); loadData() } catch (err) { Swal.fire({ icon: 'error', title: 'Erreur' }) }
    }
  }

  const resetForm = () => setForm({ name: '', departureCity: '', destinationCity: '', departureLatitude: '', departureLongitude: '', destinationLatitude: '', destinationLongitude: '', status: 'PENDING', cargo: '', description: '' })

  const filtered = items.filter(i => !filterStatus || i.status === filterStatus)

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h2><i className="bi bi-truck me-2"></i>{t('convoys')}</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setEditing(null); setShowModal(true) }}>
          <i className="bi bi-plus-lg me-1"></i>{t('add')}
        </button>
      </div>

      <div className="row g-3 mb-4">
        {statuses.map(s => (
          <div key={s} className="col-6 col-md-3">
            <div className={`card stat-card cursor-pointer ${filterStatus === s ? 'border border-2 border-primary' : ''}`} onClick={() => setFilterStatus(filterStatus === s ? '' : s)} style={{ cursor: 'pointer' }}>
              <div className="card-body text-center py-3">
                <h4 className={`fw-bold text-${statusColors[s]}`}>{items.filter(i => i.status === s).length}</h4>
                <small className="text-muted">{statusLabels[s]}</small>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="card card-custom">
        <div className="table-responsive">
          <table className="table table-custom table-hover mb-0">
            <thead>
              <tr>
                <th>Nom</th>
                <th>{t('departureCity')}</th>
                <th>{t('destinationCity')}</th>
                <th>{t('cargo')}</th>
                <th>{t('status')}</th>
                <th>{t('actions')}</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr><td colSpan="6" className="text-center py-4 text-muted">{t('noData')}</td></tr>
              ) : (
                filtered.map(item => (
                  <tr key={item.id}>
                    <td className="fw-bold">{item.name}</td>
                    <td><i className="bi bi-geo-alt text-success me-1"></i>{item.departureCity}</td>
                    <td><i className="bi bi-geo-alt-fill text-danger me-1"></i>{item.destinationCity}</td>
                    <td><small>{item.cargo}</small></td>
                    <td><span className={`badge bg-${statusColors[item.status]}`}>{statusLabels[item.status] || item.status}</span></td>
                    <td>
                      <div className="btn-group btn-group-sm">
                        <button className="btn btn-outline-primary" onClick={() => handleEdit(item)}><i className="bi bi-pencil"></i></button>
                        <button className="btn btn-outline-danger" onClick={() => handleDelete(item.id)}><i className="bi bi-trash"></i></button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">{editing ? t('edit') : t('add')} Convoi</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-12">
                      <label className="form-label">Nom du convoi</label>
                      <input type="text" className="form-control" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('departureCity')}</label>
                      <input type="text" className="form-control" value={form.departureCity} onChange={(e) => setForm({ ...form, departureCity: e.target.value })} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('destinationCity')}</label>
                      <input type="text" className="form-control" value={form.destinationCity} onChange={(e) => setForm({ ...form, destinationCity: e.target.value })} required />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">Lat. départ</label>
                      <input type="number" step="any" className="form-control" value={form.departureLatitude} onChange={(e) => setForm({ ...form, departureLatitude: e.target.value })} />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">Lng. départ</label>
                      <input type="number" step="any" className="form-control" value={form.departureLongitude} onChange={(e) => setForm({ ...form, departureLongitude: e.target.value })} />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">Lat. destination</label>
                      <input type="number" step="any" className="form-control" value={form.destinationLatitude} onChange={(e) => setForm({ ...form, destinationLatitude: e.target.value })} />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">Lng. destination</label>
                      <input type="number" step="any" className="form-control" value={form.destinationLongitude} onChange={(e) => setForm({ ...form, destinationLongitude: e.target.value })} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('status')}</label>
                      <select className="form-select" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                        {statuses.map(s => <option key={s} value={s}>{statusLabels[s]}</option>)}
                      </select>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('cargo')}</label>
                      <input type="text" className="form-control" value={form.cargo} onChange={(e) => setForm({ ...form, cargo: e.target.value })} />
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

export default Convoys
