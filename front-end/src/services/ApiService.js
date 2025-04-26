import axios from 'axios';

const API_URL = 'http://localhost:8080/api/main';

class ApiService {
  constructor() {
    this.api = axios.create({
      baseURL: API_URL,
    });

    this.api.interceptors.request.use(config => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });
  }

  checkPoint(data) {
    return this.api.post('/check-point', data);
  }

  getResults() {
    return this.api.get('/results');
  }
}

export default new ApiService();
