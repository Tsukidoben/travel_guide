<template>
  <div id="app">
    <router-view/>
    <ai-dialog v-if="showAiDialog"/>
  </div>
</template>

<script>
import AiDialog from "@/components/aiDialog.vue";

export default {
  name: 'App',
  components: {AiDialog},
  data() {
    return{
    }
  },
  computed: {
    showAiDialog() {
      const excludeRoutes = ['login'];
      return !excludeRoutes.includes(this.$route.name);
    }
  },
  mounted() {
    //首先我们获得视口高度并将其乘以1%以获得1vh单位的值
    let vh = window.innerHeight * 0.01
    // 然后，我们将——vh自定义属性中的值设置为文档的根
    document.documentElement.style.setProperty('--vh', `${vh}px`)

    // 我们监听resize事件 视图大小发生变化就重新计算1vh的值
    window.addEventListener('resize', () => {
      // 我们执行与前面相同的脚本
      let vh = window.innerHeight * 0.01
      console.log(vh);
      document.documentElement.style.setProperty('--vh', `${vh}px`)
    })

  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  box-sizing: border-box;
}

.one-line {
  display: block;
  white-space: nowrap !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  text-align: center;
}

.two-line {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.three-line {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
</style>
