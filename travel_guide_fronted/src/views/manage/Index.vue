<template>
  <div class="manage-index-box">
    <div class="base-left-menu" :class="{'is-collapse': isCollapse}">
      <own-side-bar :is-collapse.sync="isCollapse"/>
    </div>
    <div class="base-right-content" :class="{'is-collapse': isCollapse}">
      <div class="base-nav-bar">
        <own-nav-bar/>
      </div>

      <div class="base-app-main">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script>
import OwnSideBar from "@/views/manage/components/OwnSideBar.vue";
import OwnNavBar from "@/views/manage/components/OwnNavBar.vue";
export default {
  name: "Index",
  components: {OwnNavBar, OwnSideBar},
  data(){
    return{
      isCollapse: false,
    }
  }
}
</script>

<style scoped lang="scss">
div {
  box-sizing: border-box !important;
  transition: $base-transition;
}


.manage-index-box {
  width: 100vw;
  height: 100vh;
  overflow: auto;
  display: flex;
  flex-direction: column;
  background: $global-background;

  .base-left-menu {
    position: fixed;
    top: 0;
    left: 0;
    width: $base-left-menu-width;
    height: 100vh;
    transition: $base-transition;

    &.is-collapse{
      width: $base-left-menu-width-min;
    }
  }

  .base-right-content {
    margin-left: $base-left-menu-width;
    width: $base-right-content-width;
    height: 100vh;
    display: flex;
    flex-direction: column;

    &.is-collapse{
      width: $base-right-content-width-min;
      margin-left: $base-left-menu-width-min;
    }

    .base-nav-bar {
      width: 100%;
      height: $base-nav-bar-height;
    }

    .base-app-main{
      width: 100%;
      height: $base-app-main-height;
      padding: $sys-10-padding;
    }
  }
}


</style>
