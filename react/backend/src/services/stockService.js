const mockStocks = [
  { symbol: "002603", name: "以岭药业", price: 15.22, change: 0.8, changePercent: 5.55 },
  { symbol: "002317", name: "众生药业", price: 12.65, change: 0.05, changePercent: 0.41 },
  { symbol: "002317", name: "众生药业", price: 12.65, change: 0.42, changePercent: 3.35 },
  { symbol: "600085", name: "同仁堂", price: 28.45, change: 0, changePercent: 0 },
  { symbol: "600196", name: "复星医药", price: 32.18, change: 0, changePercent: 0 },
  { symbol: "000538", name: "云南白药", price: 89.75, change: 0, changePercent: 0 },
  { symbol: "W", name: "万科企业", price: 15.33, change: 0, changePercent: 0 },
  { symbol: "399300", name: "沪深300", price: 3889.09, change: -18.11, changePercent: -0.46 },
  { symbol: "399006", name: "创业板指", price: 2145.22, change: -0.59, changePercent: -0.03 },
  { symbol: "000768", name: "中航飞机", price: 40.62, change: 0.57, changePercent: 1.43 },
  { symbol: "000858", name: "五粮液", price: 105.33, change: 0, changePercent: 0 },
  { symbol: "588000", name: "科创50ETF", price: 1.044, change: -0.006, changePercent: -0.57 },
  { symbol: "588080", name: "科创50ETF", price: 1.017, change: -0.006, changePercent: -0.59 },
  { symbol: "399989", name: "中证医药", price: 6469.65, change: -39.00, changePercent: -0.60 }
];

const randomNumber = (min, max) => {
  return Math.random() * (max - min) + min;
};

const generateHistoricalPrices = (symbol, days = 30) => {
  const prices = [];
  const stock = mockStocks.find(s => s.symbol === symbol);
  
  if (!stock) {
    return [];
  }
  
  let basePrice = stock.price;
  
  for (let i = days; i >= 0; i--) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    
    const volatility = randomNumber(0.01, 0.03);
    const changePercent = randomNumber(-volatility, volatility);
    
    const open = basePrice * (1 + randomNumber(-0.01, 0.01));
    const close = open * (1 + changePercent);
    const high = Math.max(open, close) * (1 + randomNumber(0.001, 0.01));
    const low = Math.min(open, close) * (1 - randomNumber(0.001, 0.01));
    const volume = Math.floor(randomNumber(100000, 1000000));
    
    prices.push({
      date: date.toISOString().split("T")[0],
      open: parseFloat(open.toFixed(2)),
      high: parseFloat(high.toFixed(2)),
      close: parseFloat(close.toFixed(2)),
      low: parseFloat(low.toFixed(2)),
      volume
    });
    
    basePrice = close;
  }
  
  return prices;
};

const generateLiveData = (symbol, count = 10) => {
  const liveData = [];
  const stock = mockStocks.find(s => s.symbol === symbol);
  
  if (!stock) {
    return [];
  }
  
  const basePrice = stock.price;
  
  for (let i = 0; i < count; i++) {
    const timestamp = new Date(Date.now() - i * 1000).toISOString();
    const price = basePrice * (1 + randomNumber(-0.002, 0.002));
    const volume = Math.floor(randomNumber(1000, 10000));
    const spread = randomNumber(0.01, 0.05);
    const bid = price * (1 - spread/2);
    const ask = price * (1 + spread/2);
    
    liveData.push({
      timestamp,
      price: parseFloat(price.toFixed(2)),
      volume,
      bid: parseFloat(bid.toFixed(2)),
      ask: parseFloat(ask.toFixed(2))
    });
  }
  
  return liveData;
};

const generateMinuteData = (symbol, minutes = 60) => {
  const minuteData = [];
  const stock = mockStocks.find(s => s.symbol === symbol);
  
  if (!stock) {
    return [];
  }
  
  let basePrice = stock.price;
  
  for (let i = minutes; i >= 0; i--) {
    const date = new Date();
    date.setMinutes(date.getMinutes() - i);
    
    const volatility = randomNumber(0.001, 0.005);
    const changePercent = randomNumber(-volatility, volatility);
    const price = basePrice * (1 + changePercent);
    const volume = Math.floor(randomNumber(1000, 50000));
    
    minuteData.push({
      time: date.toISOString().split("T")[0] + " " + date.toTimeString().split(" ")[0],
      price: parseFloat(price.toFixed(2)),
      volume
    });
    
    basePrice = price;
  }
  
  return minuteData;
};

const generateTechnicalIndicators = (symbol, days = 30) => {
  const indicators = [];
  const historicalPrices = generateHistoricalPrices(symbol, days);
  
  if (historicalPrices.length === 0) {
    return [];
  }
  
  for (let i = 0; i < historicalPrices.length; i++) {
    const date = historicalPrices[i].date;
    const price = historicalPrices[i].close;
    
    const sma20 = calculateSMA(historicalPrices, i, 20);
    const sma50 = calculateSMA(historicalPrices, i, 50);
    const sma200 = calculateSMA(historicalPrices, i, 200);
    
    const rsi = 50 + randomNumber(-20, 20);
    
    const macd = randomNumber(-2, 2);
    const signal = macd + randomNumber(-0.5, 0.5);
    const histogram = macd - signal;
    
    indicators.push({
      date,
      sma20: parseFloat(sma20.toFixed(2)),
      sma50: parseFloat(sma50.toFixed(2)),
      sma200: parseFloat(sma200.toFixed(2)),
      rsi: parseFloat(rsi.toFixed(2)),
      macd: parseFloat(macd.toFixed(2)),
      signal: parseFloat(signal.toFixed(2)),
      histogram: parseFloat(histogram.toFixed(2))
    });
  }
  
  return indicators;
};

const calculateSMA = (prices, currentIndex, period) => {
  let sum = 0;
  let count = 0;
  
  for (let i = Math.max(0, currentIndex - period + 1); i <= currentIndex; i++) {
    sum += prices[i].close;
    count++;
  }
  
  return count > 0 ? sum / count : prices[currentIndex].close;
};

const getStocks = () => {
  return mockStocks;
};

const getStockBySymbol = (symbol) => {
  return mockStocks.find(stock => stock.symbol === symbol);
};

module.exports = {
  getStocks,
  getStockBySymbol,
  generateHistoricalPrices,
  generateLiveData,
  generateMinuteData,
  generateTechnicalIndicators
};
