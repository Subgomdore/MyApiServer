import React, {useState} from 'react';
import '../css/components/SearchAndButtons.css'; // CommonLayout 스타일 정의

function SearchAndButtons({searchTerm, handleSearch, selectedMarket, handleMarketFilter}) {
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 추가

    // 모달 열기 함수
    const openModal = () => setIsModalOpen(true);

    // 모달 닫기 함수
    const closeModal = () => setIsModalOpen(false);

    return (
        <div className="search-and-buttons">
            <div className="button-group">
                <button className="custom-button" onClick={openModal}> 검색필터 추가</button>
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

            {/* 모달 */}
            {isModalOpen && (
                <div className="modal">
                    <div className="modal-content">
                        <span className="close-button" onClick={closeModal}>&times;</span>
                        <p> 이제 여기다 검색기 달아야지</p>
                    </div>
                </div>
            )}
        </div>
    );
}

export default SearchAndButtons;
