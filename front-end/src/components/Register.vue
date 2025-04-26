<template>
  <div class="auth-container">
    <h2>Регистрация</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label for="username">Логин:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div class="form-group">
        <label for="password">Пароль:</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit">Зарегистрироваться</button>
    </form>
    <p>
      Уже есть аккаунт?
      <router-link to="/" class="login-btn">Войдите</router-link></p>
    <p v-if="message" class="message">{{ message }}</p>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script>
import AuthService from '../services/AuthService';
import { useRouter } from 'vue-router';

export default {
  name: 'Register',
  data() {
    return {
      username: '',
      password: '',
      message: '',
      error: ''
    };
  },
  methods: {
    async handleRegister() {
      try {
        const response = await AuthService.register({
          username: this.username,
          password: this.password
        });
        this.message = response.data;
        this.error = '';
      } catch (err) {
        this.error = err.response?.data || 'Ошибка при регистрации';
        this.message = '';
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
.message {
  color: green;
  margin-top: 10px;
}
.error {
  color: red;
  margin-top: 10px;
}

.login-btn {
  display: inline-block;
  padding: 8px 16px;
  margin-left: 5px;
  background-color: #2ecc71;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.login-btn:hover {
  background-color: #27ae60; 
  transform: translateY(-2px);
}

.login-btn.router-link-active {
  background-color: #16a085; 
}
</style>
