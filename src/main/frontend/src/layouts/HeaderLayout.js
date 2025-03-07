import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FaHome, FaUser, FaList, FaSync } from 'react-icons/fa';
import '../css/layouts/HeaderLayout.css';

function HeaderLayout() {
    const [currentTime, setCurrentTime] = useState(new Date());
    const [visitorCount, setVisitorCount] = useState(null); // 방문자 수 (초기값 null)

    // 현재 시간을 업데이트하는 useEffect
    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentTime(new Date());
        }, 1000);
        return () => clearInterval(interval);
    }, []);


    // 시간을 문자열로 변환하는 함수
    const formatTime = (date) => date.toLocaleTimeString();

    return (
        <header className="header">
            <nav>
                <Link to="/"><FaHome /> Home</Link>
                <Link to="/about"><FaUser /> About</Link>
                <Link to="/contact"><FaList /> Contact</Link>
            </nav>
            <div className="info-container">
                <div className="current-time">⏰ 현재 시간: {formatTime(currentTime)}</div>
                <div className="visitor-count">
                    👥 오늘 방문자: {visitorCount !== null ? `${visitorCount}명` : '로딩 중...'}
                </div>
            </div>
        </header>
    );
}

export default HeaderLayout;
