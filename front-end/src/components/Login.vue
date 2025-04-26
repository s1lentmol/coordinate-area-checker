<template>
  <div class="auth-container">
    <h2>Вход</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="username">Логин:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div class="form-group">
        <label for="password">Пароль:</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit">Войти</button>
    </form>
    <p class="register-link">
      Нет аккаунта? 
      <router-link to="/register" class="register-btn">Зарегистрируйтесь</router-link>
    </p>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script>
import AuthService from '../services/AuthService';
import { useRouter } from 'vue-router';

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      error: ''
    };
    
  },
  methods: {
    async handleLogin() {
      try {
        const response = await AuthService.login({
          username: this.username,
          password: this.password
        });
        localStorage.setItem('token', response.data.token);
        this.$router.push({ name: 'MainApp' });
      } catch (err) {
        this.error = err.response?.data || 'Ошибка при входе';
      }
    }
  }
};
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
}
input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

.error {
  color: red;
  margin-top: 10px;
}

.register-btn {
  display: inline-block;
  padding: 8px 16px;
  margin-left: 5px;
  background-color: #2ecc71; 
  color: white;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.register-btn:hover {
  background-color: #27ae60; 
  transform: translateY(-2px); 
}

.register-btn.router-link-active {
  background-color: #16a085; 
}
</style>
