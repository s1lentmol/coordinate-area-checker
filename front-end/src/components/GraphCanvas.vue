<template>
  <canvas
    ref="canvas"
    :width="width"
    :height="height"
    class="graph-canvas"
  ></canvas>
</template>

<script>
export default {
  name: 'GraphCanvas',
  props: {
    r: {
      type: Number,
      required: true,
      default: 1,
    },
    points: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      width: 500,
      height: 500,
      originX: 250,
      originY: 250,
      scale: 40,
      ctx: null,
    };
  },
  watch: {
    r(newR) {
      this.drawCanvas();
    },
    points: {
      handler() {
        this.drawPoints();
      },
      deep: true,
    },
  },
  mounted() {
    this.ctx = this.$refs.canvas.getContext('2d');
    this.drawCanvas();
    this.$refs.canvas.addEventListener('click', this.handleCanvasClick);
    window.addEventListener('resize', this.handleResize);
  },
  beforeUnmount() {
    this.$refs.canvas.removeEventListener('click', this.handleCanvasClick);
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    drawCanvas() {
      this.clearCanvas();
      this.drawCoordinatePlane();
      this.drawShapes();
      this.drawPoints();
    },
    clearCanvas() {
      this.ctx.clearRect(0, 0, this.width, this.height);
    },
    drawCoordinatePlane() {
      this.drawGrid();
      this.drawAxes();
      this.drawTicks();
    },
    drawGrid() {
      this.ctx.beginPath();
      this.ctx.strokeStyle = '#e0e0e0';
      this.ctx.lineWidth = 1;

      for (let x = this.originX; x <= this.width; x += this.scale) {
        this.ctx.moveTo(x, 0);
        this.ctx.lineTo(x, this.height);
      }
      for (let x = this.originX; x >= 0; x -= this.scale) {
        this.ctx.moveTo(x, 0);
        this.ctx.lineTo(x, this.height);
      }

      for (let y = this.originY; y <= this.height; y += this.scale) {
        this.ctx.moveTo(0, y);
        this.ctx.lineTo(this.width, y);
      }
      for (let y = this.originY; y >= 0; y -= this.scale) {
        this.ctx.moveTo(0, y);
        this.ctx.lineTo(this.width, y);
      }

      this.ctx.stroke();
    },
    drawAxes() {
      this.ctx.beginPath();
      this.ctx.strokeStyle = '#000000';
      this.ctx.lineWidth = 2;


      this.ctx.moveTo(0, this.originY);
      this.ctx.lineTo(this.width, this.originY);


      this.ctx.moveTo(this.originX, 0);
      this.ctx.lineTo(this.originX, this.height);

      this.ctx.stroke();
    },
    drawTicks() {
      this.ctx.fillStyle = '#000000';
      this.ctx.font = '8px Arial';
      this.ctx.textAlign = 'center';
      this.ctx.textBaseline = 'top';

      for (let x = this.originX + this.scale, i = 1; x < this.width; x += this.scale, i++) {
        this.ctx.beginPath();
        this.ctx.moveTo(x, this.originY - 3);
        this.ctx.lineTo(x, this.originY + 3);
        this.ctx.stroke();
        this.ctx.fillText(i, x, this.originY + 8);
      }
      for (let x = this.originX - this.scale, i = -1; x > 0; x -= this.scale, i--) {
        this.ctx.beginPath();
        this.ctx.moveTo(x, this.originY - 3);
        this.ctx.lineTo(x, this.originY + 3);
        this.ctx.stroke();
        this.ctx.fillText(i, x, this.originY + 8);
      }

      this.ctx.textAlign = 'right';
      this.ctx.textBaseline = 'middle';
      for (let y = this.originY - this.scale, i = 1; y > 0; y -= this.scale, i++) {
        this.ctx.beginPath();
        this.ctx.moveTo(this.originX - 3, y);
        this.ctx.lineTo(this.originX + 3, y);
        this.ctx.stroke();
        this.ctx.fillText(i, this.originX - 8, y);
      }
      for (let y = this.originY + this.scale, i = -1; y < this.height; y += this.scale, i--) {
        this.ctx.beginPath();
        this.ctx.moveTo(this.originX - 3, y);
        this.ctx.lineTo(this.originX + 3, y);
        this.ctx.stroke();
        this.ctx.fillText(i, this.originX - 8, y);
      }

      const arrowSize = 5;

      this.ctx.beginPath();
      this.ctx.moveTo(this.width - arrowSize, this.originY - arrowSize / 2);
      this.ctx.lineTo(this.width, this.originY);
      this.ctx.lineTo(this.width - arrowSize, this.originY + arrowSize / 2);
      this.ctx.stroke();
      this.ctx.beginPath();
      this.ctx.moveTo(this.originX - arrowSize / 2, arrowSize);
      this.ctx.lineTo(this.originX, 0);
      this.ctx.lineTo(this.originX + arrowSize / 2, arrowSize);
      this.ctx.stroke();
    },
    toCanvasCoords(x, y) {
      return {
        x: this.originX + x * this.scale,
        y: this.originY - y * this.scale,
      };
    },
    toLogicalCoords(canvasX, canvasY) {
      return {
        x: (canvasX - this.originX) / this.scale,
        y: (this.originY - canvasY) / this.scale,
      };
    },
    drawShapes() {
      const R = this.r;
      if (R <= 0) return;

      this.drawQuarterCircle(R);

      this.drawRectangle(R);

      this.drawTriangle(R);
    },
    drawQuarterCircle(R) {
      const center = this.toCanvasCoords(0, 0);
      const radius = R * this.scale;

      this.ctx.beginPath();
      this.ctx.fillStyle = 'rgba(0, 0, 255, 0.3)'; 
      this.ctx.strokeStyle = '#0000FF'; 
      this.ctx.lineWidth = 2;

      this.ctx.moveTo(center.x, center.y); 
      this.ctx.arc(center.x, center.y, radius, -0.5 * Math.PI, 0, false);
      this.ctx.closePath(); 

      this.ctx.fill(); 
      this.ctx.stroke(); 
    },
    drawRectangle(R) {
      const topLeft = this.toCanvasCoords(-R, 0);
      const topRight = this.toCanvasCoords(0, 0);
      const bottomLeft = this.toCanvasCoords(-R, -R / 2);
      const bottomRight = this.toCanvasCoords(0, -R / 2);

      this.ctx.beginPath();
      this.ctx.fillStyle = 'rgba(0, 0, 255, 0.3)';
      this.ctx.strokeStyle = '#0000FF';
      this.ctx.lineWidth = 2;

      this.ctx.moveTo(topLeft.x, topLeft.y);
      this.ctx.lineTo(topRight.x, topRight.y);
      this.ctx.lineTo(bottomRight.x, bottomRight.y);
      this.ctx.lineTo(bottomLeft.x, bottomLeft.y);
      this.ctx.closePath();

      this.ctx.fill();
      this.ctx.stroke();
    },
    drawTriangle(R) {
      const vertex1 = this.toCanvasCoords(-R, 0);
      const vertex2 = this.toCanvasCoords(0, 0);
      const vertex3 = this.toCanvasCoords(0, R / 2);

      this.ctx.beginPath();
      this.ctx.fillStyle = 'rgba(0, 0, 255, 0.3)';
      this.ctx.strokeStyle = '#0000FF';
      this.ctx.lineWidth = 2;

      this.ctx.moveTo(vertex1.x, vertex1.y);
      this.ctx.lineTo(vertex2.x, vertex2.y);
      this.ctx.lineTo(vertex3.x, vertex3.y);
      this.ctx.closePath();

      this.ctx.fill();
      this.ctx.stroke();
    },
    drawPoint(x, y, color = '#FF0000') {
      const coords = this.toCanvasCoords(x, y);
      this.ctx.beginPath();
      this.ctx.arc(coords.x, coords.y, 5, 0, 2 * Math.PI);
      this.ctx.fillStyle = color;
      this.ctx.fill();
      this.ctx.strokeStyle = '#000000';
      this.ctx.stroke();
    },
    handleCanvasClick(event) {
      const rect = this.$refs.canvas.getBoundingClientRect();
      const canvasX = event.clientX - rect.left;
      const canvasY = event.clientY - rect.top;

      const logicalCoords = this.toLogicalCoords(canvasX, canvasY);
      const x = parseFloat(logicalCoords.x.toFixed(2));
      const y = parseFloat(logicalCoords.y.toFixed(2));
      const r = this.r;

      if (r <= 0) {
        alert('Радиус должен быть больше 0');
        return;
      }
      this.$emit('pointClicked', { x, y, r });
    },
    drawPoints() {
      this.points.forEach(point => {
        this.drawPoint(point.x, point.y, point.isHit ? '#00FF00' : '#FF0000');
      });
    },
    handleResize() {
      this.drawCanvas();
    },
  },
};
</script>

<style scoped>
.graph-canvas {
  background-color: rgb(159, 159, 159);
  border: 1px solid #000;
  width: 500px; 
  height: 500px; 
  display: block;
}
</style>
