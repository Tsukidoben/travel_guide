const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack');

module.exports = {
  publicPath: "/",
  lintOnSave: false,
  transpileDependencies: true,
  devServer: {
    client:{
      overlay: false
    },
    hot: true,
    port: 8081,
    open: true,
  },
  css: {
    loaderOptions: {
      scss: {
        additionalData: `@use "@/assets/css/vars.scss" as *;`,
      },
    }
  },
}
