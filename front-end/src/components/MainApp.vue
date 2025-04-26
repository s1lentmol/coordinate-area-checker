<template>
  <div class="main-container">
    <div class="form-section">
      <form @submit.prevent="submitPoint">
        <div class="form-group">
          <label>Координата X:</label>
          <div class="radio-group">
            <label v-for="value in xOptions" :key="value" class="custom-radio">
              <input
                type="radio"
                :value="value"
                v-model.number="form.x"
                required
              />
              <span class="radio-label">{{ value }}</span>
            </label>
          </div>
        </div>
        <div class="form-group">
          <label for="y">Координата Y:</label>
          <input
            type="number"
            id="y"
            v-model.number="form.y"
            step="0.01"
            min="-5"
            max="3"
            required
          />
        </div>
        <div class="form-group">
          <label>Радиус (R):</label>
          <div class="radio-group">
            <label v-for="value in rOptions" :key="value" class="custom-radio">
              <input
                type="radio"
                :value="value"
                v-model.number="form.radius"
                required
              />
              <span class="radio-label">{{ value }}</span>
            </label>
          </div>
        </div>
        <button type="submit">Проверить</button>
      </form>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
    <div class="graph-section">
      <GraphCanvas
        ref="graphCanvas"
        :r="form.radius"
        @pointClicked="handleCanvasClick"
        :points="points"
      />
    </div>
    <div class="results-section">
      <h3>Результаты</h3>
      <div :class="['table-wrapper', { 'scrollable': results.length > 15 }]">
        <table id="response-table">
          <thead>
            <tr>
              <th>#</th>
              <th>X</th>
              <th>Y</th>
              <th>R</th>
              <th>Попадание</th>
              <th>Время</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(result, index) in results" :key="index">
              <td>{{ index + 1 }}</td>
              <td>{{ result.x }}</td>
              <td>{{ result.y }}</td>
              <td>{{ result.radius }}</td>
              <td>{{ result.isHit ? 'Да' : 'Нет' }}</td>
              <td>{{ formatDate(result.timestamp) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import ApiService from '../services/ApiService';
import GraphCanvas from './GraphCanvas.vue';
import AuthService from '../services/AuthService';

export default {
  name: 'MainApp',
  components: {
    GraphCanvas,
  },
  data() {
    return {
      form: {
        x: null,
        y: null,
        radius: 1,
      },
      xOptions: [-4, -3, -2, -1, 0, 1, 2, 3, 4],
      rOptions: [1, 2, 3, 4], 
      results: [],
      error: '',
      points: [],
      isAuthenticated: true, 
    };
  },
  mounted() {
    this.fetchResults();
    window.addEventListener('resize', this.handleResize);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    logout() {
      AuthService.logout();
      this.isAuthenticated = false;
      this.$router.push({ name: 'Login' });
    },
    async submitPoint() {
      this.error = '';
      if (this.form.radius <= 0) {
        this.error = 'Радиус должен быть больше 0';
        return;
      }
      if (this.form.x === null || this.form.y === null) {
        this.error = 'Пожалуйста, выберите координаты X и Y';
        return;
      }
      try {
        const response = await ApiService.checkPoint({
          x: this.form.x,
          y: this.form.y,
          radius: this.form.radius,
        });
        this.points.push({
          x: this.form.x,
          y: this.form.y,
          isHit: response.data.hit,
        });
        this.results.push({
          x: this.form.x,
          y: this.form.y,
          radius: this.form.radius,
          isHit: response.data.hit,
          timestamp: new Date(), 
        });
      } catch (err) {
        this.error = err.response?.data || 'Ошибка при проверке точки';
      }
    },
    async fetchResults() {
      try {
        const response = await ApiService.getResults();
        this.results = response.data.map(item => ({
          x: item.x,
          y: item.y,
          radius: item.radius,
          isHit: item.hit,
          timestamp: new Date(item.timestamp),
        }));
        this.points = this.results.map(result => ({
          x: result.x,
          y: result.y,
          isHit: result.isHit,
        }));
      } catch (err) {
        this.error = err.response?.data || 'Ошибка при получении результатов';
      }
    },
    formatDate(date) {
      const d = new Date(date);
      return d.toLocaleString();
    },
    handleCanvasClick(point) {
      ApiService.checkPoint({
        x: point.x,
        y: point.y,
        radius: point.r,
      })
        .then(response => {
          this.points.push({
            x: point.x,
            y: point.y,
            isHit: response.data.hit,
          });
          this.results.push({
            x: point.x,
            y: point.y,
            radius: point.r,
            isHit: response.data.hit,
            timestamp: new Date(), 
          });
        })
        .catch(error => {
          console.error('Ошибка:', error);
          alert('Ошибка при обработке клика по Canvas: ' + error.message);
        });
    },
    handleResize() {
      if (this.$refs.graphCanvas && typeof this.$refs.graphCanvas.drawCanvas === 'function') {
        this.$refs.graphCanvas.drawCanvas();
      }
    },
  },
};
</script>

<style scoped>
.main-container {
  display: flex;
  flex-wrap: wrap;
  padding: 20px;
  gap: 20px;
}
.form-section,
.graph-section,
.results-section {
  flex: 1 1 300px;
}
.form-group {
  margin-bottom: 15px;
}
.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.custom-radio {
  position: relative;
  display: inline-block;
  cursor: pointer;
  user-select: none;
}
.custom-radio:hover .radio-label {
  background-color: #ecf0f1;
}

.custom-radio input:checked + .radio-label {
  background-color: #3498db;
  color: #ffffff;
  border-color: #2980b9;
}
.radio-label {
  display: block;
  padding: 10px 20px;
  border: 2px solid #3498db;
  border-radius: 25px;
  background-color: #ffffff;
  color: #3498db;
  transition: all 0.3s ease;
  font-weight: bold;
}
.custom-radio input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

label {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}
input[type='number'] {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
.error {
  color: red;
  margin-top: 10px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: center;
}
.table-wrapper {
  width: 100%;
}
.scrollable {
  max-height: 600px;
  overflow-y: auto;
}
.scrollable::-webkit-scrollbar {
  width: 8px;
}

.scrollable::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.scrollable::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.4);
}

@media (max-width: 1027px) and (min-width: 715px) {
  .main-container {
    flex-direction: row;
  }
}
@media (max-width: 714px) {
  .main-container {
    flex-direction: column;
  }
}
</style>
