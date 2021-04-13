const path = require('path');

module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  outputDir: path.resolve(__dirname, "../src/main/resources/static"),
  devServer: {
    proxy: {
      "/demo-chatting": {
        target: "http://localhost:8080",
        ws: true,
        changeOrigin: true
      }
    }
  }

}
