import express from 'express';
import * as stockService from '../services/stockService';

const router = express.Router();

router.get('/', (req, res) => {
  const stocks = stockService.getStocks();
  res.json(stocks);
});

router.get('/:symbol', (req, res) => {
  const { symbol } = req.params;
  const stock = stockService.getStockBySymbol(symbol);
  
  if (!stock) {
    return res.status(404).json({ message: `Stock with symbol ${symbol} not found` });
  }
  
  res.json(stock);
});

router.get('/:symbol/historical', (req, res) => {
  const { symbol } = req.params;
  const days = req.query.days ? parseInt(req.query.days as string) : 30;
  
  const historicalPrices = stockService.generateHistoricalPrices(symbol, days);
  
  if (historicalPrices.length === 0) {
    return res.status(404).json({ message: `Stock with symbol ${symbol} not found` });
  }
  
  res.json(historicalPrices);
});

router.get('/:symbol/live', (req, res) => {
  const { symbol } = req.params;
  const count = req.query.count ? parseInt(req.query.count as string) : 10;
  
  const liveData = stockService.generateLiveData(symbol, count);
  
  if (liveData.length === 0) {
    return res.status(404).json({ message: `Stock with symbol ${symbol} not found` });
  }
  
  res.json(liveData);
});

router.get('/:symbol/minute', (req, res) => {
  const { symbol } = req.params;
  const minutes = req.query.minutes ? parseInt(req.query.minutes as string) : 60;
  
  const minuteData = stockService.generateMinuteData(symbol, minutes);
  
  if (minuteData.length === 0) {
    return res.status(404).json({ message: `Stock with symbol ${symbol} not found` });
  }
  
  res.json(minuteData);
});

router.get('/:symbol/indicators', (req, res) => {
  const { symbol } = req.params;
  const days = req.query.days ? parseInt(req.query.days as string) : 30;
  
  const indicators = stockService.generateTechnicalIndicators(symbol, days);
  
  if (indicators.length === 0) {
    return res.status(404).json({ message: `Stock with symbol ${symbol} not found` });
  }
  
  res.json(indicators);
});

export default router;
