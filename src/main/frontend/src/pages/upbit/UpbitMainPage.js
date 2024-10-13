import React, {useEffect, useState} from 'react';
import {getUpbitList} from '../../api/Api';
import '../../layouts/css/UpbitMainPage.css';
import '../../layouts/css/PriceAnimation.css';
import SearchAndButtons from "../../components/SearchAndButtons";
import MarketList from "../../components/MarketList";

function UpbitMainPage() {
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedMarket, setSelectedMarket] = useState('all');
    const [filteredData, setFilteredData] = useState({krwList: [], btcList: [], usdtList: []});
    const [btcPrice, setBtcPrice] = useState(0);

    useEffect(() => {
        const fetchData = async () => {
            const data = await getUpbitList();
            filterAndSetData(data); // 서버에서 받은 데이터 필드에 맞춰 필터링 및 설정
        };
        fetchData();
    }, [searchTerm, selectedMarket]);

    useEffect(() => {
        const eventSource = new EventSource(`${window.location.origin}/api/sse/price`);

        const pingServer = () => {
            fetch(`${window.location.origin}/api/sse/ping`).catch(console.error);
        };
        const pingInterval = setInterval(pingServer, 10000);

        eventSource.addEventListener("price", (event) => {
            const parsedData = JSON.parse(event.data);
            if (parsedData.market === 'KRW-BTC') {
                setBtcPrice(parsedData.trade_price); // 그대로 사용
            }
            updatePrice(parsedData); // 실시간 가격 업데이트
        });

        return () => {
            eventSource.close();
            clearInterval(pingInterval);
        };
    }, []);

    const handleSearch = (e) => setSearchTerm(e.target.value.toLowerCase());

    const handleMarketFilter = (e) => setSelectedMarket(e.target.value);

    const handleSortByName = (listKey, order) => {
        setFilteredData(prevData => ({
            ...prevData,
            [listKey]: [...prevData[listKey]].sort((a, b) => {
                if (order === 'asc') {
                    return a.korean_name.localeCompare(b.korean_name);
                } else {
                    return b.korean_name.localeCompare(a.korean_name);
                }
            })
        }));
    };

    const handleSortByPrice = (listKey, order) => {
        setFilteredData(prevData => ({
            ...prevData,
            [listKey]: [...prevData[listKey]].sort((a, b) => {
                if (order === 'asc') {
                    return parseFloat(a.trade_price) - parseFloat(b.trade_price); // 문자열 그대로 사용
                } else {
                    return parseFloat(b.trade_price) - parseFloat(a.trade_price); // 문자열 그대로 사용
                }
            })
        }));
    };

    const filterAndSetData = (allData) => {
        let krwList = [], btcList = [], usdtList = [];

        // allData가 COIN과 PRICE가 아니라 리스트로 들어오는 경우 바로 처리
        allData.forEach((item) => {
            const marketKey = item.market; // 각 아이템의 market 키 사용
            const tradePrice = item.trade_price; // 현재 가격
            const prevClosingPrice = item.prev_closing_price; // 전일 종가

            item.price = tradePrice; // 가격 설정
            item.prev_closing_price = prevClosingPrice; // 전일 종가 설정
            item.priceChangeClass = getPriceChangeClass(tradePrice, prevClosingPrice); // 가격 변화 클래스 계산

            // KRW-BTC 가격이면 별도로 저장
            if (marketKey === 'KRW-BTC') {
                setBtcPrice(item.price);
            }

            // 마켓 필터링하여 각 리스트에 넣음
            if (filterMarket(item, marketKey)) {
                if (marketKey.startsWith('KRW-')) {
                    krwList.push(item);
                } else if (marketKey.startsWith('BTC-')) {
                    btcList.push(item);
                } else if (marketKey.startsWith('USDT-')) {
                    usdtList.push(item);
                }
            }
        });

        // 필터링된 데이터를 상태로 저장
        setFilteredData({krwList, btcList, usdtList});
    };


    const filterMarket = (data, marketKey) => {
        return data.korean_name.toLowerCase().includes(searchTerm) &&
            (selectedMarket === 'all' ||
                (selectedMarket === 'krw' && marketKey.startsWith('KRW-')) ||
                (selectedMarket === 'btc' && marketKey.startsWith('BTC-')) ||
                (selectedMarket === 'usdt' && marketKey.startsWith('USDT-')));
    };

    const updatePrice = (parsedData) => {
        setFilteredData(prevData => {
            const updatedKrwList = prevData.krwList.map(item => {
                if (item.market === parsedData.market) {
                    const oldPrice = Number(item.price);
                    const newPrice = Number(parsedData.trade_price);
                    const priceChangeClass = newPrice > oldPrice ? 'price-up change' : 'price-down change';

                    setTimeout(() => {
                        setFilteredData(prev => ({
                            ...prev,
                            krwList: prev.krwList.map(i => i.market === parsedData.market ? {
                                ...i,
                                priceChangeClass: priceChangeClass.replace(' change', '')
                            } : i)
                        }));
                    }, 300);
                    return {
                        ...item,
                        price: newPrice,  // 그대로 사용
                        priceChangeClass
                    };
                }
                return item;
            });
            return {...prevData, krwList: updatedKrwList};
        });
    };

    const getPriceChangeClass = (price, prevClosingPrice) => {
        if (price > prevClosingPrice) return 'price-up';
        if (price < prevClosingPrice) return 'price-down';
        return 'price-stable';
    };

    return (
        <div>
            <SearchAndButtons
                searchTerm={searchTerm}
                handleSearch={handleSearch}
                selectedMarket={selectedMarket}
                handleMarketFilter={handleMarketFilter}
            />
            <div className="market-lists">
                <MarketList
                    title="KRW Market"
                    items={filteredData.krwList}
                    btcPrice={btcPrice}
                    handleSortByName={(order) => handleSortByName('krwList', order)}
                    handleSortByPrice={(order) => handleSortByPrice('krwList', order)}
                />
                <MarketList
                    title="BTC Market"
                    items={filteredData.btcList}
                    btcPrice={btcPrice}
                    handleSortByName={(order) => handleSortByName('btcList', order)}
                    handleSortByPrice={(order) => handleSortByPrice('btcList', order)}
                />
                <MarketList
                    title="USDT Market"
                    items={filteredData.usdtList}
                    btcPrice={btcPrice}
                    handleSortByName={(order) => handleSortByName('usdtList', order)}
                    handleSortByPrice={(order) => handleSortByPrice('usdtList', order)}
                />
            </div>
        </div>
    );
}

export default UpbitMainPage;
