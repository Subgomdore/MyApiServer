import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FaHome, FaUser, FaList } from 'react-icons/fa';
import '../css/layouts/HeaderLayout.css'; // CommonLayout 스타일 정의

function HeaderLayout() {
    const [currentTime, setCurrentTime] = useState(new Date());

    // 매초마다 시간을 업데이트하는 useEffect 훅
    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentTime(new Date());
        }, 1000); // 1초마다 갱신

        // 컴포넌트가 언마운트 될 때 interval 정리
        return () => clearInterval(interval);
    }, []);

    // 시간을 문자열로 변환하는 함수
    const formatTime = (date) => {
        return date.toLocaleTimeString(); // 시간:분:초 형식
    };

    return (
        <header className="header">
            <nav>
                <Link to="/"><FaHome /> Home</Link>
                <Link to="/about"><FaUser /> About</Link>
                <Link to="/contact"><FaList /> Contact</Link>
            </nav>
            <div className="current-time">
                현재 시간: {formatTime(currentTime)}
            </div>
        </header>
    );
}

export default HeaderLayout;
