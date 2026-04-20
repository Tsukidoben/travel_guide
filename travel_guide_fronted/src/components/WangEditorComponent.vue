<template>
  <div>
    <div ref="toolbar"></div>
    <div
        ref="editor"
        :style="{
        border: '1px solid #ccc',
        height: height + 'px',
        overflowY: 'auto'
      }"
    ></div>
  </div>
</template>

<script>
import E from 'wangeditor'
import axios from 'axios'
import config from "@/config/config";

export default {
  name: 'WangEditorComponent',
  props: {
    value: {
      type: String,
      default: '',
    },
    height: {
      type: Number,
      default: 300,
    },
    disabled: { type: Boolean, default: false }
  },
  data() {
    return {
      editor: null,
      uploadImgServer: config.uploadUrl
    }
  },
  mounted() {
    this.editor = new E(this.$refs.toolbar, this.$refs.editor)

    this.editor.config.onchange = (html) => {
      this.$emit('input', html)
    }

    // V4 配置方式如下
    this.editor.config.uploadImgServer = this.uploadImgServer
    this.editor.config.uploadFileName = 'file'
    this.editor.config.uploadImgHeaders = {
      Authorization: localStorage.getItem('token'),
    }
    this.editor.config.uploadImgMaxSize = 5 * 1024 * 1024
    this.editor.config.uploadImgMaxLength = 10
    this.editor.config.uploadImgAccept = []
    this.editor.config.withCredentials = true

    this.editor.config.uploadImgHooks = {
      customInsert: (insertImgFn, result, editor) => {
        if (
            result &&
            result.code === 200 &&
            Array.isArray(result.data) &&
            result.data.length > 0
        ) {
          const imgId = result.data[0].id
          const realUrl = config.downloadUrl + imgId
          const html = `<img src="${realUrl}" data-img-id="${imgId}" style="max-width:100%;" />`
          editor.txt.append(html)
        } else {
          this.$message.error('图片上传失败')
        }
      }
    }

    this.editor.create()
    this.editor.txt.html(this.value)

    if (this.disabled) {
      this.editor.disable()
    }

  },
  watch: {
    value(newVal) {
      if (this.editor && this.editor.txt.html() !== newVal) {
        this.editor.txt.html(newVal)
      }
    },
    disabled(newVal) {
      if (!this.editor) return
      newVal ? this.editor.disable() : this.editor.enable()
    },
  },
  beforeDestroy() {
    if (this.editor) {
      this.editor.destroy()
      this.editor = null
    }
  },
}
</script>

<style scoped>
</style>
