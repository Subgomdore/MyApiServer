import React, { useState } from 'react';
import '../css/components/FilterPopup.css';
import '../index.css';
import { postUpbitFilterData } from '../api/Api';

const FilterPopup = () => {
    const [conditionType, setConditionType] = useState('AND');
    const [filters, setFilters] = useState([{ id: 1, type: 'priceRange', value: 0 }]);
    const [results, setResults] = useState([]);
    const [selectedMarkets, setSelectedMarkets] = useState(["KRW", "BTC", "USDT"]); // ✅ 기본적으로 전체 선택

    // 필터 적용
    const applyFilters = async () => {
        try {
            const requestData = {
                conditionType: conditionType,
                filters: filters,
                markets: selectedMarkets // ✅ 선택된 마켓 정보 추가
            };

            const response = await postUpbitFilterData(requestData);
            setResults(response);
        } catch (error) {
            console.error('Error applying filters:', error);
        }
    };

    // 필터 초기화
    const resetFilters = () => {
        setFilters([{ id: 1, type: 'priceRange', value: 0 }]);
        setConditionType('AND');
        setResults([]);
        setSelectedMarkets(["KRW", "BTC", "USDT"]); // ✅ 기본값 리셋
    };

    const getNextId = () => {
        return filters.length > 0 ? Math.max(...filters.map(f => f.id)) + 1 : 1;
    };

    // 조건 추가
    const addFilter = () => {
        const newId = getNextId();
        setFilters([...filters, { id: newId, type: 'priceRange', value: 0 }]);
    };

    // 조건 삭제
    const removeFilter = (id) => {
        setFilters(filters.filter(filter => filter.id !== id));
    };

    // 필터 값 변경
    const handleFilterChange = (id, key, value) => {
        setFilters(filters.map(filter => filter.id === id ? { ...filter, [key]: value } : filter));
    };

    // 체크박스 변경 핸들러 (AND / OR 선택)
    const handleConditionChange = (type) => {
        setConditionType(type);
    };

    // ✅ 마켓 필터 선택 핸들러
    const handleMarketChange = (market) => {
        setSelectedMarkets((prevMarkets) =>
            prevMarkets.includes(market)
                ? prevMarkets.filter((m) => m !== market) // 체크 해제 시 제거
                : [...prevMarkets, market] // 체크 시 추가
        );
    };

    return (
        <div className="filter-popup-container">
            {/* 좌측 필터 조건 설정 */}
            <div className="filter-popup-sidebar">
                <h4>필터 설정</h4>

                {/* AND / OR 체크박스 */}
                <div className="condition-checkbox">
                    <label>
                        <input
                            type="checkbox"
                            checked={conditionType === 'AND'}
                            onChange={() => handleConditionChange('AND')}
                        />
                        AND (모든 조건 충족)
                    </label>
                    <label>
                        <input
                            type="checkbox"
                            checked={conditionType === 'OR'}
                            onChange={() => handleConditionChange('OR')}
                        />
                        OR (하나라도 충족)
                    </label>
                </div>

                {/* ✅ 마켓 선택 체크박스 (KRW, BTC, USDT) */}
                <div className="market-checkbox-group">
                    <label>
                        <input
                            type="checkbox"
                            checked={selectedMarkets.includes("KRW")}
                            onChange={() => handleMarketChange("KRW")}
                        />
                        KRW
                    </label>
                    <label>
                        <input
                            type="checkbox"
                            checked={selectedMarkets.includes("BTC")}
                            onChange={() => handleMarketChange("BTC")}
                        />
                        BTC
                    </label>
                    <label>
                        <input
                            type="checkbox"
                            checked={selectedMarkets.includes("USDT")}
                            onChange={() => handleMarketChange("USDT")}
                        />
                        USDT
                    </label>
                </div>

                {/* 동적 필터 추가 UI */}
                <div className="filter-list">
                    {filters.map((filter) => (
                        <div key={filter.id} className="filter-group">
                            <select value={filter.type} onChange={(e) => handleFilterChange(filter.id, 'type', e.target.value)}>
                                <option value="priceRange">이동평균 조건</option>
                                <option value="volume">거래량</option>
                                <option value="changeRate">변동률</option>
                            </select>

                            <input
                                type="number"
                                value={filter.value}
                                onChange={(e) => handleFilterChange(filter.id, 'value', Number(e.target.value))}
                            />

                            {filters.length > 1 && (
                                <button className="remove-button" onClick={() => removeFilter(filter.id)}>X</button>
                            )}
                        </div>
                    ))}
                </div>

                {/* 조건 추가 버튼 */}
                <button className="add-button" onClick={addFilter}>+ 조건 추가</button>

                {/* 필터 적용 및 초기화 버튼 */}
                <div className="button-group">
                    <button className="apply-button" onClick={applyFilters}>검색</button>
                    <button className="reset-button" onClick={resetFilters}>초기화</button>
                </div>
            </div>

            {/* ✅ 오른쪽 결과 테이블 (스크롤 적용) */}
            <div className="filter-popup-content">
                <div className="filter-popup-result-container">
                    <table className="filter-popup-result-table">
                        <thead>
                        <tr>
                            <th>종목명</th>
                            <th>현재가</th>
                            <th>변동률</th>
                        </tr>
                        </thead>
                        <tbody>
                        {results.length > 0 ? (
                            results.map((result, index) => (
                                <tr key={index}>
                                    <td>{result.market}</td>
                                    <td>{parseFloat(result.currentPrice).toFixed(8)}</td>
                                    <td>{parseFloat(result.percentageChange).toFixed(2)}%</td>
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
        </div>
    );
};

export default FilterPopup;
