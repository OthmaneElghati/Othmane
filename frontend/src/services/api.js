import axios from 'axios'

const API_URL = '/api'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (data) => api.post('/auth/register', data),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (data) => api.post('/auth/reset-password', data)
}

export const dashboardService = {
  getStats: () => api.get('/dashboard/stats')
}

export const missionService = {
  getAll: (params) => api.get('/missions', { params }),
  getById: (id) => api.get(`/missions/${id}`),
  create: (data) => api.post('/missions', data),
  update: (id, data) => api.put(`/missions/${id}`, data),
  delete: (id) => api.delete(`/missions/${id}`),
  search: (keyword) => api.get(`/missions/search?keyword=${keyword}`),
  getByRegion: (region) => api.get(`/missions/region/${region}`),
  getByStatus: (status) => api.get(`/missions/status/${status}`)
}

export const volunteerService = {
  getAll: (params) => api.get('/volunteers', { params }),
  getById: (id) => api.get(`/volunteers/${id}`),
  create: (data) => api.post('/volunteers', data),
  update: (id, data) => api.put(`/volunteers/${id}`, data),
  delete: (id) => api.delete(`/volunteers/${id}`),
  getAvailable: () => api.get('/volunteers/available'),
  recommend: (missionId) => api.get(`/volunteers/recommend/${missionId}`)
}

export const beneficiaryService = {
  getAll: (params) => api.get('/beneficiaries', { params }),
  getById: (id) => api.get(`/beneficiaries/${id}`),
  create: (data) => api.post('/beneficiaries', data),
  update: (id, data) => api.put(`/beneficiaries/${id}`, data),
  delete: (id) => api.delete(`/beneficiaries/${id}`),
  getByMission: (missionId) => api.get(`/beneficiaries/mission/${missionId}`)
}

export const donationService = {
  getAll: (params) => api.get('/donations', { params }),
  getById: (id) => api.get(`/donations/${id}`),
  create: (data) => api.post('/donations', data),
  update: (id, data) => api.put(`/donations/${id}`, data),
  delete: (id) => api.delete(`/donations/${id}`),
  getTotal: () => api.get('/donations/total')
}

export const convoyService = {
  getAll: (params) => api.get('/convoys', { params }),
  getById: (id) => api.get(`/convoys/${id}`),
  create: (data) => api.post('/convoys', data),
  update: (id, data) => api.put(`/convoys/${id}`, data),
  delete: (id) => api.delete(`/convoys/${id}`),
  getActive: () => api.get('/convoys/active')
}

export const eventService = {
  getAll: (params) => api.get('/events', { params }),
  getById: (id) => api.get(`/events/${id}`),
  create: (data) => api.post('/events', data),
  update: (id, data) => api.put(`/events/${id}`, data),
  delete: (id) => api.delete(`/events/${id}`)
}

export const notificationService = {
  getAll: () => api.get('/notifications'),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markAsRead: (id) => api.put(`/notifications/${id}/read`),
  markAllAsRead: () => api.put('/notifications/read-all'),
  delete: (id) => api.delete(`/notifications/${id}`)
}

export const reportService = {
  generate: (type) => api.get(`/reports/generate/${type}`, { responseType: 'blob' })
}

export default api
