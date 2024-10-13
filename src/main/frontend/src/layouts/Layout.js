import React, { useState, useEffect } from 'react';
import { Link, Outlet } from 'react-router-dom';
import { FaChevronDown, FaHome, FaList, FaUser } from 'react-icons/fa'; // 아이콘 추가
import { FcInfo } from "react-icons/fc";
import { motion } from 'framer-motion';
import './css/Layout.css'; // Layout 스타일 정의

function Layout() {
    const [isOpen, setIsOpen] = useState(false);
    const [currentTime, setCurrentTime] = useState(new Date());

    const toggleAccordion = () => {
        setIsOpen(!isOpen);
    };

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
        <div className="layout">
            {/* 사이드바 */}
            <aside className="sidebar">
                <div className="logo">insH-MyLab !</div>
                <div className="accordion-item">
                    <button onClick={toggleAccordion} className="accordion-title">
                        <FaList /> 트레이드 <FaChevronDown />
                    </button>
                    <motion.div
                        style={{ overflow: 'hidden' }}
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: isOpen ? 'auto' : 0, opacity: isOpen ? 1 : 0 }}
                        transition={{ duration: 0.8, ease: "easeInOut" }}
                    >
                        {isOpen && (
                            <ul className="sidebar-menu">
                                <li><Link to="/api/upbit/list"> <FcInfo /> 업비트 API  </Link></li>
                                <li><a href="#"><FcInfo /> 제2의 툰코 </a></li>
                                <li><a href="#"><FcInfo /> 나만의 누누티비 </a></li>
                            </ul>
                        )}
                    </motion.div>
                    <button onClick={toggleAccordion} className="accordion-title">
                        <FaList /> 컨텐츠 <FaChevronDown />
                    </button>
                    <motion.div
                        style={{ overflow: 'hidden' }}
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: isOpen ? 'auto' : 0, opacity: isOpen ? 1 : 0 }}
                        transition={{ duration: 0.8, ease: "easeInOut" }}
                    >
                        {isOpen && (
                            <ul className="sidebar-menu">
                                <li><Link to="#"> <FcInfo /> 나만의 툰코  </Link></li>
                                <li><a href="#"><FcInfo /> 나만의 누누티비 </a></li>
                                <li><a href="#"><FcInfo /> 메모장 맨날 열기 귀찮아서 게시판씀 </a></li>
                            </ul>
                        )}
                    </motion.div>
                </div>
            </aside>

            <div className="main-wrapper">
                {/* 헤더 */}
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

                {/* 메인 콘텐츠 */}
                <motion.div
                    className="main-content"
                    initial={{ y: -100, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    transition={{
                        type: "spring",
                        duration: 1.5,
                        bounce: 0.4,
                    }}
                    style={{
                        overflowY: 'auto',
                        padding: '20px',
                        flex: 1,
                    }}
                >
                    <Outlet />
                </motion.div>
            </div>
        </div>
    );
}

export default Layout;
