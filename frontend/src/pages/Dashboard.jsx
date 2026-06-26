import { useState, useEffect } from 'react'
import { useLanguage } from '../context/LanguageContext'
import { useAuth } from '../context/AuthContext'
import { dashboardService } from '../services/api'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, LineElement, PointElement, ArcElement, Title, Tooltip, Legend, Filler } from 'chart.js'
import { Bar, Line, Pie, Doughnut } from 'react-chartjs-2'

ChartJS.register(CategoryScale, LinearScale, BarElement, LineElement, PointElement, ArcElement, Title, Tooltip, Legend, Filler)

function Dashboard() {
  const { t } = useLanguage()
  const { user } = useAuth()
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadStats()
  }, [])

  const loadStats = async () => {
    try {
      const res = await dashboardService.getStats()
      setStats(res.data)
    } catch (err) {
      setStats({
        totalMissions: 30, activeMissions: 12, completedMissions: 6, totalVolunteers: 50,
        totalBeneficiaries: 100, totalDonations: 100, totalDonationAmount: 524000,
        totalEvents: 15, totalConvoys: 20, monthlyDonations: [45000, 52000, 38000, 61000, 55000, 72000, 48000, 63000, 57000, 68000, 74000, 82000],
        missionsByRegion: { 'Marrakech-Safi': 6, 'Fès-Meknès': 5, 'Rabat-Salé-Kénitra': 5, 'Drâa-Tafilalet': 4, 'Souss-Massa': 3, 'Oriental': 3, 'Casablanca-Settat': 2, 'Tanger-Tétouan-Al Hoceïma': 2 }
      })
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div className="loading-spinner"><div className="spinner-border text-primary" role="status"></div></div>

  const statCards = [
    { label: t('totalMissions'), value: stats?.totalMissions || 0, icon: 'bi-bullseye', color: '#c1272d', bg: '#c1272d15' },
    { label: t('activeMissions'), value: stats?.activeMissions || 0, icon: 'bi-lightning', color: '#006233', bg: '#00623315' },
    { label: t('totalVolunteers'), value: stats?.totalVolunteers || 0, icon: 'bi-people', color: '#17a2b8', bg: '#17a2b815' },
    { label: t('totalBeneficiaries'), value: stats?.totalBeneficiaries || 0, icon: 'bi-heart', color: '#f0c808', bg: '#f0c80815' },
    { label: t('totalDonations'), value: `${((stats?.totalDonationAmount || 0) / 1000).toFixed(0)}K MAD`, icon: 'bi-cash-coin', color: '#28a745', bg: '#28a74515' },
    { label: t('totalEvents'), value: stats?.totalEvents || 0, icon: 'bi-calendar-event', color: '#6f42c1', bg: '#6f42c115' },
    { label: t('totalConvoys'), value: stats?.totalConvoys || 0, icon: 'bi-truck', color: '#fd7e14', bg: '#fd7e1415' },
    { label: 'Missions Terminées', value: stats?.completedMissions || 0, icon: 'bi-check-circle', color: '#20c997', bg: '#20c99715' }
  ]

  const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc']

  const donationChartData = {
    labels: months,
    datasets: [{
      label: 'Donations (MAD)',
      data: stats?.monthlyDonations || [45000, 52000, 38000, 61000, 55000, 72000, 48000, 63000, 57000, 68000, 74000, 82000],
      backgroundColor: 'rgba(193, 39, 45, 0.1)',
      borderColor: '#c1272d',
      borderWidth: 2,
      fill: true,
      tension: 0.4
    }]
  }

  const regionData = stats?.missionsByRegion || { 'Marrakech-Safi': 6, 'Fès-Meknès': 5, 'Rabat-Salé-Kénitra': 5, 'Drâa-Tafilalet': 4, 'Souss-Massa': 3, 'Oriental': 3 }

  const regionChartData = {
    labels: Object.keys(regionData),
    datasets: [{
      data: Object.values(regionData),
      backgroundColor: ['#c1272d', '#006233', '#f0c808', '#17a2b8', '#6f42c1', '#fd7e14', '#20c997', '#e83e8c']
    }]
  }

  const missionStatusData = {
    labels: ['Planifié', 'Actif', 'Terminé', 'Annulé'],
    datasets: [{
      data: [stats?.totalMissions - stats?.activeMissions - stats?.completedMissions || 10, stats?.activeMissions || 12, stats?.completedMissions || 6, 2],
      backgroundColor: ['#f0c808', '#006233', '#17a2b8', '#dc3545']
    }]
  }

  const chartOptions = { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2><i className="bi bi-speedometer2 me-2"></i>{t('dashboard')}</h2>
          <p className="text-muted mb-0">{t('welcome')}, {user?.firstName} !</p>
        </div>
      </div>

      <div className="row g-3 mb-4">
        {statCards.map((card, i) => (
          <div key={i} className="col-6 col-lg-3">
            <div className="card stat-card h-100">
              <div className="card-body d-flex align-items-center gap-3">
                <div className="stat-icon" style={{ backgroundColor: card.bg, color: card.color }}>
                  <i className={`bi ${card.icon}`}></i>
                </div>
                <div>
                  <h4 className="mb-0 fw-bold">{card.value}</h4>
                  <small className="text-muted">{card.label}</small>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="row g-4 mb-4">
        <div className="col-lg-8">
          <div className="card card-custom">
            <div className="card-header bg-white border-0 pt-3">
              <h5 className="fw-bold mb-0"><i className="bi bi-graph-up me-2"></i>{t('monthlyDonations')}</h5>
            </div>
            <div className="card-body" style={{ height: '300px' }}>
              <Line data={donationChartData} options={chartOptions} />
            </div>
          </div>
        </div>
        <div className="col-lg-4">
          <div className="card card-custom">
            <div className="card-header bg-white border-0 pt-3">
              <h5 className="fw-bold mb-0"><i className="bi bi-pie-chart me-2"></i>{t('missionsByStatus')}</h5>
            </div>
            <div className="card-body" style={{ height: '300px' }}>
              <Doughnut data={missionStatusData} options={chartOptions} />
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        <div className="col-lg-6">
          <div className="card card-custom">
            <div className="card-header bg-white border-0 pt-3">
              <h5 className="fw-bold mb-0"><i className="bi bi-geo-alt me-2"></i>{t('regionalDistribution')}</h5>
            </div>
            <div className="card-body" style={{ height: '300px' }}>
              <Bar data={{ labels: Object.keys(regionData), datasets: [{ label: 'Missions', data: Object.values(regionData), backgroundColor: '#c1272d' }] }} options={{ ...chartOptions, indexAxis: 'y' }} />
            </div>
          </div>
        </div>
        <div className="col-lg-6">
          <div className="card card-custom">
            <div className="card-header bg-white border-0 pt-3">
              <h5 className="fw-bold mb-0"><i className="bi bi-pie-chart me-2"></i>Répartition par Région</h5>
            </div>
            <div className="card-body" style={{ height: '300px' }}>
              <Pie data={regionChartData} options={chartOptions} />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
