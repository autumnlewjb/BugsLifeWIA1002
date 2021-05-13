module.exports = {
    devServer: {
        port: 3000,
        proxy: {
            '^/api': {
                target: 'http://spring-app',
                ws: true,
                changeOrigin: true
            }
        }
    }
}