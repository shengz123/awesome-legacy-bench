import express from 'express';
import cors from 'cors';
import stockRoutes from './routes/stockRoutes';

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
  res.json({ message: 'Stock Dashboard API is running' });
});

app.use('/api/stocks', stockRoutes);

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

export default app;
