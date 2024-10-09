import React, {useEffect, useState} from 'react';
import {getUpbitList} from '../../api/Api';
import '../../layouts/css/UpbitMainPage.css';

function UpbitMainPage() {
    const [serverData, setServerData] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedMarket, setSelectedMarket] = useState('all'); // 필터 상태 추가
    const [filteredData, setFilteredData] = useState({krwList: [], btcList: [], usdtList: []});
    const [priceData, setPriceData] = useState({});

    useEffect(() => {
        getUpbitList().then(data => {
            setServerData(data);
            filterData(data, searchTerm, selectedMarket);
        });
    }, [searchTerm, selectedMarket]); // 검색어와 필터가 변경될 때마다 실행

    /*
        Upbit API < (WebSoket) > APISERVER -> React 실시간 데이터 스트림
     */
    useEffect(() => {
        const url = window.location.protocol + '//' + window.location.host;
        const eventSource = new EventSource(url + '/api/sse/price');
        //const eventSource = new EventSource('/api/sse/price');

        eventSource.addEventListener("price", (event) => {
            const parsedData = JSON.parse(event.data);
            setPriceData(prevData => ({
                ...prevData,
                [parsedData.code]: parsedData.trade_price // market을 키로 해서 가격 업데이트
            }));
        });

        return () => {
            console.log("Closing SSE connection");
            eventSource.close();
        };
    }, []);


    const handleSearch = (e) => {
        const search = e.target.value.toLowerCase();
        setSearchTerm(search);
        filterData(serverData, search, selectedMarket);
    };

    const handleMarketFilter = (e) => {
        const market = e.target.value;
        setSelectedMarket(market);
        filterData(serverData, searchTerm, market);
    };

    const filterData = (data, search, market) => {
        let krwList = [];
        let btcList = [];
        let usdtList = [];

        for (let key in data) {
            const marketKey = data[key].market;
            if (
                marketKey.startsWith('KRW-') &&
                data[key].korean_name.toLowerCase().includes(search) &&
                (market === 'all' || market === 'krw')
            ) {
                krwList.push(data[key]);
            } else if (
                marketKey.startsWith('BTC-') &&
                data[key].korean_name.toLowerCase().includes(search) &&
                (market === 'all' || market === 'btc')
            ) {
                btcList.push(data[key]);
            } else if (
                data[key].korean_name.toLowerCase().includes(search) &&
                (market === 'all' || market === 'usdt')
            ) {
                usdtList.push(data[key]);
            }
        }
        setFilteredData({krwList, btcList, usdtList});
    };

    return (
        <div>
            {/* 상단 버튼 및 검색바, 필터 */}
            <div className="search-and-buttons">
                <div>
                    <button onClick={() => alert('Button 1 Clicked!')}>Button 1</button>
                    <button onClick={() => alert('Button 2 Clicked!')}>Button 2</button>
                    <button onClick={() => alert('Button 3 Clicked!')}>Button 3</button>
                </div>
                <div style={{display: 'flex', alignItems: 'center'}}>
                    <input
                        type="text"
                        placeholder="Search by Korean Name..."
                        value={searchTerm}
                        onChange={handleSearch}
                    />
                    {/* 필터 추가 */}
                    <select value={selectedMarket} onChange={handleMarketFilter}>
                        <option value="all">All Markets</option>
                        <option value="krw">KRW Market</option>
                        <option value="btc">BTC Market</option>
                        <option value="usdt">USDT Market</option>
                    </select>
                </div>
            </div>

            <div className="market-lists">
                {/* KRW List (좌측) */}
                <div className="market-list">
                    <h3>KRW Market : {filteredData.krwList.length} 항목</h3>
                    {filteredData.krwList.map((item, index) => (
                        <div key={index} className="market-item">
                            <div>{item.korean_name} ({item.english_name})</div>
                            <div>{item.market}</div>
                            <div>{priceData && priceData[item.market] ? priceData[item.market].toLocaleString() : 'Price not available'}</div>
                        </div>
                    ))}
                </div>

                {/* BTC List (중앙) */}
                <div className="market-list">
                    <h3>BTC Market : {filteredData.btcList.length} 항목</h3>
                    {filteredData.btcList.map((item, index) => (
                        <div key={index} className="market-item">
                            <div>{item.korean_name} ({item.english_name})</div>
                            <div>{item.market}</div>
                            <div>{priceData && priceData[item.market] ? priceData[item.market] : 'Price not available'}</div>
                        </div>
                    ))}
                </div>

                {/* USDT List (우측) */}
                <div className="market-list">
                    <h3>USDT Market : {filteredData.usdtList.length} 항목</h3>
                    {filteredData.usdtList.map((item, index) => (
                        <div key={index} className="market-item">
                            <div>{item.korean_name} ({item.english_name})</div>
                            <div>{item.market}</div>
                            <div>{priceData && priceData[item.market] ? priceData[item.market].toLocaleString() : 'Price not available'}</div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default UpbitMainPage;
