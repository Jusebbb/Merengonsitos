const { createProxyMiddleware } = require('http-proxy-middleware');

/**
 * Angular CLI espera que este archivo exporte:
 *  - Un objeto de opciones, o
 *  - Un ARREGLO de objetos de opciones compatibles con http-proxy-middleware.
 * NO acepta una función (app) {...} como en CRA.
 */
module.exports = [
  // 🧪 Mock de login SOLO en dev:
  {
    context: ['/api/auth/login'],
    target: 'http://localhost:8080', // no importa, vamos a "bypass"
    bypass: (req, res) => {
      if (req.method === 'POST') {
        res.setHeader('Content-Type', 'application/json');
        res.end(JSON.stringify({ token: 'dev-token', role: 'ADMIN' }));
        return true; // <- evita proxy y responde aquí mismo
      }
      return false; // otras rutas siguen al proxy
    },
  },

  // 🔁 Todo lo demás bajo /api => backend real
  {
    context: ['/api'],
    target: 'http://localhost:8080',
    changeOrigin: true,
    secure: false,
    logLevel: 'debug',
    // opcional: pathRewrite si tu back no tiene prefijo /api
    // pathRewrite: { '^/api': '' },
  },
];
