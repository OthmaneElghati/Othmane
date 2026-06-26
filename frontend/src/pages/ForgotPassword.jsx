import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useLanguage } from '../context/LanguageContext'
import { authService } from '../services/api'
import Swal from 'sweetalert2'

function ForgotPassword() {
  const { t } = useLanguage()
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await authService.forgotPassword(email)
      Swal.fire({ icon: 'success', title: 'Email envoyé', text: 'Vérifiez votre boîte mail pour réinitialiser votre mot de passe' })
    } catch (err) {
      Swal.fire({ icon: 'info', title: 'Info', text: 'Si cet email existe, un lien de réinitialisation a été envoyé' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="text-center mb-4">
          <i className="bi bi-key text-danger" style={{ fontSize: '3rem' }}></i>
          <h2>{t('forgotPassword')}</h2>
          <p className="text-muted">Entrez votre email pour recevoir un lien de réinitialisation</p>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">{t('email')}</label>
            <input type="email" className="form-control" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="exemple@email.com" required />
          </div>
          <button type="submit" className="btn btn-primary w-100 py-2" disabled={loading}>
            {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : <i className="bi bi-send me-2"></i>}
            Envoyer le lien
          </button>
        </form>
        <div className="text-center mt-3">
          <Link to="/login" className="text-decoration-none"><i className="bi bi-arrow-left me-1"></i>Retour à la connexion</Link>
        </div>
      </div>
    </div>
  )
}

export default ForgotPassword
