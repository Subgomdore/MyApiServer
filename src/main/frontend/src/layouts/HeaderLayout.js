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

    // 방문자 수를 서버에서 가져오는 함수
    const fetchVisitorCount = async () => {
        try {
            const response = await fetch('/api/visitors/today/count');
            if (!response.ok) throw new Error('방문자 수를 불러오지 못했습니다.');
            const data = await response.json();
            setVisitorCount(data);
        } catch (error) {
            console.error("🚨 방문자 수 가져오기 오류:", error);
        }
    };

    // 페이지가 로드될 때 한 번만 실행
    useEffect(() => {
        fetchVisitorCount();
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
