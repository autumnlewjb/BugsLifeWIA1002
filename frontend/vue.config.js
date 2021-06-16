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
        },
        workboxPluginMode: 'GenerateSW',
        workboxOptions: {
	    navigateFallback: 'index.html',
            runtimeCaching: [
                {
                    handler: 'NetworkFirst',
                    urlPattern: new RegExp('.*/api/.*'),
                    options: {
                        cacheName: 'http-req',
                        expiration: {
                            maxAgeSeconds: 60 * 60
                        }
                    }
                }
            ]
        }
    }
}
