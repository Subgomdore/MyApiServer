import React, { useEffect, useState } from 'react';
import '../../css/pages/upbit/UpbitMainPage.css';
import '../../css/pages/upbit/PriceAnimation.css';
import SearchAndButtons from "./SearchAndButtons";
import MarketList from "../../components/MarketList";
import { getPriceList } from '../../api/Api';

function UpbitMainPage() {
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedMarket, setSelectedMarket] = useState('all');
    const [rawData, setRawData] = useState({ krwList: [], btcList: [], usdtList: [] });
    const [btcPrice, setBtcPrice] = useState(0);
    const [marketInfo, setMarketInfo] = useState({});

    // 새로운 formatPrice 함수: 천 단위는 ko-KR 로케일, 1e-8 미만은 고정 소수점 10자리
    const formatPrice = (value) => {
        const price = Number(value);
        if (isNaN(price) || price === 0) return '0';
        if (price >= 1000) {
            // 예: 1234567 → "1,234,567"
            return new Intl.NumberFormat('ko-KR', { maximumFractionDigits: 0 }).format(price);
        } else if (price >= 100) {
            return price.toFixed(1);
        } else if (price >= 1) {
            return price.toFixed(3);
        } else if (price >= 0.01) {
            return price.toFixed(4);
        } else if (price >= 0.0001) {
            return price.toFixed(6);
        } else if (price >= 1e-8) {
            return price.toFixed(8);
        } else {
            // 1e-8 미만인 경우: 고정 소수점 10자리 출력 (지수표기 방지)
            return price.toFixed(10);
        }
    };

    useEffect(() => {
        const socket = new WebSocket('wss://api.upbit.com/websocket/v1');

        socket.onopen = async () => {
            console.log('WebSocket 연결 성공');

            try {
                const data = await getPriceList();
                const filteredMarkets = data.filter(item =>
                    item.market.startsWith("KRW-") ||
                    item.market.startsWith("BTC-") ||
                    item.market.startsWith("USDT-")
                );

                const infoMapping = {};
                filteredMarkets.forEach(item => {
                    infoMapping[item.market] = item;
                });
                setMarketInfo(infoMapping);

                const initialRawData = { krwList: [], btcList: [], usdtList: [] };
                filteredMarkets.forEach(item => {
                    const initialItem = {
                        ...item,
                        price: item.opening_price,
                        trade_price: item.opening_price,
                        initialPrice: item.opening_price,
                        formattedPrice: formatPrice(item.opening_price),
                        priceChangeClass: 'price-stable',
                        korean_name: item.korean_name || item.market
                    };
                    if (item.market.startsWith("KRW-")) {
                        initialRawData.krwList.push(initialItem);
                    } else if (item.market.startsWith("BTC-")) {
                        initialRawData.btcList.push(initialItem);
                    } else if (item.market.startsWith("USDT-")) {
                        initialRawData.usdtList.push(initialItem);
                    }
                });
                setRawData(initialRawData);

                const codes = filteredMarkets.map(item => item.market);
                const subscriptionMessage = [
                    { ticket: "test" },
                    { type: "ticker", codes: codes }
                ];
                socket.send(JSON.stringify(subscriptionMessage));
            } catch (error) {
                console.error('업비트 가격 리스트 조회 실패:', error);
            }
        };

        socket.onmessage = (event) => {
            if (event.data instanceof Blob) {
                event.data.text().then((text) => {
                    try {
                        const parsedData = JSON.parse(text);
                        processTickerData(parsedData);
                    } catch (error) {
                        console.error('WebSocket 메시지 처리 에러:', error);
                    }
                }).catch((error) => {
                    console.error('Blob 변환 에러:', error);
                });
            } else {
                try {
                    const parsedData = JSON.parse(event.data);
                    processTickerData(parsedData);
                } catch (error) {
                    console.error('WebSocket 메시지 처리 에러:', error);
                }
            }
        };

        socket.onerror = (error) => {
            console.error('WebSocket 에러:', error);
        };

        socket.onclose = () => {
            console.log('WebSocket 연결 종료');
        };

        return () => {
            socket.close();
        };
    }, []);

    const processTickerData = (parsedData) => {
        if (Array.isArray(parsedData)) {
            parsedData.forEach(data => {
                const market = data.code || data.market;
                if (!market) return;
                if (market === 'KRW-BTC') {
                    setBtcPrice(data.trade_price);
                }
                updatePrice({ ...data, market });
            });
        } else {
            const market = parsedData.code || parsedData.market;
            if (!market) return;
            if (market === 'KRW-BTC') {
                setBtcPrice(parsedData.trade_price);
            }
            updatePrice({ ...parsedData, market });
        }
    };

    const updatePrice = (parsedData) => {
        const market = parsedData.market;
        if (!market) return;

        setRawData(prevData => {
            let listKey = '';
            if (market.startsWith('KRW-')) listKey = 'krwList';
            else if (market.startsWith('BTC-')) listKey = 'btcList';
            else if (market.startsWith('USDT-')) listKey = 'usdtList';
            else return prevData;

            const list = prevData[listKey];
            const idx = list.findIndex(item => item.market === market);

            if (idx === -1) {
                const newItem = {
                    ...parsedData,
                    price: parsedData.trade_price,
                    trade_price: parsedData.trade_price,
                    initialPrice: parsedData.opening_price || marketInfo[market]?.opening_price || parsedData.trade_price,
                    formattedPrice: formatPrice(parsedData.trade_price),
                    priceChangeClass: 'price-stable',
                    korean_name: marketInfo[market]?.korean_name || market
                };
                return { ...prevData, [listKey]: [...list, newItem] };
            } else {
                const oldItem = list[idx];
                const basePrice = oldItem.initialPrice;
                const newPrice = Number(parsedData.trade_price);
                let priceChangeClass = 'price-stable';
                if (newPrice > basePrice) {
                    priceChangeClass = 'price-up change';
                } else if (newPrice < basePrice) {
                    priceChangeClass = 'price-down change';
                }

                const updatedItem = {
                    ...oldItem,
                    price: newPrice,
                    trade_price: newPrice,
                    formattedPrice: formatPrice(newPrice),
                    priceChangeClass
                };

                const updatedList = [...list];
                updatedList[idx] = updatedItem;

                setTimeout(() => {
                    setRawData(prev => {
                        const subList = prev[listKey].map(i => {
                            if (i.market === market) {
                                return { ...i, priceChangeClass: i.priceChangeClass.replace(' change', '') };
                            }
                            return i;
                        });
                        return { ...prev, [listKey]: subList };
                    });
                }, 300);

                return { ...prevData, [listKey]: updatedList };
            }
        });
    };

    const filteredData = {
        krwList: rawData.krwList.filter(item =>
            (!searchTerm || item.korean_name.toLowerCase().includes(searchTerm)) &&
            (selectedMarket === 'all' || (selectedMarket === 'krw' && item.market.startsWith('KRW-')))
        ),
        btcList: rawData.btcList.filter(item =>
            (!searchTerm || item.korean_name.toLowerCase().includes(searchTerm)) &&
            (selectedMarket === 'all' || (selectedMarket === 'btc' && item.market.startsWith('BTC-')))
        ),
        usdtList: rawData.usdtList.filter(item =>
            (!searchTerm || item.korean_name.toLowerCase().includes(searchTerm)) &&
            (selectedMarket === 'all' || (selectedMarket === 'usdt' && item.market.startsWith('USDT-')))
        )
    };

    const sortedData = {
        krwList: [...filteredData.krwList].sort((a, b) => b.trade_price - a.trade_price),
        btcList: [...filteredData.btcList].sort((a, b) => b.trade_price - a.trade_price),
        usdtList: [...filteredData.usdtList].sort((a, b) => b.trade_price - a.trade_price)
    };

    const handleSearch = (e) => setSearchTerm(e.target.value.toLowerCase());
    const handleMarketFilter = (e) => setSelectedMarket(e.target.value);

    return (
        <div>
            <SearchAndButtons
                searchTerm={searchTerm}
                handleSearch={handleSearch}
                selectedMarket={selectedMarket}
                handleMarketFilter={handleMarketFilter}
            />
            <div className="market-lists">
                <MarketList title="KRW Market" items={sortedData.krwList} btcPrice={btcPrice} />
                <MarketList title="BTC Market" items={sortedData.btcList} btcPrice={btcPrice} />
                <MarketList title="USDT Market" items={sortedData.usdtList} btcPrice={btcPrice} />
            </div>
        </div>
    );
}

export default UpbitMainPage;
