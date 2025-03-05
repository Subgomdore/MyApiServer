import React, { useState } from 'react';
import '../css/components/FilterPopup.css';
import '../index.css';
import { postUpbitFilterData } from '../api/Api';

// 옵션에 대한 매핑: 각 select option의 값에 따라 indicator와 operator를 분리합니다.
const mapFilterOption = (option) => {
    const mapping = {
        aboveMovingAverage: { indicator: 'MA', operator: 'ABOVE' },
        belowMovingAverage: { indicator: 'MA', operator: 'BELOW' },
        aboveRSI: { indicator: 'RSI', operator: 'ABOVE' },
        belowRSI: { indicator: 'RSI', operator: 'BELOW' },
        changeRate: { indicator: 'CR', operator: 'ABOVE' } // 변동률은 조건이 하나인 경우; 필요시 별도 옵션 추가
    };
    return mapping[option] || { indicator: option, operator: 'ABOVE' };
};

const FilterPopup = () => {
    const [conditionType, setConditionType] = useState('AND');
    // 초기 필터: id는 내부 numeric id, option은 UI에서 선택하는 값, value는 조건 값
    const [filters, setFilters] = useState([{ id: 1, option: 'aboveMovingAverage', value: 0 }]);
    const [results, setResults] = useState([]);
    const [selectedMarkets, setSelectedMarkets] = useState(["KRW"]);

    // 필터 적용 시, 각 필터 객체의 option 값을 내부 매핑을 통해 indicator와 operator로 분리하여 변환
    const applyFilters = async () => {
        try {
            const transformedFilters = filters.map(filter => {
                const mapped = mapFilterOption(filter.option);
                return {
                    indicator: mapped.indicator,   // 예: "MA" 또는 "RSI"
                    operator: mapped.operator,     // 예: "ABOVE" 또는 "BELOW"
                    value: filter.value
                };
            });

            const requestData = {
                conditionType: conditionType,
                filters: transformedFilters,
                markets: selectedMarkets
            };

            console.log(requestData);
            const response = await postUpbitFilterData(requestData);
            setResults(response);
        } catch (error) {
            console.error('Error applying filters:', error);
        }
    };

    // 필터 초기화
    const resetFilters = () => {
        setFilters([{ id: 1, option: 'aboveMovingAverage', value: 0 }]);
        setConditionType('AND');
        setResults([]);
        setSelectedMarkets(["KRW"]);
    };

    const getNextId = () => {
        return filters.length > 0 ? Math.max(...filters.map(f => f.id)) + 1 : 1;
    };

    // 조건 추가
    const addFilter = () => {
        const newId = getNextId();
        setFilters([...filters, { id: newId, option: 'aboveMovingAverage', value: 0 }]);
    };

    // 조건 삭제 (ID 기준)
    const removeFilter = (id) => {
        setFilters(filters.filter(filter => filter.id !== id));
    };

    // 필터 값 변경 (ID 기준)
    const handleFilterChange = (id, key, value) => {
        setFilters(filters.map(filter => filter.id === id ? { ...filter, [key]: value } : filter));
    };

    // AND / OR 체크박스 핸들러
    const handleConditionChange = (type) => {
        setConditionType(type);
    };

    // 마켓 선택 핸들러
    const handleMarketChange = (market) => {
        setSelectedMarkets((prevMarkets) =>
            prevMarkets.includes(market)
                ? prevMarkets.filter((m) => m !== market)
                : [...prevMarkets, market]
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

                {/* 마켓 선택 체크박스 (KRW, BTC, USDT) */}
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
                            <select
                                value={filter.option}
                                onChange={(e) => handleFilterChange(filter.id, 'option', e.target.value)}
                            >
                                <option value="aboveMovingAverage">이동평균 조건이상</option>
                                <option value="belowMovingAverage">이동평균 조건이하</option>
                                <option value="aboveRSI">RSI 조건이상</option>
                                <option value="belowRSI">RSI 조건이하</option>
                                <option value="movingAverage">변동률</option>
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

            {/* 오른쪽 결과 테이블 (스크롤 적용) */}
            <div className="filter-popup-content">
                <div className="filter-popup-result-container">
                    <table className="filter-popup-result-table">
                        <thead>
                        <tr>
                            <th>종목명</th>
                            {/*<th>현재가</th>*/}
                            {/*<th>평균값</th>*/}
                            {/*<th>변동률</th>*/}
                        </tr>
                        </thead>
                        <tbody>
                        {results.length > 0 ? (
                            results.map((result, index) => (
                                <tr key={index}>
                                    <td>{result.market}</td>
                                    {/*<td>{parseFloat(result.currentPrice)}</td>*/}
                                    {/*<td>{parseFloat(result.movingAverage).toFixed(1)}</td>*/}
                                    {/*<td>{parseFloat(result.percentageChange).toFixed(1)}</td>*/}
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
