<template>
  <div class="own-nav-tabs-box">
    <el-breadcrumb separator="/" style="display: inline-block;margin-left: 10px">
<!--      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>-->
      <el-breadcrumb-item
          :to="{ path: item.path }"
          v-for="item in breadcrumbList"
          :key="item.path"
      >
        <span v-if="item.meta.group">
          <span>{{ item.meta.group }}</span>
          <span style="margin: 0 9px;font-weight: 700;color: #C0C4CC;">/</span>
        </span>
        <span>
          {{ item.meta.title }}
        </span>
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script>
export default {
  name: "OwnTabsBar",
  data(){
    return {
      // breadcrumbList: []
    }
  },
  mounted() {
    // this.breadcrumbList = this.$route.matched
  },
  computed: {
    breadcrumbList(){
      return this.$route.matched
    }
  }
}
</script>

<style scoped lang="scss">
  .own-nav-tabs-box ::v-deep{
    span{
      color: $nav-bar-color;
    }
  }
</style>
