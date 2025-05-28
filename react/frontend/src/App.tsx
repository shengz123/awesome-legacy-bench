import { useState, useEffect } from 'react';
import './App.css';
import StockWatchlist from './components/StockWatchlist';
import CandlestickChart from './components/CandlestickChart';
import LiveDataFeed from './components/LiveDataFeed';
import MinuteDataFeed from './components/MinuteDataFeed';
import TechnicalIndicatorsChart from './components/TechnicalIndicatorsChart';
import axios from 'axios';

function App() {
  const [selectedStock, setSelectedStock] = useState<string>('399300'); // Default to 沪深300
  const [isBackendAvailable, setIsBackendAvailable] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    // Check if backend is available
    const checkBackend = async () => {
      try {
        setIsLoading(true);
        await axios.get('http://localhost:5000/api/stocks');
        setIsBackendAvailable(true);
        setIsLoading(false);
      } catch (error) {
        console.error('Error connecting to backend:', error);
        setIsBackendAvailable(false);
        setIsLoading(false);
      }
    };

    checkBackend();
  }, []);

  const handleStockSelect = (symbol: string) => {
    setSelectedStock(symbol);
  };

  if (isLoading) {
    return (
      <div className="bg-black text-white min-h-screen flex items-center justify-center">
        <div className="text-xl">Loading...</div>
      </div>
    );
  }

  if (!isBackendAvailable) {
    return (
      <div className="bg-black text-white min-h-screen flex items-center justify-center">
        <div className="text-xl text-red-500">
          Error connecting to backend server. Please make sure the backend is running on port 5000.
        </div>
      </div>
    );
  }

  return (
    <div className="bg-black text-white min-h-screen">
      <header className="bg-gray-900 p-4 text-center">
        <h1 className="text-2xl font-bold">股票行情分析系统</h1>
      </header>
      
      <div className="container mx-auto p-4">
        <div className="grid grid-cols-12 gap-4 h-[800px]">
          {/* Area 1: Stock Watchlist */}
          <div className="col-span-3 h-full">
            <StockWatchlist onSelectStock={handleStockSelect} />
          </div>
          
          <div className="col-span-9 grid grid-rows-2 gap-4 h-full">
            <div className="grid grid-cols-2 gap-4">
              {/* Area 2: Candlestick Chart */}
              <div className="h-full">
                <CandlestickChart symbol={selectedStock} />
              </div>
              
              {/* Area 3: Live Data Feed */}
              <div className="h-full">
                <LiveDataFeed symbol={selectedStock} />
              </div>
            </div>
            
            <div className="grid grid-cols-2 gap-4">
              {/* Area 4: Minute Data Feed */}
              <div className="h-full">
                <MinuteDataFeed symbol={selectedStock} />
              </div>
              
              {/* Area 5: Technical Indicators */}
              <div className="h-full">
                <TechnicalIndicatorsChart symbol={selectedStock} />
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <footer className="bg-gray-900 p-4 text-center mt-8">
        <p>股票行情分析系统 &copy; 2025</p>
      </footer>
    </div>
  );
}

export default App;
