import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useLanguage } from '../context/LanguageContext'
import { authService } from '../services/api'
import Swal from 'sweetalert2'

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const { t } = useLanguage()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const res = await authService.login({ email, password })
      login(res.data, res.data.token)
      Swal.fire({ icon: 'success', title: t('welcome') + ' !', text: `${res.data.firstName} ${res.data.lastName}`, timer: 2000, showConfirmButton: false })
      navigate('/app')
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || 'Email ou mot de passe incorrect' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="text-center mb-4">
          <i className="bi bi-heart-pulse text-danger" style={{ fontSize: '3rem' }}></i>
          <h2>{t('login')}</h2>
          <p className="text-muted">Plateforme Humanitaire Maroc</p>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">{t('email')}</label>
            <div className="input-group">
              <span className="input-group-text"><i className="bi bi-envelope"></i></span>
              <input type="email" className="form-control" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="exemple@email.com" required />
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label">{t('password')}</label>
            <div className="input-group">
              <span className="input-group-text"><i className="bi bi-lock"></i></span>
              <input type="password" className="form-control" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="********" required />
            </div>
          </div>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <div className="form-check">
              <input className="form-check-input" type="checkbox" id="remember" />
              <label className="form-check-label" htmlFor="remember">{t('rememberMe')}</label>
            </div>
            <Link to="/forgot-password" className="text-decoration-none small">{t('forgotPassword')}</Link>
          </div>
          <button type="submit" className="btn btn-primary w-100 py-2" disabled={loading}>
            {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : <i className="bi bi-box-arrow-in-right me-2"></i>}
            {t('login')}
          </button>
        </form>
        <div className="text-center mt-4">
          <span className="text-muted">Pas encore de compte ? </span>
          <Link to="/register" className="text-decoration-none fw-bold">{t('register')}</Link>
        </div>
        <div className="mt-4 p-3 bg-light rounded">
          <small className="text-muted d-block mb-1"><strong>Comptes de démonstration :</strong></small>
          <small className="text-muted d-block">Admin: admin@humanitaire.ma / admin123</small>
          <small className="text-muted d-block">Manager: manager@humanitaire.ma / manager123</small>
          <small className="text-muted d-block">Bénévole: volunteer@humanitaire.ma / volunteer123</small>
          <small className="text-muted d-block">Donateur: donor@humanitaire.ma / donor123</small>
        </div>
      </div>
    </div>
  )
}

export default Login
