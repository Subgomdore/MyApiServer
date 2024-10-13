import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FaChevronDown, FaList } from 'react-icons/fa';
import { FcInfo } from "react-icons/fc";
import { motion } from 'framer-motion';
import '../css/layouts/SidebarLayout.css'; // CommonLayout 스타일 정의

function SidebarLayout() {
    const [isOpen, setIsOpen] = useState(false);

    const toggleAccordion = () => {
        setIsOpen(!isOpen);
    };

    return (
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
                            <li><Link to="/api/upbit/list"> <FcInfo /> 업비트 API </Link></li>
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
                            <li><Link to="#"> <FcInfo /> 나만의 툰코 </Link></li>
                            <li><a href="#"><FcInfo /> 나만의 누누티비 </a></li>
                            <li><a href="#"><FcInfo /> 메모장 맨날 열기 귀찮아서 게시판씀 </a></li>
                        </ul>
                    )}
                </motion.div>
            </div>
        </aside>
    );
}

export default SidebarLayout;
