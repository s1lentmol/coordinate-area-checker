<template>
  <header class="header">
    <div class="left-section">
      <img class="itmo-logo-light" src="../assets/itmo-logo-light.svg" alt="ITMO Logo">
      <img class="cs-logo" src="../assets/cs-logo.png" alt="CS Logo">
    </div>

    <div class="middle-section">
      Лабораторная работа №4
    </div>

    <div class="right-section">
      <p>Молчанов Артём</p>
      <p>Группа: Р3219</p>
      <p>Вариант: 83761</p>
    </div>
  </header>
  <nav v-if="isAuthenticated">
    <button @click="logout">Выйти</button>
  </nav>
</template>

<script>
import AuthService from '../services/AuthService';
import { watch, onMounted, onBeforeUnmount } from 'vue';
import { useRoute } from 'vue-router';

export default {
  name: 'Header',
  data() {
    return {
      isAuthenticated: !!localStorage.getItem('token')
    };
  },
  methods: {
    logout() {
      AuthService.logout();
      this.isAuthenticated = false;
      this.$router.push({ name: 'Login' });
    },
    checkAuth() {
      this.isAuthenticated = !!localStorage.getItem('token');
    }
  },
  setup(props, { emit }) {
    const route = useRoute();

    return { route };
  },
  watch: {
    '$route.name': function(newName, oldName) {
      this.checkAuth();
    }
  },
  mounted() {
    window.addEventListener('storage', this.checkAuth);
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.checkAuth);
  }
};
</script>

<style scoped>
.header {
  height: 100px;
  background-color: rgb(32,32,32);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.left-section, .middle-section {
  display: flex;
  align-items: center;
}

.middle-section {
  font-size: 30px;
}

.left-section img {
  margin-left: 20px;
  height: 90px;
}

.right-section {
  display: flex;
  flex-direction: column;
  margin-right: 35px;
  flex-shrink: 0;
  line-height: 0.2;
}

nav {
  margin-left: 20px;
  margin-top: 10px;
}
</style>
