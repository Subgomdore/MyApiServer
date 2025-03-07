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
    };
    return mapping[option] || { indicator: option, operator: 'ABOVE' };
};

const FilterPopup = () => {
    const [conditionType, setConditionType] = useState('AND');
    // 초기 필터: MA는 value 하나, RSI는 period와 threshold를 받도록 함
    const [filters, setFilters] = useState([
        { id: 1, option: 'aboveMovingAverage', value: 0 },
        // 예시로 RSI 필터 초기값: period 14, threshold 70 (아래는 준비중 옵션)
        // { id: 2, option: 'aboveRSI', period: 14, threshold: 70 }
    ]);
    const [results, setResults] = useState([]);
    const [selectedMarkets, setSelectedMarkets] = useState(["KRW"]);

    // 필터 적용 시, 각 필터 객체의 option 값을 내부 매핑을 통해 indicator와 operator로 분리하여 변환
    const applyFilters = async () => {
        try {
            const transformedFilters = filters.map(filter => {
                const mapped = mapFilterOption(filter.option);
                if (mapped.indicator === 'RSI') {
                    // RSI 필터인 경우 period와 threshold를 함께 전달
                    return {
                        indicator: mapped.indicator, // "RSI"
                        operator: mapped.operator,   // "ABOVE" 또는 "BELOW"
                        period: filter.period,         // RSI 계산 기간
                        value: filter.threshold        // RSI 기준값(예: 70, 30 등)
                    };
                } else {
                    // MA 등의 경우 단일 value 사용
                    return {
                        indicator: mapped.indicator, // "MA"
                        operator: mapped.operator,   // "ABOVE" 또는 "BELOW"
                        value: filter.value
                    };
                }
            });

            const requestData = {
                conditionType: conditionType,
                filters: transformedFilters,
                markets: selectedMarkets
            };

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
        // 기본적으로 MA 필터 추가. RSI 필터는 사용자가 별도로 선택할 수 있도록 함.
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

    // Upbit 거래소 링크 URL 생성 함수
    const getUpbitLink = (market) => {
        return `https://upbit.com/exchange?code=CRIX.UPBIT.${market}`;
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
                                <option value="aboveMovingAverage">이동평균(MA) 이상</option>
                                <option value="belowMovingAverage">이동평균(MA) 이하</option>
                                <option value="aboveRSI">RSI 이상</option>
                                <option value="belowRSI">RSI 이하</option>
                            </select>

                            {/* MA 필터이면 value 하나 입력, RSI 필터이면 period와 threshold 입력 */}
                            {filter.option === "aboveRSI" || filter.option === "belowRSI" ? (
                                <>
                                    <input
                                        type="number"
                                        placeholder="기간"
                                        value={filter.period || 14}
                                        onChange={(e) => handleFilterChange(filter.id, 'period', Number(e.target.value))}
                                    />
                                    <input
                                        type="number"
                                        placeholder="RSI 기준값"
                                        value={filter.threshold || 70}
                                        onChange={(e) => handleFilterChange(filter.id, 'threshold', Number(e.target.value))}
                                    />
                                </>
                            ) : (
                                <input
                                    type="number"
                                    value={filter.value}
                                    onChange={(e) => handleFilterChange(filter.id, 'value', Number(e.target.value))}
                                />
                            )}

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
                            <th>#</th>
                            <th>종목명</th>
                            <th>링크</th>
                        </tr>
                        </thead>
                        <tbody>
                        {results.length > 0 ? (
                            results.map((result, index) => (
                                <tr key={index}>
                                    <td>{index + 1}</td>
                                    <td>{result.market}</td>
                                    <td>
                                        <a href={getUpbitLink(result.market)} target="_blank" rel="noopener noreferrer">
                                            링크
                                        </a>
                                    </td>gfs
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
