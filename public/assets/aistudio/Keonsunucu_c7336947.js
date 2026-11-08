const http = require('http');
const net = require('net');

const PORT = 8080;
const AI_CORE_IP = '127.0.0.1'; // AI beyninin çalıştığı yerel IP veya sunucu IP'si
const AI_CORE_PORT = 9090;      // AI beyninin dinlediği port

const server = http.createServer((req, res) => {
    const socket = net.createConnection({ host: AI_CORE_IP, port: AI_CORE_PORT }, () => {
        req.pipe(socket);
    });

    socket.pipe(res);
    
    socket.on('error', (err) => {
        res.writeHead(500);
        res.end('Yapay zeka beynine bağlantı başarısız: ' + err.message);
    });
});

server.listen(PORT, () => {
    console.log(`Sınırsız Proxy Sunucusu ${PORT} portunda çalışıyor. AI beynine doğrudan bağlantı hazır.`);
});
