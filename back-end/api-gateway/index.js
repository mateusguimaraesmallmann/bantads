require("dotenv-safe").config();

const jwt = require("jsonwebtoken");
const http = require("http");
const express = require("express");
const httpProxy = require("express-http-proxy");
const cookieParser = require("cookie-parser");
const bodyParser = require("body-parser");
const logger = require("morgan");
const helmet = require("helmet");

const app = express();

// Configurações do app
app.use(logger('dev'));
app.use(helmet());
app.use(cookieParser());
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(bodyParser.urlencoded({ extended: false }));// parse application/x-www-form-urlencoded
app.use(bodyParser.json());// parse application/json

const authServiceProxy = httpProxy(process.env.MS_AUTH_URL, {
    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        try {
            retBody = {};
            retBody.login = bodyContent.login || bodyContent.user || bodyContent.email;
            retBody.login = bodyContent.senha || bodyContent.password;
            bodyContent = retBody;
        } catch (e) {
            console.log(' ERRO: ', e);
        }
        return bodyContent
    },
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },
    userResDecorator: function(proxyRes, proxyResData, userReq, userRes) {
        if (proxyRes.statusCode == 200){
            var str = Buffer.from(proxyResData).toString('utf-8');
            var objBody = JSON.parse(str)
            const id = objBody.id;
            const token = jwt.sign( {id}, process.env.JWT_SECRET, {
                expiresIn:300
            });
            userRes.status(200)
            return {auth: true, token: token, data: objBody}
        } else {
            userRes.status(401);
            return {message: 'Login inválido'}
        }
    }
});
const clienteServiceProxy = httpProxy(process.env.MS_CLIENTE_URL);
const contaServiceProxy = httpProxy(process.env.MS_CONTA_URL);
const gerentesServiceProxy = httpProxy(process.env.MS_GERENTE_URL);
const sagaServiceProxy = httpProxy(process.env.MS_SAGA_URL);

const JWT_SECRET = Buffer.from(process.env.JWT_SECRET, 'base64');

// Cria o servidor na porta 3000
var server = http.createServer(app);
server.listen(3000);

function verifyJWT(req, res, next) {
  const token = req.headers["x-access-token"] || (req.headers['authorization'] && req.headers['authorization'].split(' ')[1]);
  if (!token)
    return res
      .status(401)
      .json({ auth: false, message: "Token não fornecido." });


    jwt.verify(token, JWT_SECRET, function (err, decoded) {
        if (err) {
            console.log('Erro na verificação do token:', err.message);
            return res.status(401).json({ auth: false, message: 'Token inválido ou expirado.' });
        }

    req.user = decoded;
    next();
  });
}

// app.get('/reboot', verifyJWT, (req, res, next) => {
//     clientsServiceProxy(req, res, next);
// });

app.post('/login',  (req, res, next) => {
    authServiceProxy(req,res,next)
});

//CRIAÇÃO DE CONTA
app.post('/clientes', (req, res, next) => {
    sagaServiceProxy(req, res, next);
});

app.get('/clientes', verifyJWT, (req, res, next) => {
    clienteServiceProxy(req, res, next);
});

app.get('/clientes/:cpfCliente', verifyJWT, (req, res, next) => {
    clienteServiceProxy(req, res, next);
});

app.get('/gerentes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

app.get('/gerentes/:cpfGerente', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);    
});

app.post('/gerentes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

app.put('/gerentes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

app.delete('/gerentes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

app.put('/clientes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});


app.post('/logout', function(req, res) {
res.json({ auth: false, token: null });
})
