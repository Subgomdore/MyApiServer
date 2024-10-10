import React, { useEffect, useState } from 'react';
import { getUpbitList } from '../../api/Api';
import '../../layouts/css/UpbitMainPage.css';
import '../../layouts/css/PriceAnimation.css'

function UpbitMainPage() {
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedMarket, setSelectedMarket] = useState('all'); // 필터 상태 추가
    const [filteredData, setFilteredData] = useState({ krwList: [], btcList: [], usdtList: [] });
    const [btcPrice, setBtcPrice] = useState(0);

    useEffect(() => {
        getUpbitList().then(data => {
            filterData(data, searchTerm, selectedMarket);
        });
    }, [searchTerm, selectedMarket]); // 검색어와 필터가 변경될 때마다 실행

    useEffect(() => {
        const url = window.location.protocol + '//' + window.location.host;
        const eventSource = new EventSource(url + '/api/sse/price');

        // Ping을 주기적으로 전송하는 함수
        const pingServer = () => {
            fetch(url + '/api/sse/ping').catch(error => console.error("Ping error: ", error));
        };

        // 30초마다 ping을 보냄
        const pingInterval = setInterval(pingServer, 10000);

        eventSource.addEventListener("price", (event) => {
            let parsedData = JSON.parse(event.data);

            if (parsedData.code === 'KRW-BTC') {
                setBtcPrice(parsedData.trade_price); // BTC 가격 상태로 저장
            }

            // SSE로 수신된 가격 데이터를 filteredData에 업데이트
            setFilteredData(prevData => {
                const updatedKrwList = prevData.krwList.map(item => {
                    if (item.market === parsedData.code) {
                        const oldPrice = Number(item.price);
                        const newPrice = Number(parsedData.trade_price);
                        const priceChangeClass = newPrice > oldPrice ? 'price-up change' : 'price-down change'; // 가격 변화 클래스 결정

                        setTimeout(() => {
                            setFilteredData(prev => {
                                return {
                                    ...prev,
                                    krwList: prev.krwList.map(i => {
                                        if (i.market === parsedData.code) {
                                            return { ...i, priceChangeClass: priceChangeClass.replace(' change', '') }; // 반짝임 효과 제거
                                        }
                                        return i;
                                    })
                                };
                            });
                        }, 300); // 0.3초 후에 클래스 제거

                        return {
                            ...item,
                            price: formatPrice(newPrice), // 가격 형식화
                            priceChangeClass: priceChangeClass // 클래스 추가
                        };
                    }
                    return item;
                });

                return {
                    ...prevData,
                    krwList: updatedKrwList
                };
            });
        });

        return () => {
            console.log("Closing SSE connection");
            eventSource.close();
            clearInterval(pingInterval); // ping interval 정리
        };
    }, []); // 한 번만 실행


    const formatPrice = (price) => {
        // 숫자로 변환 후 소수점 이하 처리
        let num = Number(price);

        if (num > 1000) {
            return Math.floor(num).toLocaleString(); // 1000원 초과 시 소수점 이하 제거
        } else if (num <= 1000 && num > 1) {
            return num.toFixed(1).toString(); // 1000원 이하일 경우 소수점 1자리까지 표시
        } else if (num < 1) {
            const convertValue = num.toFixed(10);
            return convertValue.replace(/\.?0+$/, ''); // 1보다 작은 경우 유효한 자리수만 표시
        } else if (!isNaN(num)) {
            return num.toString(); // 지수형 숫자 처리
        }
        return 'Price not available'; // 가격이 0인 경우
    };

    const handleSearch = (e) => {
        const search = e.target.value.toLowerCase();
        setSearchTerm(search);
        filterData(filteredData, search, selectedMarket);
    };

    const handleMarketFilter = (e) => {
        const market = e.target.value;
        setSelectedMarket(market);
        filterData(filteredData, searchTerm, market);
    };

    const filterData = (allData, search, market) => {
        let krwList = [];
        let btcList = [];
        let usdtList = [];

        const data = allData['COIN'];
        const priceList = allData['PRICE'];

        for (let key in data) {
            const marketKey = data[key].market;

            // 가격 초기화
            for (let key2 in priceList) {
                if (priceList[key2].market === marketKey) {
                    data[key].price = formatPrice(priceList[key2].trade_price); // 현재가격
                    data[key].prev_closing_price = formatPrice(priceList[key2].prev_closing_price); // 이전 종가
                    data[key].priceChangeClass = getPriceChangeClass(priceList[key2].trade_price, priceList[key2].prev_closing_price); // 가격 변동 클래스

                    if(marketKey == 'KRW-BTC'){
                        setBtcPrice(data[key].price)
                    }
                }
            }

            if (marketKey.startsWith('KRW-') && data[key].korean_name.toLowerCase().includes(search) && (market === 'all' || market === 'krw')) {
                krwList.push(data[key]);
            } else if (marketKey.startsWith('BTC-') && data[key].korean_name.toLowerCase().includes(search) && (market === 'all' || market === 'btc')) {
                btcList.push(data[key]);
            } else if (data[key].korean_name.toLowerCase().includes(search) && (market === 'all' || market === 'usdt')) {
                usdtList.push(data[key]);
            }
        }

        setFilteredData({ krwList, btcList, usdtList });
    };

    // CSS 클래스를 설정하는 함수
    const getPriceChangeClass = (price, prevClosingPrice) => {
        if (price > prevClosingPrice) {
            return 'price-up'; // 상승
        } else if (price < prevClosingPrice) {
            return 'price-down'; // 하락
        }
        return 'price-stable'; // 변동 없음
    };

    // ...이후 리턴문은 그대로 유지



    return (
        <div>
            <div className="search-and-buttons">
                <div>
                    <button onClick={() => alert('어휴 바보 ㅉㅉ')}>누르면 바보</button>
                    <button onClick={() => alert('나혼자 노는중')}>여기다 무슨기능달지</button>
                    <button onClick={() => alert('Button 3 Clicked!')}>Button 3</button>
                </div>
                <div style={{display: 'flex', alignItems: 'center'}}>
                    <input
                        type="text"
                        placeholder="Search by Korean Name..."
                        value={searchTerm}
                        onChange={handleSearch}
                    />
                    <select value={selectedMarket} onChange={handleMarketFilter}>
                        <option value="all">All Markets</option>
                        <option value="krw">KRW Market</option>
                        <option value="btc">BTC Market</option>
                        <option value="usdt">USDT Market</option>
                    </select>
                </div>
            </div>

            <div className="market-lists">
                <div className="market-list">
                    <h3>KRW Market : {filteredData.krwList.length} 항목</h3>
                    {filteredData.krwList.map((item, index) => (
                        <div key={index} className={`market-item ${item.priceChangeClass}`}>
                            <div className="market-name">{item.korean_name} ({item.english_name})</div>
                            <div className="market-code">{item.market}</div>
                            <div className="market-price">
                                {item.price ? item.price + ' 원' : 'Price not available'}
                            </div>
                        </div>
                    ))}
                </div>

                <div className="market-list">
                    <h3>BTC Market : {filteredData.btcList.length} 항목</h3>
                    {filteredData.btcList.map((item, index) => (
                        <div key={index} className={`market-item ${item.priceChangeClass}`}>
                            <div className="market-name">{item.korean_name} ({item.english_name})</div>
                            <div className="market-code">{item.market}</div>
                            <div className="market-price">
                                {item.price ? item.price + ' 원' : 'Price not available'}
                                <div style={{fontSize: '10px', color: 'midnightblue'}}>
                                    { (btcPrice * item.price).toLocaleString() }
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="market-list">
                    <h3>USDT Market : {filteredData.usdtList.length} 항목</h3>
                    {filteredData.usdtList.map((item, index) => (
                        <div key={index} className={`market-item ${item.priceChangeClass}`}>
                            <div className="market-name">{item.korean_name} ({item.english_name})</div>
                            <div className="market-code">{item.market}</div>
                            <div className="market-price">
                                {item.price ? item.price : 'Price not available'}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default UpbitMainPage;
