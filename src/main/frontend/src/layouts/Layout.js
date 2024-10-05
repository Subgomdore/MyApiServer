import React, {useState} from 'react';
import {Link, Outlet} from 'react-router-dom';
import {FaChevronDown, FaHome, FaList, FaUser} from 'react-icons/fa'; // 아이콘 추가
import {FcInfo} from "react-icons/fc";
import {motion} from 'framer-motion';
import './css/Layout.css'; // Layout 스타일 정의

function Layout() {
    const [isOpen, setIsOpen] = useState(false);

    const toggleAccordion = () => {
        setIsOpen(!isOpen);
    };

    return (
        <div className="layout">
            {/* 사이드바 */}
            <aside className="sidebar">
                <div className="logo">insH-MyLab</div>
                <div className="accordion-item">
                    <button onClick={toggleAccordion} className="accordion-title">
                        <FaList /> 개발중이에요!! <FaChevronDown />
                    </button>
                    <motion.div
                        style={{ overflow: 'hidden' }}
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: isOpen ? 'auto' : 0, opacity: isOpen ? 1 : 0 }}
                        transition={{ duration: 0.8, ease: "easeInOut" }}
                    >
                        {isOpen && (
                            <ul className="sidebar-menu">
                                <li><Link to="/api/upbit/list"><FcInfo /> Upbit ...ing </Link></li>
                                <li><a href="#"><FcInfo /> API 2 ... </a></li>
                                <li><a href="#"><FcInfo /> API 3 ... </a></li>
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
