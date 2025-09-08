require("dotenv-safe").config();

const jwt = require("jsonwebtoken");
const http = require("http");
const express = require("express");
const httpProxy = require("express-http-proxy");
const cookieParser = require("cookie-parser");
const bodyParser = require("body-parser");
const logger = require("morgan");
const helmet = require("helmet");
const axios = require('axios');

const app = express();

// Configurações do app
app.use(logger('dev'));
app.use(helmet());
app.use(cookieParser());
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(bodyParser.urlencoded({ extended: false }));// parse application/x-www-form-urlencoded
app.use(bodyParser.json());// parse application/json

const BASE_URL_AUTH = "http://auth:8080"
const BASE_URL_CLIENTE = "http://clientes:8081"
const BASE_URL_CONTA = "http://contas:8082"
const BASE_URL_GERENTE = "http://gerentes:8083"
// const BASE_URL_ORCHESTRATOR = "http://orchestrator:8084"


const authServiceProxy = httpProxy(BASE_URL_AUTH);
const clientesServiceProxy = httpProxy(BASE_URL_CLIENTE);
const contasServiceProxy = httpProxy(BASE_URL_CONTA);
const gerentesServiceProxy = httpProxy(BASE_URL_GERENTE);

const JWT_SECRET = Buffer.from(process.env.JWT_SECRET, 'base64');

// Cria o servidor na porta 3000
// var server = http.createServer(app);
// server.listen(3000);

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

app.post('/login', async (req, res) => {
    try {
        console.log(req.body)
        const authBody = {
            login: req.body.login || req.body.email,  
            senha: req.body.senha || req.body.password, 
        };
        console.log(authBody)
        // Direcionar ao serviço
        const authResponse = await axios.post(`${BASE_URL_AUTH}/login`, authBody);
        console.log(authResponse)
        const authData = authResponse.data;

        // Verificação de id
        if (!authData.usuario.id) {
            return res.status(401).json({ message: 'Login inválido!' });
        }

        let usuarioResponse;
        let tipo = authData.tipo;
        let cpf = authData.usuario.cpf
        let access_token = authData.access_token
        let token_type = authData.token_type

        if (tipo === 'CLIENTE') {
            usuarioResponse = await axios.get(`${BASE_URL_CLIENTE}/clientes/${cpf}`);
        } else if (tipo === 'GERENTE') {
            usuarioResponse = await axios.get(`${BASE_URL_GERENTE}/gerentes/${cpf}`);
        } else {
            return res.status(500).json({ message: 'Tipo de usuário não existente.' });
        }

        // Monta resposta final
        return res.status(200).json({
            access_token,
            token_type,
            tipo,
            usuario: usuarioResponse.data
        });

    } catch (error) {
        if (error.response) {
            return res.status(error.response.status).json(error.response.data);
        }
        return res.status(500).json({ message: 'Erro ao efetuar login.', error: error.message });
    }
});

app.get('/clientes', verifyJWT, (req, res, next) => {
    clientesServiceProxy(req, res, next);
});

app.get('/clientes/:cpfCliente', verifyJWT, (req, res, next) => {
    clientesServiceProxy(req, res, next);
});

app.get('/gerentes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

app.get('/gerentes/:cpfGerente', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);    
});

app.post('/logout', function(req, res) {
res.json({ auth: false, token: null });
})
