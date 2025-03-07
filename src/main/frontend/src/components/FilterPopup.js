import React, { useState } from 'react';
import '../css/components/FilterPopup.css';  // 기존 CSS
import '../index.css';
import { postUpbitFilterData } from '../api/Api';

// UI에 표시할 TimeUnit (한글) → Backend 전송 시 매핑
const timeUnits = ["1초", "1분", "3분", "5분", "10분", "15분", "30분", "1시간", "4시간", "일", "주", "달", "12달"];
const timeUnitMapping = {
    "1초": "1s",
    "1분": "1m",
    "3분": "3m",
    "5분": "5m",
    "10분": "10m",
    "15분": "15m",
    "30분": "30m",
    "1시간": "1h",
    "4시간": "4h",
    "일": "1d",
    "주": "1w",
    "달": "1M",
    "12달": "12M",
};

// 왼쪽/오른쪽 표현식에서 사용할 지표/타입 목록
const expressionTypes = [
    { value: "MA", label: "이동평균(MA)" },
    { value: "RSI", label: "RSI" },
    { value: "Price", label: "가격" },
    { value: "Number", label: "직접입력" }, // 숫자를 직접 입력
];

// 연산자 목록
const operators = [">", "<", ">=", "<="];

const FilterPopup = () => {
    // AND / OR
    const [conditionType, setConditionType] = useState('AND');

    // TimeUnit
    const [selectedTimeUnit, setSelectedTimeUnit] = useState("일");

    // 검색 결과
    const [results, setResults] = useState([]);

    // 마켓 체크박스
    const [selectedMarkets, setSelectedMarkets] = useState(["KRW"]);

    // 고급연산 필터 목록 (한 줄에 leftType, leftPeriod, operator, rightType, rightPeriod)
    // 예: { id: 1, leftType: 'MA', leftPeriod: 448, operator: '>', rightType: 'MA', rightPeriod: 224 }
    const [filters, setFilters] = useState([
        {
            id: 1,
            leftType: 'Price',      // 가격
            leftPeriod: '',         // 가격에는 period 불필요, MA/RSI만 사용
            operator: '>',
            rightType: 'MA',        // 이동평균(MA)
            rightPeriod: 224,       // 224
        },
    ]);

    // 마켓 체크
    const handleMarketChange = (market) => {
        setSelectedMarkets(prev =>
            prev.includes(market)
                ? prev.filter(m => m !== market)
                : [...prev, market]
        );
    };

    // AND / OR
    const handleConditionChange = (type) => {
        setConditionType(type);
    };

    // 필터 추가
    const getNextId = () => (filters.length ? Math.max(...filters.map(f => f.id)) + 1 : 1);
    const addFilter = () => {
        const newId = getNextId();
        // 기본 값: MA(14) > Price or something
        const newFilter = {
            id: newId,
            leftType: 'MA',
            leftPeriod: 448,
            operator: '>',
            rightType: 'MA',
            rightPeriod: 224,
        };
        setFilters([...filters, newFilter]);
    };

    // 필터 삭제
    const removeFilter = (id) => {
        setFilters(filters.filter(f => f.id !== id));
    };

    // 필터 변경
    const handleFilterChange = (id, key, value) => {
        setFilters(filters.map(f => f.id === id ? { ...f, [key]: value } : f));
    };

    // 초기화
    const resetFilters = () => {
        setConditionType('AND');
        setSelectedMarkets(["KRW"]);
        setSelectedTimeUnit("일");
        setResults([]);
        setFilters([
            {
                id: 1,
                leftType: 'Price',
                leftPeriod: '',
                operator: '>',
                rightType: 'MA',
                rightPeriod: 224,
            },
        ]);
    };

    // 검색
    const applyFilters = async () => {
        try {
            // 변환: 백엔드가 해석할 수 있는 구조로
            // 예: { left: {type: 'MA', period: 448}, operator: '>', right: {type: 'MA', period: 224} }
            const transformed = filters.map(f => ({
                left: {
                    type: f.leftType,
                    period: f.leftType === 'MA' || f.leftType === 'RSI' ? Number(f.leftPeriod) : null,
                },
                operator: f.operator,
                right: {
                    type: f.rightType,
                    period: f.rightType === 'MA' || f.rightType === 'RSI' ? Number(f.rightPeriod) : null,
                    // 만약 rightType === 'Number'이면 period 대신 numericValue를 사용
                    numericValue: f.rightType === 'Number' ? Number(f.rightPeriod) : null,
                },
            }));

            const requestData = {
                conditionType,
                filters: transformed,
                markets: selectedMarkets,
                timeUnit: timeUnitMapping[selectedTimeUnit],
            };

            console.log("requestData:", requestData);

            // 서버 호출
            const response = await postUpbitFilterData(requestData);
            setResults(response);
        } catch (error) {
            console.error('Error applying filters:', error);
        }
    };

    // Upbit 링크
    const getUpbitLink = (market) => {
        return `https://upbit.com/exchange?code=CRIX.UPBIT.${market}`;
    };

    return (
        <div className="filter-popup-container">
            <div className="filter-popup-sidebar">
                <h4>필터 설정</h4>

                {/* AND / OR */}
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

                {/* 마켓 체크박스 */}
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

                {/* TimeUnit 버튼 */}
                <div className="time-unit-selector">
                    {timeUnits.map(unit => (
                        <button
                            key={unit}
                            className={`time-unit-button ${selectedTimeUnit === unit ? 'active' : ''}`}
                            onClick={() => setSelectedTimeUnit(unit)}
                        >
                            {unit}
                        </button>
                    ))}
                </div>

                {/* 고급연산 필터 목록 */}
                <div className="filter-list">
                    {filters.map(f => (
                        <div key={f.id} className="filter-group">
                            {/* 왼쪽 SelectBox */}
                            <select
                                value={f.leftType}
                                onChange={e => handleFilterChange(f.id, 'leftType', e.target.value)}
                            >
                                {expressionTypes.map(et => (
                                    <option key={et.value} value={et.value}>{et.label}</option>
                                ))}
                            </select>
                            {/* 왼쪽 period (MA, RSI일 때만 노출) */}
                            {(f.leftType === 'MA' || f.leftType === 'RSI') && (
                                <input
                                    type="number"
                                    placeholder="period"
                                    value={f.leftPeriod}
                                    onChange={e => handleFilterChange(f.id, 'leftPeriod', e.target.value)}
                                    style={{ width: '60px' }}
                                />
                            )}

                            {/* 연산자 */}
                            <select
                                value={f.operator}
                                onChange={e => handleFilterChange(f.id, 'operator', e.target.value)}
                            >
                                {operators.map(op => (
                                    <option key={op} value={op}>{op}</option>
                                ))}
                            </select>

                            {/* 오른쪽 SelectBox */}
                            <select
                                value={f.rightType}
                                onChange={e => handleFilterChange(f.id, 'rightType', e.target.value)}
                            >
                                {expressionTypes.map(et => (
                                    <option key={et.value} value={et.value}>{et.label}</option>
                                ))}
                            </select>
                            {/* 오른쪽 period or numeric */}
                            {(f.rightType === 'MA' || f.rightType === 'RSI') && (
                                <input
                                    type="number"
                                    placeholder="period"
                                    value={f.rightPeriod}
                                    onChange={e => handleFilterChange(f.id, 'rightPeriod', e.target.value)}
                                    style={{ width: '60px' }}
                                />
                            )}
                            {f.rightType === 'Number' && (
                                <input
                                    type="number"
                                    placeholder="값"
                                    value={f.rightPeriod}
                                    onChange={e => handleFilterChange(f.id, 'rightPeriod', e.target.value)}
                                    style={{ width: '60px' }}
                                />
                            )}

                            {/* 삭제 버튼 */}
                            {filters.length > 1 && (
                                <button className="remove-button" onClick={() => removeFilter(f.id)}>X</button>
                            )}
                        </div>
                    ))}
                </div>

                {/* 버튼 그룹 */}
                <div className="button-group">
                    <div className="button-group-left">
                        <button className="reset-button" onClick={resetFilters}>초기화</button>
                    </div>
                    <div className="button-group-right">
                        <button className="add-button" onClick={addFilter}>+ 조건 추가</button>
                        <button className="apply-button" onClick={applyFilters}>검색</button>
                    </div>
                </div>
            </div>

            {/* 결과 테이블 */}
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
                            results.map((item, idx) => (
                                <tr key={idx}>
                                    <td>{idx + 1}</td>
                                    <td>{item.market}</td>
                                    <td>
                                        <a
                                            href={getUpbitLink(item.market)}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                        >
                                            링크
                                        </a>
                                    </td>
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
