const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const targetPath = '../../resources/META-INF'

module.exports = {
    mode: 'development',
    devServer: {
        port: 8081,
        contentBase: './dist',
     },
     entry: './src/index.js',
     output: {
         filename: 'bundle.js',
         path: path.resolve(__dirname + targetPath, 'resources'),
         publicPath: '/',
     },
     plugins: [
        new HtmlWebpackPlugin({
            inlineSource: '.(js|css)$',
            template: __dirname + `/src/index.html`,
            filename: __dirname + targetPath + `/resources/index.html`,
            inject: 'head',
          })
     ]
}