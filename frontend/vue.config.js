module.exports = {
    devServer: {
        port: 3000,
        proxy: {
            '^/api': {
                target: 'http://localhost:8080',
                ws: true,
                changeOrigin: true
            }
        }
    },

    runtimeCompiler: true,

    transpileDependencies: [
      'vuetify'
    ],

    pwa: {
        name: 'BugsLife',
        themeColor: '#FFFAFA',
        appleMobileWebAppCapable: 'yes',
        iconPaths: {
            msTileImage: 'img/icons/mstile-150x150.png'
        }
    }
}
