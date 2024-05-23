// server.js

const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// MongoDB 연결
mongoose.connect(process.env.MONGO_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true
}).then(() => {
    console.log('Connected to MongoDB');
}).catch(err => {
    console.error('Error connecting to MongoDB:', err.message);
});

// 모델 정의
const Data = mongoose.model('Data', new mongoose.Schema({
    message: String
}));

// API 라우트 설정
app.use(bodyParser.json());

// 데이터 저장 API 엔드포인트
app.post('/api/save-data', async (req, res) => {
    try {
        const { message } = req.body;
        const newData = new Data({ message });
        await newData.save();
        res.json({ success: true });
    } catch (error) {
        console.error('Error saving data to MongoDB:', error.message);
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
