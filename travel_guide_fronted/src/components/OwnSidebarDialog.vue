<template>
  <el-dialog
      :width="width"
      class="right-dialog"
      :class="modal ? '' : 'no-modal'"
      height="100%"
      :title="title"
      :visible.sync="dialogVisible"
      :before-close="beforeClose"
      :append-to-body="appendToBody"
      :modal="modal"
      :modal-append-to-body="modalAppendToBody"
      :custom-class="customClass"
      :close-on-click-modal="closeOnClickModal"
      :close-on-press-escape="closeOnPressEscape"
      :show-close="showClose"
      :destroy-on-close="destroyOnClose"
      :center="center"
      :fullscreen="fullscreen"
      :top="top"
      @open="open"
      @opened="opened"
      @close="close"
      @closed="closed"
  >
    <slot slot="title" name="title"></slot>
    <slot></slot>
    <slot slot="footer" name="footer"></slot>
  </el-dialog>
</template>

<script>
export default {
  name: 'YfSidebarDialog',
  props: {
    width: {
      type: String,
      default: () => {
        return '50%'
      },
    },
    title: {
      type: String,
      default: '',
    },
    visible: {
      type: Boolean,
      default: false,
    },
    beforeClose: {
      type: Function,
    },
    appendToBody: {
      type: Boolean,
      default: false,
    },
    customClass: {
      type: String,
    },
    closeOnClickModal: {
      type: Boolean,
      default: false,
    },
    closeOnPressEscape: {
      type: Boolean,
      default: true,
    },
    showClose: {
      type: Boolean,
      default: true,
    },
    destroyOnClose: {
      type: Boolean,
      default: false,
    },
    center: {
      type: Boolean,
      default: false,
    },
    fullscreen: {
      type: Boolean,
      default: false,
    },
    top: {
      type: String,
      default: '0vh',
    },
    open: {
      type: Function,
      default() {
        return () => {}
      },
    },
    opened: {
      type: Function,
      default() {
        return () => {}
      },
    },
    close: {
      type: Function,
      default() {
        return () => {}
      },
    },
    closed: {
      type: Function,
      default() {
        return () => {}
      },
    },
    modal: {
      type: Boolean,
      default: true,
    },
    modalAppendToBody: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {}
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      },
    },
  },
  watch: {},
  created() {},
  methods: {},
}
</script>

<style scoped lang="scss">
$header-height: 50px;
$footer-height: 53px;
$body-height: calc(100% - $header-height - $footer-height);
div{
  box-sizing: border-box;
}

.right-dialog{
  height: 100vh;
  overflow: hidden  !important;

  &.no-modal{
    margin-top: $icon-height;
    height: calc(100vh - $icon-height);
  }
}

::v-deep .v-modal{
  margin-top: $icon-height;
  height: calc(100vh - $icon-height);
}

.right-dialog ::v-deep .el-dialog {
  height: 100% !important;
  border-radius: 0 !important;
  right: 0 !important;
  float: right !important;
  overflow: hidden !important;
}

.right-dialog ::v-deep .el-dialog__header {
  text-align: left;
  box-sizing: border-box;
  height: $header-height;
  padding: 10px;
  border-bottom: 1px solid darkgrey;
}

.right-dialog.no-modal ::v-deep .el-dialog__header {
  border-top: 1px solid darkgrey;
}

.right-dialog ::v-deep .el-dialog__body {
  box-sizing: border-box;
  height: $body-height;
  overflow-y: auto;
}

.right-dialog ::v-deep .el-dialog__footer {
  box-sizing: border-box;
  min-height: $footer-height;
  position: absolute;
  left: 0;
  right: 0;
  justify-content: left;
  text-align: left !important;
  border-top: 1px solid darkgrey !important;
}
</style>
