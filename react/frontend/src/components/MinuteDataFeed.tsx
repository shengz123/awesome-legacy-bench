import { useState, useEffect } from 'react';
import { AgGridReact } from 'ag-grid-react';
import { ColDef } from 'ag-grid-community';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-alpine.css';
import axios from 'axios';

interface MinuteData {
  time: string;
  price: number;
  volume: number;
}

interface MinuteDataFeedProps {
  symbol: string;
}

const MinuteDataFeed = ({ symbol }: MinuteDataFeedProps) => {
  const [rowData, setRowData] = useState<MinuteData[]>([]);

  const columnDefs: ColDef[] = [
    { 
      field: 'time', 
      headerName: '时间', 
      width: 180
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
    }
  ];

  useEffect(() => {
    if (!symbol) return;

    const fetchMinuteData = async () => {
      try {
        const response = await axios.get(`http://localhost:5000/api/stocks/${symbol}/minute`);
        const data = response.data || [];
        setRowData(data);
      } catch (error) {
        console.error('Error fetching minute data:', error);
        setRowData([]); // Set empty array on error
      }
    };

    fetchMinuteData();
    
    const interval = setInterval(fetchMinuteData, 60000);
    
    return () => clearInterval(interval);
  }, [symbol]);

  return (
    <div className="h-full w-full bg-black text-white border border-gray-700">
      <div className="p-2 bg-gray-900 text-white font-bold">分钟数据</div>
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

export default MinuteDataFeed;
