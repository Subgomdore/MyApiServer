import React from 'react';

function SearchAndButtons({ searchTerm, handleSearch, selectedMarket, handleMarketFilter }) {
    return (
        <div className="search-and-buttons">
            <div className="button-group">
                <button className="custom-button" onClick={() => alert('어휴 바보 ㅉㅉ')}>누르면 바보</button>
                <button className="custom-button" onClick={() => alert('나혼자 노는중')}>여기다 무슨기능달지</button>
                <button className="custom-button" onClick={() => alert('Button 3 Clicked!')}>Button 3</button>
            </div>
            <div className="search-and-filter">
                <input
                    type="text"
                    placeholder="Search by Korean Name..."
                    value={searchTerm}
                    onChange={handleSearch}
                    className="search-input"
                />
                <select value={selectedMarket} onChange={handleMarketFilter} className="market-select">
                    <option value="all">All Markets</option>
                    <option value="krw">KRW Market</option>
                    <option value="btc">BTC Market</option>
                    <option value="usdt">USDT Market</option>
                </select>
            </div>
        </div>
    );
}

export default SearchAndButtons;