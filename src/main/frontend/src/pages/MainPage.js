import React from 'react';
import { motion } from 'framer-motion'; // framer-motion 라이브러리 추가
import { useNavigate } from 'react-router-dom'; // 페이지 이동을 위한 react-router-dom 추가

function MainPage(props) {
    const navigate = useNavigate(); // 페이지 이동을 위한 useNavigate 훅 사용

    const container = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                delayChildren: 0.6,
                staggerChildren: 0.6,
            }
        }
    };

    const item = {
        hidden: { y: -50, opacity: 0 },
        visible: { y: 0, opacity: 1 },
        hover: { scale: 1.05 }, // 마우스 오버 시 약간의 확대 효과
        tap: { scale: 0.95 } // 클릭 시 약간의 축소 효과
    };

    return (
        <div style={{ padding: '20px', textAlign: 'center', width: '80%', margin: '0 auto' }}>
            <h1 style={{ fontSize: '36px', color: '#4CAF50', marginBottom: '20px' }}>insH-MyLab에 오신 것을 환영합니다!</h1>
            <p style={{ fontSize: '18px', color: '#333' }}>
                이곳은 API와 관련된 최신 정보와 업데이트를 제공하는 메인 콘텐츠 영역입니다.
                좌측 메뉴를 통해 다양한 API들을 확인하고, 필요한 리소스를 탐색해보세요!
            </p>

            {/* 전체 박스를 감싸는 컨테이너에 애니메이션 적용 */}
            <motion.div
                className="api-boxes"
                variants={container}
                initial="hidden"
                animate="visible"
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '30px' }}
            >
                {/* 각각의 박스에 고정된 크기와 애니메이션 설정 */}
                <motion.div
                    variants={item}
                    whileHover="hover"
                    whileTap="tap"
                    onClick={() => navigate('/api/upbit/list')} // 클릭 시 페이지 이동
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px', // 고정된 너비
                        height: '250px', // 고정된 높이
                        cursor: 'pointer' // 마우스 포인터 변경
                    }}
                >
                    <h3 style={{ color: '#2196F3' }}>API 1</h3>
                    <p>사용자 데이터를 탐색하고, 계정을 쉽게 관리할 수 있습니다.</p>
                </motion.div>

                <motion.div
                    variants={item}
                    whileHover="hover"
                    whileTap="tap"
                    onClick={() => navigate('/api2')} // 클릭 시 페이지 이동
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px', // 고정된 너비
                        height: '250px', // 고정된 높이
                        cursor: 'pointer' // 마우스 포인터 변경
                    }}
                >
                    <h3 style={{ color: '#ff5722' }}>API 2</h3>
                    <p>제품 목록을 확인하고, 재고 관리를 간편하게 할 수 있습니다.</p>
                </motion.div>

                <motion.div
                    variants={item}
                    whileHover="hover"
                    whileTap="tap"
                    onClick={() => navigate('/api3')} // 클릭 시 페이지 이동
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px', // 고정된 너비
                        height: '250px', // 고정된 높이
                        cursor: 'pointer' // 마우스 포인터 변경
                    }}
                >
                    <h3 style={{ color: '#9C27B0' }}>API 3</h3>
                    <p>주문을 원활하게 처리하고, 배송 상태를 추적할 수 있습니다.</p>
                </motion.div>
            </motion.div>
        </div>
    );
}

export default MainPage;
