import { useState, useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet'
import { useLanguage } from '../context/LanguageContext'
import { missionService, convoyService } from '../services/api'
import L from 'leaflet'

const missionIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
  iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
})

const convoyIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
  iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
})

function MapPage() {
  const { t } = useLanguage()
  const [missions, setMissions] = useState([])
  const [convoys, setConvoys] = useState([])
  const [showMissions, setShowMissions] = useState(true)
  const [showConvoys, setShowConvoys] = useState(true)
  const [filterRegion, setFilterRegion] = useState('')
  const [filterStatus, setFilterStatus] = useState('')

  const regions = ['Rabat-Salé-Kénitra', 'Casablanca-Settat', 'Marrakech-Safi', 'Fès-Meknès', 'Tanger-Tétouan-Al Hoceïma', 'Souss-Massa', 'Oriental', 'Béni Mellal-Khénifra', 'Drâa-Tafilalet', 'Laâyoune-Sakia El Hamra', 'Dakhla-Oued Ed-Dahab']

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [mRes, cRes] = await Promise.all([missionService.getAll(), convoyService.getAll()])
      setMissions(Array.isArray(mRes.data) ? mRes.data : mRes.data.content || [])
      setConvoys(Array.isArray(cRes.data) ? cRes.data : cRes.data.content || [])
    } catch (err) {
      setMissions([])
      setConvoys([])
    }
  }

  const filteredMissions = missions.filter(m => {
    if (!m.latitude || !m.longitude) return false
    if (filterRegion && m.region !== filterRegion) return false
    if (filterStatus && m.status !== filterStatus) return false
    return true
  })

  const filteredConvoys = convoys.filter(c => c.currentLatitude && c.currentLongitude)

  const statusColors = { PLANNED: '#ffc107', ACTIVE: '#28a745', COMPLETED: '#17a2b8', CANCELLED: '#dc3545' }

  return (
    <div>
      <div className="page-header d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
        <h2><i className="bi bi-geo-alt me-2"></i>Carte Interactive</h2>
      </div>

      <div className="card card-custom mb-3">
        <div className="card-body py-2">
          <div className="row g-2 align-items-center">
            <div className="col-auto">
              <div className="form-check form-check-inline">
                <input className="form-check-input" type="checkbox" checked={showMissions} onChange={(e) => setShowMissions(e.target.checked)} id="showMissions" />
                <label className="form-check-label" htmlFor="showMissions"><i className="bi bi-bullseye text-danger me-1"></i>Missions</label>
              </div>
              <div className="form-check form-check-inline">
                <input className="form-check-input" type="checkbox" checked={showConvoys} onChange={(e) => setShowConvoys(e.target.checked)} id="showConvoys" />
                <label className="form-check-label" htmlFor="showConvoys"><i className="bi bi-truck text-primary me-1"></i>Convois</label>
              </div>
            </div>
            <div className="col-auto">
              <select className="form-select form-select-sm" value={filterRegion} onChange={(e) => setFilterRegion(e.target.value)}>
                <option value="">{t('allRegions')}</option>
                {regions.map(r => <option key={r} value={r}>{r}</option>)}
              </select>
            </div>
            <div className="col-auto">
              <select className="form-select form-select-sm" value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
                <option value="">{t('allStatuses')}</option>
                <option value="PLANNED">{t('planned')}</option>
                <option value="ACTIVE">{t('active')}</option>
                <option value="COMPLETED">{t('completed')}</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <div className="map-container">
        <MapContainer center={[31.7917, -7.0926]} zoom={6} style={{ height: '100%', width: '100%' }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" attribution='&copy; OpenStreetMap contributors' />
          
          {showMissions && filteredMissions.map(mission => (
            <Marker key={`m-${mission.id}`} position={[mission.latitude, mission.longitude]} icon={missionIcon}>
              <Popup>
                <div>
                  <strong>{mission.title}</strong><br />
                  <small className="text-muted">{mission.city}, {mission.region}</small><br />
                  <span className="badge" style={{ backgroundColor: statusColors[mission.status], color: 'white', fontSize: '10px' }}>{mission.status}</span>
                  {mission.budget && <><br /><small>Budget: {mission.budget.toLocaleString()} MAD</small></>}
                </div>
              </Popup>
            </Marker>
          ))}

          {showConvoys && filteredConvoys.map(convoy => (
            <Marker key={`c-${convoy.id}`} position={[convoy.currentLatitude, convoy.currentLongitude]} icon={convoyIcon}>
              <Popup>
                <div>
                  <strong>{convoy.name}</strong><br />
                  <small>{convoy.departureCity} → {convoy.destinationCity}</small><br />
                  <span className="badge bg-primary" style={{ fontSize: '10px' }}>{convoy.status}</span>
                  {convoy.cargo && <><br /><small>Cargo: {convoy.cargo}</small></>}
                </div>
              </Popup>
            </Marker>
          ))}

          {showConvoys && filteredConvoys.map(convoy => {
            if (convoy.departureLatitude && convoy.destinationLatitude) {
              return (
                <Polyline
                  key={`route-${convoy.id}`}
                  positions={[[convoy.departureLatitude, convoy.departureLongitude], [convoy.currentLatitude, convoy.currentLongitude], [convoy.destinationLatitude, convoy.destinationLongitude]]}
                  color="#0d6efd"
                  weight={2}
                  dashArray="5,10"
                  opacity={0.7}
                />
              )
            }
            return null
          })}
        </MapContainer>
      </div>

      <div className="row g-3 mt-3">
        <div className="col-md-6">
          <div className="card card-custom">
            <div className="card-body">
              <h6 className="fw-bold"><i className="bi bi-bullseye text-danger me-2"></i>Missions sur la carte: {filteredMissions.length}</h6>
            </div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="card card-custom">
            <div className="card-body">
              <h6 className="fw-bold"><i className="bi bi-truck text-primary me-2"></i>Convois actifs: {filteredConvoys.length}</h6>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default MapPage
