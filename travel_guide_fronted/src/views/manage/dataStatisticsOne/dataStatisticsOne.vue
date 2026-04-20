<template>
  <div class="statistics-container">
    <div class="board-grid">
      <div class="data-card" v-for="(item, index) in boardConfig" :key="index">
        <div class="card-content">
          <p class="label">{{ item.name }}</p>
          <h2 class="value">
            <span v-if="item.isMoney">¥</span>
            {{ boardData[item.key] }}
            <small v-if="!item.isMoney">单</small>
          </h2>
        </div>
        <div class="card-icon" :style="{ backgroundColor: item.bgColor, color: item.color }">
          <i :class="item.icon"></i>
        </div>
      </div>
    </div>

    <div class="chart-grid">
      <div class="chart-item">
        <div class="chart-title">
          <span class="title-text">景点分类订单占比</span>
        </div>
        <div class="chart-box" id="echartLeft"></div>
      </div>

      <div class="chart-item">
        <div class="chart-title">
          <span class="title-text">最近7日销售额趋势</span>
        </div>
        <div class="chart-box" id="echartRight"></div>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";

export default {
  name: "DataStatistics",
  mixins: [common],
  data() {
    return {
      boardData: {
        "totalOrder": 0,
        "todayOrder": 0,
        "totalPrice": 0,
        "todayPrice": 0
      },
      boardConfig: [
        { name: '总订单量', key: 'totalOrder', icon: 'el-icon-s-order', color: '#409EFF', bgColor: 'rgba(64,158,255,0.1)' },
        { name: '今日订单量', key: 'todayOrder', icon: 'el-icon-document-checked', color: '#67C23A', bgColor: 'rgba(103,194,58,0.1)' },
        { name: '总流水', key: 'totalPrice', icon: 'el-icon-money', color: '#E6A23C', bgColor: 'rgba(230,162,60,0.1)', isMoney: true },
        { name: '今日流水', key: 'todayPrice', icon: 'el-icon-wallet', color: '#F56C6C', bgColor: 'rgba(245,108,108,0.1)', isMoney: true },
      ],
      charts: { left: null, right: null }
    }
  },
  mounted() {
    this.initAllData();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    if (this.charts.left) this.charts.left.dispose();
    if (this.charts.right) this.charts.right.dispose();
  },
  methods: {
    handleResize() {
      this.charts.left && this.charts.left.resize();
      this.charts.right && this.charts.right.resize();
    },
    async initAllData() {
      try {
        const res = await request({ url: config.backHost + '/echart/adminBoardData' });
        if (res.code == 200) {
          res.data.forEach(item => {
            console.log(item.count,this.boardData[item.type])
            this.boardData[item.type] = item.count;
          });
        }
      } catch (e) { console.error(e); }
      this.renderAttractionPie();
      this.renderTrendLine();
    },
    async renderAttractionPie() {
      const chartDom = document.getElementById('echartLeft');
      this.charts.left = this.$echarts.init(chartDom);
      try {
        const res = await request({ url: config.backHost + '/echart/typeAttractionOrder' });
        if (res.code == 200) {
          const formattedData = res.data.map(item => ({ name: item.typeName, value: item.orderCount }));
          this.charts.left.setOption({
            tooltip: { trigger: 'item', formatter: '{b}: {c}单 ({d}%)' },
            legend: { top: '5%', left: '5%', right: '8%', bottom: '10%', left: 'center', icon: 'circle' },
            series: [{
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
              label: { show: false },
              emphasis: { label: { show: true, fontSize: '18', fontWeight: 'bold' } },
              data: formattedData
            }],
            color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
          });
        }
      } catch (e) { console.error(e); }
    },
    async renderTrendLine() {
      const chartDom = document.getElementById('echartRight');
      this.charts.right = this.$echarts.init(chartDom);
      try {
        const res = await request({ url: config.backHost + '/echart/adminRecent7Days' });
        if (res.code == 200) {
          this.charts.right.setOption({
            tooltip: { trigger: 'axis' },
            grid: { top: '5%', left: '5%', right: '8%', bottom: '10%', containLabel: true },
            xAxis: {
              type: 'category',
              boundaryGap: false,
              data: res.data.map(item => item.day),
              axisLine: { lineStyle: { color: '#EBEEF5' } }
            },
            yAxis: {
              type: 'value',
              splitLine: { lineStyle: { type: 'dashed' } }
            },
            series: [{
              name: '销售额',
              type: 'line',
              smooth: true,
              data: res.data.map(item => item.total_amount),
              areaStyle: {
                color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(64,158,255,0.3)' },
                  { offset: 1, color: 'rgba(64,158,255,0)' }
                ])
              },
              itemStyle: { color: '#409EFF' }
            }]
          });
        }
      } catch (e) { console.error(e); }
    }
  }
}
</script>

<style scoped lang="scss">
.statistics-container {
  background-color: #f8f9fa;
  height: 100%;
  box-sizing: border-box;

  .board-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;
  }

  .data-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.04);

    .label { font-size: 14px; color: #909399; margin: 0 0 8px 0; }
    .value {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
      margin: 0;
      span { font-size: 16px; margin-right: 2px; }
      small { font-size: 12px; color: #999; margin-left: 4px; font-weight: normal; }
    }
    .card-icon {
      width: 48px;
      height: 48px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
    }
  }

  .chart-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    height: calc(100% - 120px);
    .chart-item {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.04);

      .chart-title {
        margin-bottom: 20px;
        .title-text {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          border-left: 4px solid #409EFF;
          padding-left: 12px;
        }
      }
      .chart-box {
        width: 100%;
        height: calc(100% - 44px);
      }
    }
  }
}

@media (max-width: 1200px) {
  .board-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .board-grid { grid-template-columns: 1fr; }
  .chart-grid { grid-template-columns: 1fr; }
}
</style>
