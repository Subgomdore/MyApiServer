import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

function MainPage() {
    const navigate = useNavigate();

    // 컨테이너 애니메이션 설정
    const container = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                delayChildren: 0.3, // 각 자식 요소의 등장 딜레이
                staggerChildren: 0.2, // 자식 요소들의 등장 간격
            }
        }
    };

    // 각 박스 애니메이션 설정
    const item = {
        hidden: { y: -50, opacity: 0 }, // 초기 상태
        visible: { y: 0, opacity: 1, transition: { type: 'spring', stiffness: 120 } }, // 등장 시 상태
        hover: { scale: 1.05 }, // 마우스 오버 시 확대
        tap: { scale: 0.95 } // 클릭 시 축소
    };

    return (
        <div style={{ padding: '20px', textAlign: 'center', width: '80%', margin: '0 auto' }}>
            <h1 style={{ fontSize: '36px', color: '#4CAF50', marginBottom: '20px' }}>
                insH-MyLab에 오신 것을 환영합니다!
            </h1>
            <p style={{ fontSize: '18px', color: '#333' }}>
                뚝딱뚝딱 공사중입니다 . 쾅쾅 from MainPage.js
            </p>

            <motion.div
                className="api-boxes"
                variants={container}
                initial="hidden"
                animate="visible"
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '30px' }}
            >
                <motion.div
                    variants={item}
                    whileHover="hover"
                    whileTap="tap"
                    onClick={() => navigate('/api/upbit/list')}
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px',
                        height: '250px',
                        cursor: 'pointer',
                    }}
                >
                    <h3 style={{ color: '#2196F3' }}> 업비트 검색기 </h3>
                    <p> HTS의 기능을 코인거래에도 지원하기 위해 <br/> 개발중에 있습니다.</p>
                </motion.div>

                <motion.div
                    variants={item}
                    whileHover="hover"
                    whileTap="tap"
                    onClick={() => navigate('/api/webtoon')}
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px',
                        height: '250px',
                        cursor: 'pointer',
                    }}
                >
                    <h3 style={{ color: '#ff5722' }}>API 2</h3>
                    <p> - 빈 페이지 입니당 -</p>
                </motion.div>

                <motion.div
                    variants={item}
                    whileHover="hover"
                    whileTap="tap"
                    onClick={() => navigate('/api3')}
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px',
                        height: '250px',
                        cursor: 'pointer',
                    }}
                >
                    <h3 style={{ color: '#9C27B0' }}>API 3</h3>
                    <p> - 빈페이지 입니당 -</p>
                </motion.div>
            </motion.div>
        </div>
    );
}

export default MainPage;
