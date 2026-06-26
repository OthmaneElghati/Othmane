import { useState } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { reportService } from '../services/api'
import Swal from 'sweetalert2'

function Reports() {
  const { t } = useLanguage()
  const [generating, setGenerating] = useState('')

  const generateReport = async (type) => {
    setGenerating(type)
    try {
      const res = await reportService.generate(type)
      if (res.data instanceof Blob) {
        const url = window.URL.createObjectURL(new Blob([res.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `rapport_${type}_${new Date().toISOString().split('T')[0]}.pdf`)
        document.body.appendChild(link)
        link.click()
        link.remove()
        window.URL.revokeObjectURL(url)
      }
      Swal.fire({ icon: 'success', title: 'Rapport généré', text: `Le rapport ${type} a été téléchargé.`, timer: 2000, showConfirmButton: false })
    } catch (err) {
      console.error('Failed to generate report:', err)
      Swal.fire({ icon: 'error', title: 'Erreur', text: err.response?.data?.message || 'Impossible de générer le rapport' })
    } finally {
      setGenerating('')
    }
  }

  const reports = [
    { type: 'missions', icon: 'bi-bullseye', color: 'danger', title: 'Rapport des Missions', desc: 'Liste complète des missions humanitaires avec statuts, budgets et résultats. Inclut les statistiques par région et par priorité.' },
    { type: 'volunteers', icon: 'bi-people', color: 'primary', title: 'Rapport des Bénévoles', desc: 'Inventaire des bénévoles inscrits avec compétences, disponibilités et affectations. Statistiques de participation.' },
    { type: 'donations', icon: 'bi-cash-coin', color: 'success', title: 'Rapport des Donations', desc: 'Historique complet des donations reçues. Répartition par méthode de paiement, montants cumulés et moyennes.' },
    { type: 'beneficiaries', icon: 'bi-heart', color: 'info', title: 'Rapport des Bénéficiaires', desc: 'Liste des bénéficiaires assistés avec niveaux d\'urgence, tailles des familles et besoins identifiés.' },
    { type: 'convoys', icon: 'bi-truck', color: 'warning', title: 'Rapport Logistique', desc: 'Suivi des convois humanitaires. Routes, cargaisons, statuts de livraison et délais.' },
    { type: 'monthly', icon: 'bi-calendar-month', color: 'secondary', title: 'Rapport Mensuel', desc: 'Synthèse mensuelle complète: missions, bénévoles, donations, événements. Vue d\'ensemble de l\'activité.' }
  ]

  return (
    <div>
      <div className="page-header mb-4">
        <h2><i className="bi bi-file-earmark-pdf me-2"></i>{t('reports')}</h2>
        <p className="text-muted mt-1">Générez des rapports PDF professionnels pour vos activités humanitaires</p>
      </div>

      <div className="row g-4">
        {reports.map(report => (
          <div key={report.type} className="col-md-6 col-lg-4">
            <div className="card card-custom h-100">
              <div className="card-body d-flex flex-column">
                <div className="d-flex align-items-center mb-3">
                  <div className={`rounded-circle bg-${report.color} bg-opacity-10 p-3 me-3`}>
                    <i className={`bi ${report.icon} text-${report.color} fs-4`}></i>
                  </div>
                  <h6 className="fw-bold mb-0">{report.title}</h6>
                </div>
                <p className="text-muted small flex-grow-1">{report.desc}</p>
                <button
                  className={`btn btn-${report.color} w-100`}
                  onClick={() => generateReport(report.type)}
                  disabled={generating === report.type}
                >
                  {generating === report.type ? (
                    <><span className="spinner-border spinner-border-sm me-1"></span>Génération...</>
                  ) : (
                    <><i className="bi bi-download me-1"></i>Générer PDF</>
                  )}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="card card-custom mt-4">
        <div className="card-body">
          <h6 className="fw-bold mb-3"><i className="bi bi-info-circle me-2"></i>Informations sur les rapports</h6>
          <div className="row g-3">
            <div className="col-md-4">
              <div className="d-flex align-items-start">
                <i className="bi bi-file-earmark-pdf text-danger me-2 mt-1"></i>
                <div><strong>Format PDF</strong><p className="small text-muted mb-0">Tous les rapports sont générés au format PDF professionnel, prêts à imprimer.</p></div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="d-flex align-items-start">
                <i className="bi bi-table text-primary me-2 mt-1"></i>
                <div><strong>Tableaux détaillés</strong><p className="small text-muted mb-0">Données organisées en tableaux avec en-têtes, totaux et statistiques.</p></div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="d-flex align-items-start">
                <i className="bi bi-graph-up text-success me-2 mt-1"></i>
                <div><strong>Statistiques incluses</strong><p className="small text-muted mb-0">Chaque rapport inclut des statistiques et métriques clés pour l'analyse.</p></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Reports
