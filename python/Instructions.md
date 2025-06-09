Create a python project with the following functions.

- **1. Source China A share stock daily market data using tushare or akshare and save into clickhouse database**:
    -   Allow user to select the start and end date
    -   Allow user to select the single stock code
    -   Allow user to select the benchmark index to load the constituents
    
- **2. Implement utility functions to calculate the technical indicators based on daily data**:
    -   Categorize the technical indicators into different categories
    -   Trigger the calculation based on the selected technical indicator category
    -   Store the calculated technical indicators into clickhouse database    

- **3. Calculate the worldquant101 factor, factor IC and factor rankIC and store into clickhouse database**

- **4. Machine learning model to combine factors into trading strategy that maximize the sharpe ratio**

- **5. Backtest the trading strategy and provide metrics in visualization**
