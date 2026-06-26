import { Link } from 'react-router-dom'
import { useLanguage } from '../context/LanguageContext'
import { useEffect } from 'react'

function Landing() {
  const { t } = useLanguage()

  useEffect(() => {
    if (window.AOS) window.AOS.init({ duration: 1000, once: true })
  }, [])

  const features = [
    { icon: 'bi-bullseye', title: 'Gestion des Missions', desc: 'Planifiez et suivez vos missions humanitaires sur tout le territoire marocain', color: '#c1272d' },
    { icon: 'bi-people', title: 'Gestion des Bénévoles', desc: 'Recrutez, formez et affectez des bénévoles aux missions selon leurs compétences', color: '#006233' },
    { icon: 'bi-cash-coin', title: 'Gestion des Donations', desc: 'Suivez les donations et gérez les paiements en toute sécurité', color: '#f0c808' },
    { icon: 'bi-truck', title: 'Suivi des Convois', desc: 'Suivez en temps réel vos convois humanitaires sur la carte du Maroc', color: '#17a2b8' },
    { icon: 'bi-geo-alt', title: 'Cartographie Interactive', desc: 'Visualisez toutes vos missions et convois sur une carte interactive', color: '#28a745' },
    { icon: 'bi-bar-chart', title: 'Statistiques Avancées', desc: 'Tableaux de bord et rapports détaillés pour le suivi des performances', color: '#6f42c1' }
  ]

  return (
    <div>
      <nav className="navbar navbar-expand-lg navbar-dark fixed-top" style={{ background: 'rgba(0,0,0,0.3)', backdropFilter: 'blur(10px)' }}>
        <div className="container">
          <a className="navbar-brand fw-bold" href="/">
            <i className="bi bi-heart-pulse me-2"></i>Humanitaire Maroc
          </a>
          <div className="d-flex gap-2">
            <Link to="/login" className="btn btn-outline-light btn-sm">{t('login')}</Link>
            <Link to="/register" className="btn btn-light btn-sm text-danger fw-bold">{t('register')}</Link>
          </div>
        </div>
      </nav>

      <section className="landing-hero">
        <div className="container">
          <div className="row align-items-center">
            <div className="col-lg-7" data-aos="fade-right">
              <h1>{t('heroTitle')}</h1>
              <p className="lead mb-4">{t('heroSubtitle')}</p>
              <div className="d-flex gap-3">
                <Link to="/register" className="btn btn-light btn-lg text-danger fw-bold px-4">
                  <i className="bi bi-rocket-takeoff me-2"></i>{t('getStarted')}
                </Link>
                <a href="#features" className="btn btn-outline-light btn-lg px-4">
                  <i className="bi bi-info-circle me-2"></i>{t('learnMore')}
                </a>
              </div>
              <div className="mt-5 d-flex gap-4">
                <div><h3 className="mb-0 fw-bold">30+</h3><small className="opacity-75">Missions Actives</small></div>
                <div><h3 className="mb-0 fw-bold">50+</h3><small className="opacity-75">Bénévoles</small></div>
                <div><h3 className="mb-0 fw-bold">12</h3><small className="opacity-75">Régions Couvertes</small></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section id="features" className="py-5 bg-white">
        <div className="container">
          <div className="text-center mb-5" data-aos="fade-up">
            <h2 className="fw-bold text-dark">Fonctionnalités Principales</h2>
            <p className="text-muted">Une plateforme complète pour gérer vos actions humanitaires</p>
          </div>
          <div className="row g-4">
            {features.map((f, i) => (
              <div key={i} className="col-md-6 col-lg-4" data-aos="fade-up" data-aos-delay={i * 100}>
                <div className="feature-card card h-100">
                  <div className="feature-icon" style={{ backgroundColor: `${f.color}15`, color: f.color }}>
                    <i className={`bi ${f.icon}`}></i>
                  </div>
                  <h5 className="fw-bold">{f.title}</h5>
                  <p className="text-muted mb-0">{f.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-5" style={{ background: 'linear-gradient(135deg, #1a1a2e, #16213e)' }}>
        <div className="container text-center text-white">
          <h2 className="fw-bold mb-3" data-aos="fade-up">Prêt à faire la différence ?</h2>
          <p className="lead mb-4 opacity-75" data-aos="fade-up">Rejoignez notre plateforme et contribuez aux actions humanitaires au Maroc</p>
          <Link to="/register" className="btn btn-danger btn-lg px-5" data-aos="fade-up">
            Créer un compte gratuitement
          </Link>
        </div>
      </section>

      <footer className="py-4 bg-dark text-white text-center">
        <div className="container">
          <p className="mb-1"><i className="bi bi-heart-pulse me-2"></i>Plateforme Humanitaire Maroc &copy; 2024</p>
          <small className="opacity-50">Projet de Fin d'Études - Gestion des Missions Humanitaires</small>
        </div>
      </footer>
    </div>
  )
}

export default Landing
