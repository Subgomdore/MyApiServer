import React, { useState } from 'react';
import { Link, Outlet } from 'react-router-dom';  // Link와 Outlet 추가
import { motion } from 'framer-motion';
import './css/Sidebar.css'; // Sidebar 스타일 정의

function Layout() {
    const [isOpen, setIsOpen] = useState(false);

    const toggleAccordion = () => {
        setIsOpen(!isOpen);
    };

    return (
        <motion.div
            className="layout"
            initial={{ y: -1000 }}
            animate={{ y: 0 }}
            transition={{
                type: "spring",
                duration: 1.2,
                bounce: 0.3, // 바운스 효과 적용
            }}
        >
            {/* 헤더 */}
            <header className="header">
                <div className="logo">insH-MyLab</div>
                <nav>
                    <Link to="/">Home</Link>  {/* Link로 변경 */}
                    <a href="#">About</a>
                    <a href="#">Contact</a>
                </nav>
            </header>

            {/* 사이드바 & 메인 콘텐츠 */}
            <div className="content">
                <aside className="sidebar">
                    <div className="accordion-item">
                        <button onClick={toggleAccordion} className="accordion-title">
                            작업중인 API 목록 1
                        </button>
                        <motion.div
                            style={{ overflow: 'hidden' }}
                            initial={{ height: 0, opacity: 0 }}
                            animate={{ height: isOpen ? 'auto' : 0, opacity: isOpen ? 1 : 0 }}
                            transition={{ duration: 0.8, ease: "easeInOut" }}
                        >
                            {isOpen && (
                                <ul>
                                    <li><Link to="/api1">API 1: User Data</Link></li>  {/* Link로 변경 */}
                                    <li><a href="#">API 2: Product List</a></li>
                                    <li><a href="#">API 3: Order Management</a></li>
                                </ul>
                            )}
                        </motion.div>
                    </div>
                </aside>

                {/* `Outlet`을 통해 동적으로 메인 콘텐츠 렌더링 */}
                <motion.div
                    className="main-content"
                    initial={{ y: -100, opacity: 0 }}  // 위에서 내려오고 투명하게 시작
                    animate={{ y: 0, opacity: 1 }}     // 제자리에서 점점 보임
                    transition={{
                        type: "spring",  // 스프링 애니메이션 사용
                        duration: 1.5,   // 애니메이션 시간
                        bounce: 0.4,     // 바운스 효과 정도
                    }}
                >
                    <Outlet />  {/* 현재 라우트에 따른 컴포넌트 렌더링 */}
                </motion.div>
            </div>

            {/* 푸터 */}
            <footer className="footer">
                <p>&copy; 2024 My API Blog. All rights reserved.</p>
            </footer>
        </motion.div>
    );
}

export default Layout;
