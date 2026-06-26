import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useLanguage } from '../context/LanguageContext'
import { authService } from '../services/api'
import Swal from 'sweetalert2'

function Register() {
  const { t } = useLanguage()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', password: '', confirmPassword: '', phone: '', city: '', region: '' })

  const regions = ['Rabat-Salé-Kénitra', 'Casablanca-Settat', 'Marrakech-Safi', 'Fès-Meknès', 'Tanger-Tétouan-Al Hoceïma', 'Souss-Massa', 'Oriental', 'Béni Mellal-Khénifra', 'Drâa-Tafilalet', 'Laâyoune-Sakia El Hamra', 'Dakhla-Oued Ed-Dahab', 'Guelmim-Oued Noun']

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (form.password !== form.confirmPassword) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: 'Les mots de passe ne correspondent pas' })
      return
    }
    setLoading(true)
    try {
      await authService.register(form)
      Swal.fire({ icon: 'success', title: 'Inscription réussie !', text: 'Vous pouvez maintenant vous connecter', timer: 2000, showConfirmButton: false })
      navigate('/login')
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || "Erreur lors de l'inscription" })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-card" style={{ maxWidth: '550px' }}>
        <div className="text-center mb-4">
          <i className="bi bi-heart-pulse text-danger" style={{ fontSize: '3rem' }}></i>
          <h2>{t('register')}</h2>
          <p className="text-muted">Créer un nouveau compte</p>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="row g-3">
            <div className="col-6">
              <label className="form-label">{t('firstName')}</label>
              <input type="text" className="form-control" name="firstName" value={form.firstName} onChange={handleChange} required />
            </div>
            <div className="col-6">
              <label className="form-label">{t('lastName')}</label>
              <input type="text" className="form-control" name="lastName" value={form.lastName} onChange={handleChange} required />
            </div>
            <div className="col-12">
              <label className="form-label">{t('email')}</label>
              <input type="email" className="form-control" name="email" value={form.email} onChange={handleChange} required />
            </div>
            <div className="col-6">
              <label className="form-label">{t('password')}</label>
              <input type="password" className="form-control" name="password" value={form.password} onChange={handleChange} required minLength={6} />
            </div>
            <div className="col-6">
              <label className="form-label">Confirmer</label>
              <input type="password" className="form-control" name="confirmPassword" value={form.confirmPassword} onChange={handleChange} required />
            </div>
            <div className="col-6">
              <label className="form-label">{t('phone')}</label>
              <input type="tel" className="form-control" name="phone" value={form.phone} onChange={handleChange} />
            </div>
            <div className="col-6">
              <label className="form-label">{t('city')}</label>
              <input type="text" className="form-control" name="city" value={form.city} onChange={handleChange} />
            </div>
            <div className="col-12">
              <label className="form-label">{t('region')}</label>
              <select className="form-select" name="region" value={form.region} onChange={handleChange}>
                <option value="">Sélectionner une région</option>
                {regions.map(r => <option key={r} value={r}>{r}</option>)}
              </select>
            </div>
          </div>
          <button type="submit" className="btn btn-primary w-100 py-2 mt-4" disabled={loading}>
            {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : <i className="bi bi-person-plus me-2"></i>}
            {t('register')}
          </button>
        </form>
        <div className="text-center mt-3">
          <span className="text-muted">Déjà un compte ? </span>
          <Link to="/login" className="text-decoration-none fw-bold">{t('login')}</Link>
        </div>
      </div>
    </div>
  )
}

export default Register
