import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { donationService } from '../services/api'
import Swal from 'sweetalert2'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend } from 'chart.js'
import { Bar, Doughnut } from 'react-chartjs-2'

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend)

function Donations() {
  const { t } = useLanguage()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [form, setForm] = useState({ donorName: '', donorEmail: '', amount: '', currency: 'MAD', paymentMethod: 'CASH', description: '', status: 'COMPLETED' })

  const paymentMethods = ['CMI', 'PAYPAL', 'STRIPE', 'BANK_TRANSFER', 'CASH']
  const methodLabels = { CMI: 'CMI Maroc', PAYPAL: 'PayPal', STRIPE: 'Stripe', BANK_TRANSFER: 'Virement', CASH: 'Espèces' }

  useEffect(() => { loadData() }, [])

  const loadData = async () => {
    try {
      const res = await donationService.getAll()
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
      await donationService.create(form)
      Swal.fire({ icon: 'success', title: 'Donation enregistrée', timer: 1500, showConfirmButton: false })
      setShowModal(false)
      resetForm()
      loadData()
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || 'Erreur' })
    }
  }

  const handleDelete = async (id) => {
    const result = await Swal.fire({ title: 'Supprimer cette donation ?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545', confirmButtonText: 'Supprimer', cancelButtonText: 'Annuler' })
    if (result.isConfirmed) {
      try {
        await donationService.delete(id)
        loadData()
      } catch (err) {
        Swal.fire({ icon: 'error', title: 'Erreur' })
      }
    }
  }

  const resetForm = () => setForm({ donorName: '', donorEmail: '', amount: '', currency: 'MAD', paymentMethod: 'CASH', description: '', status: 'COMPLETED' })

  const totalAmount = items.reduce((sum, d) => sum + (d.amount || 0), 0)
  const avgAmount = items.length > 0 ? totalAmount / items.length : 0

  const methodStats = paymentMethods.reduce((acc, m) => {
    acc[m] = items.filter(d => d.paymentMethod === m).length
    return acc
  }, {})

  const methodChartData = {
    labels: paymentMethods.map(m => methodLabels[m]),
    datasets: [{ data: Object.values(methodStats), backgroundColor: ['#c1272d', '#0070ba', '#6772e5', '#006233', '#f0c808'] }]
  }

  const filtered = items.filter(d => d.donorName?.toLowerCase().includes(searchTerm.toLowerCase()))

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h2><i className="bi bi-cash-coin me-2"></i>{t('donations')}</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setShowModal(true) }}>
          <i className="bi bi-plus-lg me-1"></i>{t('add')}
        </button>
      </div>

      <div className="row g-3 mb-4">
        <div className="col-md-4">
          <div className="card stat-card">
            <div className="card-body text-center">
              <h3 className="fw-bold text-success">{totalAmount.toLocaleString()} MAD</h3>
              <small className="text-muted">Total des donations</small>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card stat-card">
            <div className="card-body text-center">
              <h3 className="fw-bold text-primary">{items.length}</h3>
              <small className="text-muted">Nombre de donations</small>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card stat-card">
            <div className="card-body text-center">
              <h3 className="fw-bold text-info">{avgAmount.toFixed(0)} MAD</h3>
              <small className="text-muted">Moyenne par donation</small>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4 mb-4">
        <div className="col-md-5">
          <div className="card card-custom">
            <div className="card-header bg-white border-0 pt-3">
              <h6 className="fw-bold">Par méthode de paiement</h6>
            </div>
            <div className="card-body" style={{ height: '250px' }}>
              <Doughnut data={methodChartData} options={{ responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }} />
            </div>
          </div>
        </div>
        <div className="col-md-7">
          <div className="card card-custom">
            <div className="card-body">
              <input type="text" className="form-control mb-3" placeholder={t('search') + '...'} value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
              <div className="table-responsive" style={{ maxHeight: '280px', overflowY: 'auto' }}>
                <table className="table table-sm table-hover">
                  <thead className="table-light">
                    <tr><th>{t('donor')}</th><th>{t('amount')}</th><th>{t('paymentMethod')}</th><th></th></tr>
                  </thead>
                  <tbody>
                    {filtered.slice(0, 20).map(d => (
                      <tr key={d.id}>
                        <td>{d.donorName}</td>
                        <td className="fw-bold">{d.amount?.toLocaleString()} MAD</td>
                        <td><span className="badge bg-light text-dark">{methodLabels[d.paymentMethod] || d.paymentMethod}</span></td>
                        <td><button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(d.id)}><i className="bi bi-trash"></i></button></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Nouvelle Donation</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-12">
                      <label className="form-label">Nom du donateur</label>
                      <input type="text" className="form-control" value={form.donorName} onChange={(e) => setForm({ ...form, donorName: e.target.value })} required />
                    </div>
                    <div className="col-12">
                      <label className="form-label">Email du donateur</label>
                      <input type="email" className="form-control" value={form.donorEmail} onChange={(e) => setForm({ ...form, donorEmail: e.target.value })} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('amount')} (MAD)</label>
                      <input type="number" className="form-control" value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} required min="1" />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">{t('paymentMethod')}</label>
                      <select className="form-select" value={form.paymentMethod} onChange={(e) => setForm({ ...form, paymentMethod: e.target.value })}>
                        {paymentMethods.map(m => <option key={m} value={m}>{methodLabels[m]}</option>)}
                      </select>
                    </div>
                    <div className="col-12">
                      <label className="form-label">{t('description')}</label>
                      <textarea className="form-control" rows="2" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}></textarea>
                    </div>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>{t('cancel')}</button>
                  <button type="submit" className="btn btn-success"><i className="bi bi-check-lg me-1"></i>Enregistrer</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Donations
