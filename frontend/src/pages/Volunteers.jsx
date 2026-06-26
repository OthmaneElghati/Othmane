import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { volunteerService } from '../services/api'
import Swal from 'sweetalert2'

function Volunteers() {
  const { t } = useLanguage()
  const [volunteers, setVolunteers] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', phone: '', address: '', city: '', region: '', skills: '', available: true })

  const regions = ['Rabat-Salé-Kénitra', 'Casablanca-Settat', 'Marrakech-Safi', 'Fès-Meknès', 'Tanger-Tétouan-Al Hoceïma', 'Souss-Massa', 'Oriental', 'Béni Mellal-Khénifra', 'Drâa-Tafilalet', 'Laâyoune-Sakia El Hamra', 'Dakhla-Oued Ed-Dahab']

  useEffect(() => { loadData() }, [])

  const loadData = async () => {
    try {
      const res = await volunteerService.getAll()
      setVolunteers(Array.isArray(res.data) ? res.data : res.data.content || [])
    } catch (err) {
      setVolunteers([])
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editing) {
        await volunteerService.update(editing.id, form)
        Swal.fire({ icon: 'success', title: 'Bénévole modifié', timer: 1500, showConfirmButton: false })
      } else {
        await volunteerService.create(form)
        Swal.fire({ icon: 'success', title: 'Bénévole ajouté', timer: 1500, showConfirmButton: false })
      }
      setShowModal(false)
      setEditing(null)
      resetForm()
      loadData()
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || 'Une erreur est survenue' })
    }
  }

  const handleEdit = (vol) => {
    setEditing(vol)
    setForm({ firstName: vol.firstName || '', lastName: vol.lastName || '', email: vol.email || '', phone: vol.phone || '', address: vol.address || '', city: vol.city || '', region: vol.region || '', skills: vol.skills || '', available: vol.available ?? true })
    setShowModal(true)
  }

  const handleDelete = async (id) => {
    const result = await Swal.fire({ title: 'Confirmer la suppression ?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545', confirmButtonText: 'Supprimer', cancelButtonText: 'Annuler' })
    if (result.isConfirmed) {
      try {
        await volunteerService.delete(id)
        Swal.fire({ icon: 'success', title: 'Supprimé', timer: 1500, showConfirmButton: false })
        loadData()
      } catch (err) {
        Swal.fire({ icon: 'error', title: 'Erreur' })
      }
    }
  }

  const resetForm = () => setForm({ firstName: '', lastName: '', email: '', phone: '', address: '', city: '', region: '', skills: '', available: true })

  const filtered = volunteers.filter(v => {
    const term = searchTerm.toLowerCase()
    return v.firstName?.toLowerCase().includes(term) || v.lastName?.toLowerCase().includes(term) || v.city?.toLowerCase().includes(term) || v.skills?.toLowerCase().includes(term)
  })

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h2><i className="bi bi-people me-2"></i>{t('volunteers')}</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setEditing(null); setShowModal(true) }}>
          <i className="bi bi-plus-lg me-1"></i>{t('add')}
        </button>
      </div>

      <div className="card card-custom mb-4">
        <div className="card-body">
          <input type="text" className="form-control" placeholder={t('search') + ' (nom, ville, compétences)...'} value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
        </div>
      </div>

      <div className="row g-3">
        {filtered.length === 0 ? (
          <div className="col-12"><div className="empty-state"><i className="bi bi-people"></i><p>{t('noData')}</p></div></div>
        ) : (
          filtered.map(vol => (
            <div key={vol.id} className="col-md-6 col-lg-4">
              <div className="card card-custom h-100">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <div>
                      <h6 className="fw-bold mb-0">{vol.firstName} {vol.lastName}</h6>
                      <small className="text-muted">{vol.email}</small>
                    </div>
                    <span className={`badge ${vol.available ? 'bg-success' : 'bg-secondary'}`}>
                      {vol.available ? t('available') : t('unavailable')}
                    </span>
                  </div>
                  <div className="mb-2">
                    <small className="text-muted"><i className="bi bi-geo-alt me-1"></i>{vol.city}, {vol.region}</small>
                  </div>
                  <div className="mb-2">
                    <small className="text-muted"><i className="bi bi-telephone me-1"></i>{vol.phone}</small>
                  </div>
                  {vol.skills && (
                    <div className="mb-2">
                      {vol.skills.split(',').map((skill, i) => (
                        <span key={i} className="badge bg-light text-dark me-1 mb-1">{skill.trim()}</span>
                      ))}
                    </div>
                  )}
                  <div className="btn-group btn-group-sm mt-2">
                    <button className="btn btn-outline-primary" onClick={() => handleEdit(vol)}><i className="bi bi-pencil me-1"></i>{t('edit')}</button>
                    <button className="btn btn-outline-danger" onClick={() => handleDelete(vol.id)}><i className="bi bi-trash me-1"></i>{t('delete')}</button>
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
                <h5 className="modal-title">{editing ? t('edit') : t('add')} Bénévole</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">{t('firstName')}</label>
                      <input type="text" className="form-control" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('lastName')}</label>
                      <input type="text" className="form-control" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('email')}</label>
                      <input type="email" className="form-control" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('phone')}</label>
                      <input type="tel" className="form-control" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
                    </div>
                    <div className="col-12">
                      <label className="form-label">{t('address')}</label>
                      <input type="text" className="form-control" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
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
                    <div className="col-md-9">
                      <label className="form-label">{t('skills')} (séparées par des virgules)</label>
                      <input type="text" className="form-control" value={form.skills} onChange={(e) => setForm({ ...form, skills: e.target.value })} placeholder="ex: Logistique, Médecine, Communication" />
                    </div>
                    <div className="col-md-3 d-flex align-items-end">
                      <div className="form-check">
                        <input className="form-check-input" type="checkbox" checked={form.available} onChange={(e) => setForm({ ...form, available: e.target.checked })} id="available" />
                        <label className="form-check-label" htmlFor="available">{t('available')}</label>
                      </div>
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

export default Volunteers
