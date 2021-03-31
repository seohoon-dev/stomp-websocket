const path = require('path');

module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  outputDir: path.resolve(__dirname, "../src/main/resources/static"),
  devServer: {
    proxy: {
      "/api": {
        target: "http://localhost:10000",
        ws: true,
        changeOrigin: true
      }
    }
  }

}