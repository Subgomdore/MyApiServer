import React from 'react';
import { motion } from 'framer-motion';
import { Outlet } from 'react-router-dom';
import '../css/layouts/MainContentsLayout.css'; // CommonLayout 스타일 정의

function MainContentsLayout() {
    return (
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
    );
}

export default MainContentsLayout;
