import { useState, useEffect } from 'react';
import { AgGridReact } from 'ag-grid-react';
import { ColDef } from 'ag-grid-community';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-alpine.css';
import axios from 'axios';

interface Stock {
  symbol: string;
  name: string;
  price: number;
  change: number;
  changePercent: number;
}

interface StockWatchlistProps {
  onSelectStock: (symbol: string) => void;
}

const StockWatchlist = ({ onSelectStock }: StockWatchlistProps) => {
  const [rowData, setRowData] = useState<Stock[]>([]);
  const [selectedStock, setSelectedStock] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const columnDefs: ColDef[] = [
    { field: 'symbol', headerName: '代码', width: 100 },
    { field: 'name', headerName: '名称', width: 120 },
    { 
      field: 'price', 
      headerName: '价格', 
      width: 100,
      cellStyle: (params: any) => {
        return { color: params.data?.change >= 0 ? 'green' : 'red' };
      }
    },
    { 
      field: 'change', 
      headerName: '涨跌', 
      width: 100,
      cellStyle: (params: any) => {
        return { color: params.data?.change >= 0 ? 'green' : 'red' };
      }
    },
    { 
      field: 'changePercent', 
      headerName: '涨跌%', 
      width: 100,
      valueFormatter: (params: any) => {
        // Handle undefined or null values
        if (params.value === undefined || params.value === null) {
          return '';
        }
        return params.value.toFixed(2) + '%';
      },
      cellStyle: (params: any) => {
        return { color: params.data?.change >= 0 ? 'green' : 'red' };
      }
    }
  ];

  useEffect(() => {
    const fetchStocks = async () => {
      try {
        setIsLoading(true);
        setError(null);
        
        const response = await axios.get('http://localhost:5000/api/stocks');
        const data = response.data || [];
        
        setRowData(data);
        setIsLoading(false);
        
        if (data.length > 0 && !selectedStock) {
          handleStockSelect(data[0].symbol);
        }
      } catch (error) {
        console.error('Error fetching stocks:', error);
        setRowData([]); // Set empty array on error
        setError("Failed to load stocks");
        setIsLoading(false);
      }
    };

    fetchStocks();
    const interval = setInterval(fetchStocks, 10000);
    
    return () => clearInterval(interval);
  }, [selectedStock]);

  const handleStockSelect = (symbol: string) => {
    setSelectedStock(symbol);
    onSelectStock(symbol);
  };

  return (
    <div className="h-full w-full bg-black text-white border border-gray-700">
      <div className="p-2 bg-gray-900 text-white font-bold">股票列表</div>
      <div className="ag-theme-alpine-dark h-[calc(100%-40px)] w-full">
        {isLoading && <div className="flex items-center justify-center h-full">Loading...</div>}
        {error && <div className="flex items-center justify-center h-full text-red-500">{error}</div>}
        {!isLoading && !error && (
          <AgGridReact
            rowData={rowData}
            columnDefs={columnDefs}
            rowSelection="single"
            onRowClicked={(event: any) => handleStockSelect(event.data?.symbol)}
            domLayout="autoHeight"
            defaultColDef={{
              sortable: true,
              filter: true,
              resizable: true
            }}
          />
        )}
      </div>
    </div>
  );
};

export default StockWatchlist;
