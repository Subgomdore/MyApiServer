import React, { useState } from 'react';

function MarketList({ title, items, btcPrice, handleSortByName, handleSortByPrice }) {
    const [nameOrder, setNameOrder] = useState('asc');
    const [codeOrder, setCodeOrder] = useState('asc');
    const [priceOrder, setPriceOrder] = useState('asc');

    const toggleSortByName = () => {
        const newOrder = nameOrder === 'asc' ? 'desc' : 'asc';
        setNameOrder(newOrder);
        handleSortByName(newOrder);
    };

    const toggleSortByCode = () => {
        const newOrder = codeOrder === 'asc' ? 'desc' : 'asc';
        setCodeOrder(newOrder);
        handleSortByName(newOrder); // Assuming handleSortByName for code as well, replace if needed
    };

    const toggleSortByPrice = () => {
        const newOrder = priceOrder === 'asc' ? 'desc' : 'asc';
        setPriceOrder(newOrder);
        handleSortByPrice(newOrder);
    };

    return (
        <div className="market-list">
            <div className="market-list-header">
                <h3>{title} : {items.length} 항목</h3>
            </div>
            <div className="market-items-container">
                <div className="market-item"
                     style={{display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '10px', alignItems: 'center'}}>
                    <div className="market-name" style={{borderRight: '1px solid #ccc', textAlign: 'center'}}>
                        <button className={`sort-button ${nameOrder}`} onClick={toggleSortByName}>
                            이름 정렬 ({nameOrder === 'asc' ? '▲' : '▼'})
                        </button>
                    </div>
                    <div className="market-code" style={{borderRight: '1px solid #ccc', textAlign: 'center'}}>
                        <button className={`sort-button ${codeOrder}`} onClick={toggleSortByCode}>
                            코인 정렬 ({codeOrder === 'asc' ? '▲' : '▼'})
                        </button>
                    </div>
                    <div className="market-price" style={{textAlign: 'center'}}>
                        <button className={`sort-button ${priceOrder}`} onClick={toggleSortByPrice}>
                            가격 정렬 ({priceOrder === 'asc' ? '▲' : '▼'})
                        </button>
                    </div>
                </div>

                {items.map((item, index) => (
                    <div key={index} className={`market-item ${item.priceChangeClass}`} style={{
                        display: 'grid',
                        gridTemplateColumns: '2fr 1fr 1fr',
                        gap: '10px',
                        alignItems: 'center'
                    }}>
                        <div className="market-name" style={{
                            borderRight: '1px solid #ccc',
                            textAlign: 'center'
                        }}>{item.korean_name || 'Unknown Name'} <br/>({item.english_name || 'Unknown Name'})
                        </div>
                        <div className="market-code"
                             style={{borderRight: '1px solid #ccc', textAlign: 'center'}}>{item.market || 'N/A'}</div>
                        <div className="market-price" style={{textAlign: 'center'}}>
                            {item.price ? item.price + (title === 'KRW Market' ? ' 원' : '') : 'Price not available'}
                            {title === 'BTC Market' && item.price && btcPrice && (
                                <div style={{fontSize: '10px'}}>
                                    {(btcPrice * parseFloat(item.price)).toLocaleString() + ' 원'}
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
