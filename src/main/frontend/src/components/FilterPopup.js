import React, { useState } from 'react';
import '../css/components/FilterPopup.css';  // 스타일 파일 로드
import '../index.css';  // 글로벌 CSS 파일 로드
import { postUpbitFilterData } from '../api/Api';  // 필터 데이터를 서버로 보내는 API 함수

const FilterPopup = () => {
    // 필터 상태 관리
    const [filterData, setFilterData] = useState({ priceRange: 0, volume: 0 });
    const [results, setResults] = useState([]); // 서버에서 반환된 결과 저장

    // 필터 적용 버튼 클릭 시 호출되는 함수
    const applyFilters = async () => {
        try {
            // 서버로 필터 데이터 전송 및 응답 저장
            const response = await postUpbitFilterData(filterData);
            setResults(response); // 결과 상태 업데이트
        } catch (error) {
            console.error('Error applying filters:', error);
        }
    };

    // 필터 초기화 함수
    const resetFilters = () => {
        setFilterData({ priceRange: 0, volume: 0 }); // 필터 초기화
        setResults([]); // 결과 초기화
    };

    // 가격 범위 변경 핸들러
    const handlePriceRangeChange = (value) => {
        setFilterData((prevState) => ({
            ...prevState,
            priceRange: value,
        }));
    };

    // 거래량 변경 핸들러
    const handleVolumeChange = (value) => {
        setFilterData((prevState) => ({
            ...prevState,
            volume: value,
        }));
    };

    return (
        <div className="filter-popup-container">
            <div className="filter-popup-info">
                <h4>필터 설정</h4>
                <h4>ex : 112 를 입력하면 112일평균값 이상의 데이터 조회</h4>
                {/* 가격 범위 설정 */}
                <div className="filter-group">
                    <label>이동평균 조건값: </label>
                    <input
                        type="range"
                        min="0"
                        max="500"
                        value={filterData.priceRange}
                        onChange={(e) => handlePriceRangeChange(Number(e.target.value))}
                    />
                    <input
                        type="number"
                        min="0"
                        max="500"
                        value={filterData.priceRange}
                        onChange={(e) => handlePriceRangeChange(Number(e.target.value))}
                    />
                </div>

                {/* 거래량 설정 */}
                <div className="filter-group">
                    <label>거래량:</label>
                    <input
                        type="number"
                        min="0"
                        value={filterData.volume}
                        onChange={(e) => handleVolumeChange(Number(e.target.value))}
                    />
                </div>

                {/* 필터 버튼 */}
                <div className="button-group">
                    <button className="apply-button" onClick={applyFilters}>
                        필터 적용
                    </button>
                    <button className="reset-button" onClick={resetFilters}>
                        필터 초기화
                    </button>
                </div>
            </div>

            {/* 결과 테이블 */}
            <div className="filter-popup-result-table">
                <table>
                    <thead>
                    <tr>
                        <th>종목명</th>
                        <th>현재가</th>
                        <th>거래량</th>
                    </tr>
                    </thead>
                    <tbody>
                    {results.length > 0 ? (
                        results.map((result, index) => (
                            <tr key={index}>
                                <td>{result.market}</td>
                                <td>{result.currentPrice}</td>
                                <td>{result.percentageChange}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="3">데이터가 없습니다</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default FilterPopup;
