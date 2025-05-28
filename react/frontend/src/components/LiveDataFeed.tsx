import { useState, useEffect } from 'react';
import { AgGridReact } from 'ag-grid-react';
import { ColDef } from 'ag-grid-community';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-alpine.css';
import axios from 'axios';

interface LiveData {
  timestamp: string;
  price: number;
  volume: number;
  bid: number;
  ask: number;
}

interface LiveDataFeedProps {
  symbol: string;
}

const LiveDataFeed = ({ symbol }: LiveDataFeedProps) => {
  const [rowData, setRowData] = useState<LiveData[]>([]);

  const columnDefs: ColDef[] = [
    { 
      field: 'timestamp', 
      headerName: '时间', 
      width: 180,
      valueFormatter: (params: any) => {
        const date = new Date(params.value);
        return date.toLocaleTimeString();
      }
    },
    { 
      field: 'price', 
      headerName: '价格', 
      width: 100,
      cellStyle: { color: 'white' }
    },
    { 
      field: 'volume', 
      headerName: '成交量', 
      width: 100,
      cellStyle: { color: 'white' }
    },
    { 
      field: 'bid', 
      headerName: '买入价', 
      width: 100,
      cellStyle: { color: 'green' }
    },
    { 
      field: 'ask', 
      headerName: '卖出价', 
      width: 100,
      cellStyle: { color: 'red' }
    }
  ];

  useEffect(() => {
    if (!symbol) return;

    const fetchLiveData = async () => {
      try {
        const response = await axios.get(`http://localhost:5000/api/stocks/${symbol}/live`);
        const data = response.data || [];
        setRowData(data);
      } catch (error) {
        console.error('Error fetching live data:', error);
        setRowData([]); // Set empty array on error
      }
    };

    fetchLiveData();
    
    const interval = setInterval(fetchLiveData, 2000);
    
    return () => clearInterval(interval);
  }, [symbol]);

  return (
    <div className="h-full w-full bg-black text-white border border-gray-700">
      <div className="p-2 bg-gray-900 text-white font-bold">实时数据</div>
      <div className="ag-theme-alpine-dark h-[calc(100%-40px)] w-full">
        <AgGridReact
          rowData={rowData}
          columnDefs={columnDefs}
          domLayout="autoHeight"
          defaultColDef={{
            sortable: true,
            filter: true,
            resizable: true
          }}
        />
      </div>
    </div>
  );
};

export default LiveDataFeed;
