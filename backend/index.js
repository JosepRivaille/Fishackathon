import express from 'express';

const app = express();

app.get('/', (req, res) => {
    res.send('<html><head></head><body><h1>Hello world!</h1></body></html>')
});

app.listen(3000, () => {
    console.log('Listening on port 3000');
});
