import React, { useState } from 'react';

function MarketList({ title, items, btcPrice, handleSortByName, handleSortByPrice }) {
    const [nameOrder, setNameOrder] = useState('asc');
    const [codeOrder, setCodeOrder] = useState('asc');
    const [priceOrder, setPriceOrder] = useState('asc');

    const toggleSortByName = () => {
        const newOrder = nameOrder === 'asc' ? 'desc' : 'asc';
        setNameOrder(newOrder);
        handleSortByName && handleSortByName(newOrder);
    };

    const toggleSortByCode = () => {
        const newOrder = codeOrder === 'asc' ? 'desc' : 'asc';
        setCodeOrder(newOrder);
        // 만약 코인 코드 정렬 핸들러가 있다면 여기에 호출
        handleSortByName && handleSortByName(newOrder);
    };

    const toggleSortByPrice = () => {
        const newOrder = priceOrder === 'asc' ? 'desc' : 'asc';
        setPriceOrder(newOrder);
        handleSortByPrice && handleSortByPrice(newOrder);
    };

    // 원화 형식으로 가격을 포맷팅하는 헬퍼 함수
    const formatKRW = (value) => {
        const price = Number(value);
        if (!price) return '0';
        if (price >= 1000) {
            // 한글 로케일(ko-KR)을 사용해 천 단위 구분 기호를 적용
            return new Intl.NumberFormat('ko-KR', { maximumFractionDigits: 0 }).format(price);
        } else if (price >= 100) {
            return price.toFixed(1);
        } else if (price >= 1) {
            return price.toFixed(3);
        } else {
            return price.toFixed(3);
        }
    };

    return (
        <div className="market-list">
            <div className="market-list-header">
                <h3>{title} : {items.length} 항목</h3>
            </div>
            <div className="market-items-container">
                <div className="market-item"
                     style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '10px', alignItems: 'center' }}>
                    <div className="market-name" style={{ borderRight: '1px solid #ccc', textAlign: 'center' }}>
                        <button className={`sort-button ${nameOrder}`} onClick={toggleSortByName}>
                            이름 정렬 ({nameOrder === 'asc' ? '▲' : '▼'})
                        </button>
                    </div>
                    <div className="market-code" style={{ borderRight: '1px solid #ccc', textAlign: 'center' }}>
                        <button className={`sort-button ${codeOrder}`} onClick={toggleSortByCode}>
                            코인 정렬 ({codeOrder === 'asc' ? '▲' : '▼'})
                        </button>
                    </div>
                    <div className="market-price" style={{ textAlign: 'center' }}>
                        <button className={`sort-button ${priceOrder}`} onClick={toggleSortByPrice}>
                            가격 정렬 ({priceOrder === 'asc' ? '▲' : '▼'})
                        </button>
                    </div>
                </div>

                {items.map((item, index) => (
                    <div key={index} className={`market-item ${item.priceChangeClass}`}
                         style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '10px', alignItems: 'center' }}>
                        <div className="market-name" style={{ borderRight: '1px solid #ccc', textAlign: 'center' }}>
                            {item.korean_name || 'Unknown Name'} <br />({item.english_name || 'Unknown Name'})
                        </div>
                        <div className="market-code" style={{ borderRight: '1px solid #ccc', textAlign: 'center' }}>
                            {item.market || 'N/A'}
                        </div>
                        <div className="market-price" style={{ textAlign: 'center' }}>
                            {item.formattedPrice ? item.formattedPrice + (title === 'KRW Market' ? ' 원' : '') : 'Price not available'}
                            {title === 'BTC Market' && item.trade_price && btcPrice && (
                                <div style={{ fontSize: '10px', marginTop: '4px' }}>
                                    {formatKRW(btcPrice * parseFloat(item.trade_price))} 원
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default MarketList;
