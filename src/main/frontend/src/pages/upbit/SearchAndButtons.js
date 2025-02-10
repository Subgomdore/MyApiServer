import React, { useEffect, useState } from 'react';
import '../../css/components/SearchAndButtons.css';

function SearchAndButtons({ searchTerm, handleSearch, selectedMarket, handleMarketFilter, onFilterApply, fetchData }) {
    const [isPopupOpen, setIsPopupOpen] = useState(false);

    // 팝업 열기 함수
    const openPopup = () => {
        const popup = window.open(
            `http://localhost:3000/filter-popup`, // 3000번 포트를 명시적으로 지정
            "FilterPopup", // 팝업 이름
            "width=800,height=600,left=300,top=200" // 팝업 창 크기 및 위치 설정
        );

        if (popup) {
            setIsPopupOpen(true); // 팝업이 성공적으로 열렸는지 확인
        } else {
            alert("팝업 차단이 활성화되어 있습니다. 팝업 차단을 해제해주세요."); // 팝업 차단 알림
        }
    };



    // 팝업에서 필터 데이터를 받는 이벤트 핸들러
    useEffect(() => {
        const handleMessage = (event) => {
            if (event.origin !== window.location.origin) return;  // 안전한 출처 확인

            // 필터 데이터를 수신했을 때
            if (event.data.priceRange || event.data.volume) {
                onFilterApply(event.data);  // 부모 컴포넌트로 필터 데이터를 전달
            }
        };

        window.addEventListener('message', handleMessage);

        // 컴포넌트 언마운트 시 이벤트 제거
        return () => {
            window.removeEventListener('message', handleMessage);
        };
    }, [onFilterApply]);

    return (
        <div className="search-and-buttons">
            <div className="button-group">
                {/* 팝업 열기 버튼 */}
                <button className="custom-button" onClick={openPopup}> 검색필터 추가</button>

                {/* 기본 데이터 가져오기 버튼 */}
                <button className="custom-button" onClick={fetchData}> 필터 초기화(예정) </button>

            </div>

            {/* 검색창 및 필터 */}
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
