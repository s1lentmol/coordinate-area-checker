import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

class AuthService {
  register(user) {
    return axios.post(`${API_URL}/register`, user);
  }

  login(user) {
    return axios.post(`${API_URL}/login`, user);
  }

  logout() {
    localStorage.removeItem('token');
  }
}

export default new AuthService();
