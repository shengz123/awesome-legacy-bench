import { useEffect, useRef, useState } from 'react';
import { Chart, registerables } from 'chart.js';
import 'chartjs-adapter-date-fns';
import axios from 'axios';

Chart.register(...registerables);

interface TechnicalIndicator {
  date: string;
  sma20: number;
  sma50: number;
  sma200: number;
  rsi: number;
  macd: number;
  signal: number;
  histogram: number;
}

interface TechnicalIndicatorsChartProps {
  symbol: string;
}

const TechnicalIndicatorsChart = ({ symbol }: TechnicalIndicatorsChartProps) => {
  const chartRef = useRef<HTMLCanvasElement>(null);
  const chartInstance = useRef<Chart | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!symbol) return;

    const fetchIndicatorsData = async () => {
      try {
        setIsLoading(true);
        setError(null);
        
        const response = await axios.get(`http://localhost:5000/api/stocks/${symbol}/indicators`);
        const data: TechnicalIndicator[] = response.data || [];
        
        if (data.length === 0) {
          setError("No indicator data available");
          setIsLoading(false);
          return;
        }
        
        renderChart(data);
        setIsLoading(false);
      } catch (error) {
        console.error('Error fetching technical indicators:', error);
        setError("Failed to load indicator data");
        setIsLoading(false);
      }
    };

    fetchIndicatorsData();

    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
        chartInstance.current = null;
      }
    };
  }, [symbol]);

  const renderChart = (data: TechnicalIndicator[]) => {
    if (!chartRef.current) return;

    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    const ctx = chartRef.current.getContext('2d');
    if (!ctx) return;

    try {
      const dates = data.map(item => new Date(item.date));
      const sma20Data = data.map(item => item.sma20);
      const sma50Data = data.map(item => item.sma50);
      const sma200Data = data.map(item => item.sma200);
      const rsiData = data.map(item => item.rsi);
      const macdData = data.map(item => item.macd);
      const signalData = data.map(item => item.signal);
      
      // Histogram data is calculated but not used in this chart
      // We could add it as a bar chart in the future if needed
      // const histogramData = data.map(item => item.histogram);

      chartInstance.current = new Chart(ctx, {
        type: 'line',
        data: {
          labels: dates,
          datasets: [
            {
              label: 'SMA 20',
              data: sma20Data,
              borderColor: 'rgba(255, 99, 132, 1)',
              borderWidth: 1,
              fill: false
            },
            {
              label: 'SMA 50',
              data: sma50Data,
              borderColor: 'rgba(54, 162, 235, 1)',
              borderWidth: 1,
              fill: false
            },
            {
              label: 'SMA 200',
              data: sma200Data,
              borderColor: 'rgba(255, 206, 86, 1)',
              borderWidth: 1,
              fill: false
            },
            {
              label: 'RSI',
              data: rsiData,
              borderColor: 'rgba(75, 192, 192, 1)',
              borderWidth: 1,
              fill: false,
              hidden: true
            },
            {
              label: 'MACD',
              data: macdData,
              borderColor: 'rgba(153, 102, 255, 1)',
              borderWidth: 1,
              fill: false,
              hidden: true
            },
            {
              label: 'Signal',
              data: signalData,
              borderColor: 'rgba(255, 159, 64, 1)',
              borderWidth: 1,
              fill: false,
              hidden: true
            }
          ]
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
              position: 'top',
              labels: {
                color: 'rgba(255, 255, 255, 0.7)'
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
      <div className="p-2 bg-gray-900 text-white font-bold">技术指标</div>
      <div className="h-[calc(100%-40px)] w-full p-2">
        {isLoading && <div className="flex items-center justify-center h-full">Loading...</div>}
        {error && <div className="flex items-center justify-center h-full text-red-500">{error}</div>}
        <canvas ref={chartRef} style={{ display: isLoading || error ? 'none' : 'block' }} />
      </div>
    </div>
  );
};

export default TechnicalIndicatorsChart;
