import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { missionService } from '../services/api'
import Swal from 'sweetalert2'

function Missions() {
  const { t } = useLanguage()
  const [missions, setMissions] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingMission, setEditingMission] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [filterRegion, setFilterRegion] = useState('')
  const [filterStatus, setFilterStatus] = useState('')
  const [form, setForm] = useState({ title: '', description: '', city: '', region: '', latitude: '', longitude: '', status: 'PLANNED', priority: 'MEDIUM', startDate: '', endDate: '', budget: '' })

  const regions = ['Rabat-Salé-Kénitra', 'Casablanca-Settat', 'Marrakech-Safi', 'Fès-Meknès', 'Tanger-Tétouan-Al Hoceïma', 'Souss-Massa', 'Oriental', 'Béni Mellal-Khénifra', 'Drâa-Tafilalet', 'Laâyoune-Sakia El Hamra', 'Dakhla-Oued Ed-Dahab']
  const statuses = ['PLANNED', 'ACTIVE', 'COMPLETED', 'CANCELLED']
  const priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']

  useEffect(() => { loadMissions() }, [])

  const loadMissions = async () => {
    try {
      const res = await missionService.getAll()
      setMissions(Array.isArray(res.data) ? res.data : res.data.content || [])
    } catch (err) {
      console.error('Failed to load missions:', err)
      setMissions([])
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editingMission) {
        await missionService.update(editingMission.id, form)
        Swal.fire({ icon: 'success', title: 'Mission modifiée', timer: 1500, showConfirmButton: false })
      } else {
        await missionService.create(form)
        Swal.fire({ icon: 'success', title: 'Mission créée', timer: 1500, showConfirmButton: false })
      }
      setShowModal(false)
      setEditingMission(null)
      resetForm()
      loadMissions()
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || 'Une erreur est survenue' })
    }
  }

  const handleEdit = (mission) => {
    setEditingMission(mission)
    setForm({ title: mission.title || '', description: mission.description || '', city: mission.city || '', region: mission.region || '', latitude: mission.latitude || '', longitude: mission.longitude || '', status: mission.status || 'PLANNED', priority: mission.priority || 'MEDIUM', startDate: mission.startDate || '', endDate: mission.endDate || '', budget: mission.budget || '' })
    setShowModal(true)
  }

  const handleDelete = async (id) => {
    const result = await Swal.fire({ title: 'Confirmer la suppression ?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545', confirmButtonText: 'Supprimer', cancelButtonText: 'Annuler' })
    if (result.isConfirmed) {
      try {
        await missionService.delete(id)
        Swal.fire({ icon: 'success', title: 'Supprimée', timer: 1500, showConfirmButton: false })
        loadMissions()
      } catch (err) {
        Swal.fire({ icon: 'error', title: 'Erreur', text: 'Impossible de supprimer cette mission' })
      }
    }
  }

  const resetForm = () => setForm({ title: '', description: '', city: '', region: '', latitude: '', longitude: '', status: 'PLANNED', priority: 'MEDIUM', startDate: '', endDate: '', budget: '' })

  const getStatusBadge = (status) => {
    const colors = { PLANNED: 'warning', ACTIVE: 'success', COMPLETED: 'info', CANCELLED: 'danger' }
    const labels = { PLANNED: t('planned'), ACTIVE: t('active'), COMPLETED: t('completed'), CANCELLED: t('cancelled') }
    return <span className={`badge bg-${colors[status] || 'secondary'}`}>{labels[status] || status}</span>
  }

  const getPriorityBadge = (priority) => {
    const colors = { LOW: 'secondary', MEDIUM: 'primary', HIGH: 'warning', CRITICAL: 'danger' }
    const labels = { LOW: t('low'), MEDIUM: t('medium'), HIGH: t('high'), CRITICAL: t('critical') }
    return <span className={`badge bg-${colors[priority] || 'secondary'}`}>{labels[priority] || priority}</span>
  }

  const filteredMissions = missions.filter(m => {
    const matchSearch = m.title?.toLowerCase().includes(searchTerm.toLowerCase()) || m.city?.toLowerCase().includes(searchTerm.toLowerCase())
    const matchRegion = !filterRegion || m.region === filterRegion
    const matchStatus = !filterStatus || m.status === filterStatus
    return matchSearch && matchRegion && matchStatus
  })

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h2><i className="bi bi-bullseye me-2"></i>{t('missions')}</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setEditingMission(null); setShowModal(true) }}>
          <i className="bi bi-plus-lg me-1"></i>{t('add')}
        </button>
      </div>

      <div className="card card-custom mb-4">
        <div className="card-body">
          <div className="row g-2">
            <div className="col-md-4">
              <input type="text" className="form-control" placeholder={t('search') + '...'} value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
            </div>
            <div className="col-md-4">
              <select className="form-select" value={filterRegion} onChange={(e) => setFilterRegion(e.target.value)}>
                <option value="">{t('allRegions')}</option>
                {regions.map(r => <option key={r} value={r}>{r}</option>)}
              </select>
            </div>
            <div className="col-md-4">
              <select className="form-select" value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
                <option value="">{t('allStatuses')}</option>
                {statuses.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          </div>
        </div>
      </div>

      <div className="card card-custom">
        <div className="table-responsive">
          <table className="table table-custom table-hover mb-0">
            <thead>
              <tr>
                <th>{t('title')}</th>
                <th>{t('city')}</th>
                <th>{t('region')}</th>
                <th>{t('status')}</th>
                <th>{t('priority')}</th>
                <th>{t('budget')}</th>
                <th>{t('actions')}</th>
              </tr>
            </thead>
            <tbody>
              {filteredMissions.length === 0 ? (
                <tr><td colSpan="7" className="text-center py-4 text-muted">{t('noData')}</td></tr>
              ) : (
                filteredMissions.map(mission => (
                  <tr key={mission.id}>
                    <td className="fw-bold">{mission.title}</td>
                    <td>{mission.city}</td>
                    <td><small>{mission.region}</small></td>
                    <td>{getStatusBadge(mission.status)}</td>
                    <td>{getPriorityBadge(mission.priority)}</td>
                    <td>{mission.budget ? `${mission.budget.toLocaleString()} MAD` : '-'}</td>
                    <td>
                      <div className="btn-group btn-group-sm">
                        <button className="btn btn-outline-primary" onClick={() => handleEdit(mission)}><i className="bi bi-pencil"></i></button>
                        <button className="btn btn-outline-danger" onClick={() => handleDelete(mission.id)}><i className="bi bi-trash"></i></button>
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
                <h5 className="modal-title">{editingMission ? t('edit') : t('add')} Mission</h5>
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
                      <textarea className="form-control" rows="3" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}></textarea>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('city')}</label>
                      <input type="text" className="form-control" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('region')}</label>
                      <select className="form-select" value={form.region} onChange={(e) => setForm({ ...form, region: e.target.value })}>
                        <option value="">Sélectionner</option>
                        {regions.map(r => <option key={r} value={r}>{r}</option>)}
                      </select>
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">Latitude</label>
                      <input type="number" step="any" className="form-control" value={form.latitude} onChange={(e) => setForm({ ...form, latitude: e.target.value })} />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">Longitude</label>
                      <input type="number" step="any" className="form-control" value={form.longitude} onChange={(e) => setForm({ ...form, longitude: e.target.value })} />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">{t('status')}</label>
                      <select className="form-select" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                        {statuses.map(s => <option key={s} value={s}>{s}</option>)}
                      </select>
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">{t('priority')}</label>
                      <select className="form-select" value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })}>
                        {priorities.map(p => <option key={p} value={p}>{p}</option>)}
                      </select>
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">{t('startDate')}</label>
                      <input type="date" className="form-control" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">{t('endDate')}</label>
                      <input type="date" className="form-control" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label">{t('budget')} (MAD)</label>
                      <input type="number" className="form-control" value={form.budget} onChange={(e) => setForm({ ...form, budget: e.target.value })} />
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

export default Missions
