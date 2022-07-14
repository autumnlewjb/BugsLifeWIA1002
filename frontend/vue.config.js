module.exports = {
  devServer: {
    port: 3000,
    proxy: {
      "^/api": {
        target: "https://bugs-life.herokuapp.com",
        ws: true,
        changeOrigin: true,
      },
    },
  },

  runtimeCompiler: true,

  transpileDependencies: ["vuetify"],
};
