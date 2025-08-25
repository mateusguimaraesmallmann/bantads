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

const BASE_URL_AUTH = "http://auth:8080"
const BASE_URL_CLIENTE = "http://clientes:8081"
const BASE_URL_CONTA = "http://contas:8082"
const BASE_URL_GERENTE = "http://gerentes:8083"
// const BASE_URL_ORCHESTRATOR = "http://orchestrator:8084"


const authServiceProxy = httpProxy(BASE_URL_AUTH);
const cclientesServiceProxy = httpProxy(BASE_URL_CLIENTE);
const contasServiceProxy = httpProxy(BASE_URL_CONTA);
const gerentesServiceProxy = httpProxy(BASE_URL_GERENTE);

const JWT_SECRET = Buffer.from(process.env.JWT_SECRET, 'base64');

// Cria o servidor na porta 3000
// var server = http.createServer(app);
// server.listen(3000);

function verifyJWT(req, res, next) {
  const token = req.headers["x-access-token"];
  if (!token)
    return res
      .status(401)
      .json({ auth: false, message: "Token não fornecido." });
  jwt.verify(token, process.env.SECRET, function (err, decoded) {
    if (err)
      return res
        .status(500)
        .json({ auth: false, message: "Falha ao autenticar o token." });
    // se tudo estiver ok, salva no request para uso posterior

    req.user = decoded;
    next();
  });
}

app.post("/login", urlencodedParser, (req, res, next) => {
  // Esse teste deve ser feito invocando um serviço apropriado
  if (req.body.user === "admin" && req.body.password === "admin") {
    // auth ok
    const id = 1; // esse id viria do serviço de autenticação
    const token = jwt.sign({ id }, process.env.SECRET, {
      expiresIn: 300, // expira em 5min
    });
    return res.json({ auth: true, token: token });
  }
  res.status(500).json({ message: "Login inválido!" });
});

app.post('/logout', function(req, res) {
res.json({ auth: false, token: null });
})
