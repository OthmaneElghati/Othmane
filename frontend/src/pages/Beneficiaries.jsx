import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { beneficiaryService } from '../services/api'
import Swal from 'sweetalert2'

function Beneficiaries() {
  const { t } = useLanguage()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [form, setForm] = useState({ fullName: '', familySize: '', emergencyLevel: 'MEDIUM', address: '', city: '', region: '', phone: '', needs: '' })

  const regions = ['Rabat-Salé-Kénitra', 'Casablanca-Settat', 'Marrakech-Safi', 'Fès-Meknès', 'Tanger-Tétouan-Al Hoceïma', 'Souss-Massa', 'Oriental', 'Béni Mellal-Khénifra', 'Drâa-Tafilalet', 'Laâyoune-Sakia El Hamra', 'Dakhla-Oued Ed-Dahab']
  const levels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']

  useEffect(() => { loadData() }, [])

  const loadData = async () => {
    try {
      const res = await beneficiaryService.getAll()
      setItems(Array.isArray(res.data) ? res.data : res.data.content || [])
    } catch (err) {
      console.error('Failed to load beneficiaries:', err)
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editing) {
        await beneficiaryService.update(editing.id, form)
        Swal.fire({ icon: 'success', title: 'Bénéficiaire modifié', timer: 1500, showConfirmButton: false })
      } else {
        await beneficiaryService.create(form)
        Swal.fire({ icon: 'success', title: 'Bénéficiaire ajouté', timer: 1500, showConfirmButton: false })
      }
      setShowModal(false)
      setEditing(null)
      resetForm()
      loadData()
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || 'Une erreur est survenue' })
    }
  }

  const handleEdit = (item) => {
    setEditing(item)
    setForm({ fullName: item.fullName || '', familySize: item.familySize || '', emergencyLevel: item.emergencyLevel || 'MEDIUM', address: item.address || '', city: item.city || '', region: item.region || '', phone: item.phone || '', needs: item.needs || '' })
    setShowModal(true)
  }

  const handleDelete = async (id) => {
    const result = await Swal.fire({ title: 'Confirmer la suppression ?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545', confirmButtonText: 'Supprimer', cancelButtonText: 'Annuler' })
    if (result.isConfirmed) {
      try {
        await beneficiaryService.delete(id)
        loadData()
      } catch (err) {
        Swal.fire({ icon: 'error', title: 'Erreur' })
      }
    }
  }

  const resetForm = () => setForm({ fullName: '', familySize: '', emergencyLevel: 'MEDIUM', address: '', city: '', region: '', phone: '', needs: '' })

  const getLevelBadge = (level) => {
    const colors = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger', CRITICAL: 'dark' }
    return <span className={`badge bg-${colors[level] || 'secondary'}`}>{t(level?.toLowerCase()) || level}</span>
  }

  const filtered = items.filter(i => i.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) || i.city?.toLowerCase().includes(searchTerm.toLowerCase()))

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h2><i className="bi bi-heart me-2"></i>{t('beneficiaries')}</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setEditing(null); setShowModal(true) }}>
          <i className="bi bi-plus-lg me-1"></i>{t('add')}
        </button>
      </div>

      <div className="card card-custom mb-4">
        <div className="card-body">
          <input type="text" className="form-control" placeholder={t('search') + '...'} value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
        </div>
      </div>

      <div className="card card-custom">
        <div className="table-responsive">
          <table className="table table-custom table-hover mb-0">
            <thead>
              <tr>
                <th>Nom</th>
                <th>{t('city')}</th>
                <th>{t('familySize')}</th>
                <th>{t('emergencyLevel')}</th>
                <th>Besoins</th>
                <th>{t('actions')}</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr><td colSpan="6" className="text-center py-4 text-muted">{t('noData')}</td></tr>
              ) : (
                filtered.slice(0, 50).map(item => (
                  <tr key={item.id}>
                    <td className="fw-bold">{item.fullName}</td>
                    <td>{item.city}</td>
                    <td><span className="badge bg-light text-dark">{item.familySize} pers.</span></td>
                    <td>{getLevelBadge(item.emergencyLevel)}</td>
                    <td><small>{item.needs}</small></td>
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
                <h5 className="modal-title">{editing ? t('edit') : t('add')} Bénéficiaire</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">Nom complet</label>
                      <input type="text" className="form-control" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} required />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">{t('familySize')}</label>
                      <input type="number" className="form-control" value={form.familySize} onChange={(e) => setForm({ ...form, familySize: e.target.value })} required min="1" />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label">{t('emergencyLevel')}</label>
                      <select className="form-select" value={form.emergencyLevel} onChange={(e) => setForm({ ...form, emergencyLevel: e.target.value })}>
                        {levels.map(l => <option key={l} value={l}>{l}</option>)}
                      </select>
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
                    <div className="col-12">
                      <label className="form-label">{t('address')}</label>
                      <input type="text" className="form-control" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('phone')}</label>
                      <input type="tel" className="form-control" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Besoins</label>
                      <input type="text" className="form-control" value={form.needs} onChange={(e) => setForm({ ...form, needs: e.target.value })} placeholder="Alimentaire, Médical, Vêtements..." />
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

export default Beneficiaries
