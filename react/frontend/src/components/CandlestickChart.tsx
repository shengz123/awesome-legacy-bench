import { useEffect, useRef, useState } from 'react';
import { Chart, registerables } from 'chart.js';
import 'chartjs-adapter-date-fns';
import axios from 'axios';
import { CandlestickController, CandlestickElement, OhlcController, OhlcElement } from 'chartjs-chart-financial';

// Register the financial chart components
Chart.register(...registerables, CandlestickController, CandlestickElement, OhlcController, OhlcElement);

interface HistoricalPrice {
  date: string;
  open: number;
  high: number;
  close: number;
  low: number;
  volume: number;
}

interface CandlestickChartProps {
  symbol: string;
}

const CandlestickChart = ({ symbol }: CandlestickChartProps) => {
  const chartRef = useRef<HTMLCanvasElement>(null);
  const chartInstance = useRef<Chart | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!symbol) return;

    const fetchHistoricalData = async () => {
      try {
        setIsLoading(true);
        setError(null);
        
        const response = await axios.get(`http://localhost:5000/api/stocks/${symbol}/historical`);
        const data: HistoricalPrice[] = response.data || [];
        
        if (data.length === 0) {
          setError("No historical data available");
          setIsLoading(false);
          return;
        }
        
        renderChart(data);
        setIsLoading(false);
      } catch (error) {
        console.error('Error fetching historical data:', error);
        setError("Failed to load historical data");
        setIsLoading(false);
      }
    };

    fetchHistoricalData();

    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
        chartInstance.current = null;
      }
    };
  }, [symbol]);

  const renderChart = (data: HistoricalPrice[]) => {
    if (!chartRef.current) return;

    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    const ctx = chartRef.current.getContext('2d');
    if (!ctx) return;

    try {
      // Convert data to the format expected by the financial chart
      const chartData = data.map(item => ({
        x: new Date(item.date),
        o: item.open,
        h: item.high,
        l: item.low,
        c: item.close
      }));

      // @ts-ignore - Ignore type errors for financial chart
      chartInstance.current = new Chart(ctx, {
        type: 'candlestick',
        data: {
          datasets: [{
            label: `${symbol} Price`,
            data: chartData,
            // @ts-ignore - Candlestick specific properties
            color: {
              up: 'rgba(0, 255, 0, 1)',
              down: 'rgba(255, 0, 0, 1)',
              unchanged: 'rgba(0, 0, 255, 1)',
            }
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: {
              type: 'time',
              time: {
                unit: 'day'
              },
              grid: {
                color: 'rgba(255, 255, 255, 0.1)'
              },
              ticks: {
                color: 'rgba(255, 255, 255, 0.7)'
              }
            },
            y: {
              grid: {
                color: 'rgba(255, 255, 255, 0.1)'
              },
              ticks: {
                color: 'rgba(255, 255, 255, 0.7)'
              }
            }
          },
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: (context: any) => {
                  const point = context.raw;
                  return [
                    `Open: ${point.o}`,
                    `High: ${point.h}`,
                    `Low: ${point.l}`,
                    `Close: ${point.c}`
                  ];
                }
              }
            }
          }
        }
      });
    } catch (error) {
      console.error('Error rendering chart:', error);
      setError("Failed to render chart");
    }
  };

  return (
    <div className="h-full w-full bg-black text-white border border-gray-700">
      <div className="p-2 bg-gray-900 text-white font-bold">历史价格图表</div>
      <div className="h-[calc(100%-40px)] w-full p-2">
        {isLoading && <div className="flex items-center justify-center h-full">Loading...</div>}
        {error && <div className="flex items-center justify-center h-full text-red-500">{error}</div>}
        <canvas ref={chartRef} style={{ display: isLoading || error ? 'none' : 'block' }} />
      </div>
    </div>
  );
};

export default CandlestickChart;
