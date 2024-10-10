import React from 'react';
import { motion } from 'framer-motion';
import '../layouts/css/about.css'; // CSS 파일을 import

function AboutPage() {
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
        visible: { y: 0, opacity: 1 }
    };

    return (
        <div className="about-container">
            <motion.div initial="hidden" animate="visible" variants={container}>

                {/* 이름 및 간단한 소개 */}
                <motion.div variants={item} className="about-header">
                    <h1>홍길동</h1>
                    <p>아무거또몰라요 개발자 | GPT로 날로만드는 개발자입니다. </p>
                </motion.div>

                {/* 연락처 정보 */}
                <motion.div variants={item} className="about-section">
                    <h2>연락처 정보</h2>
                    <ul>
                        <li><strong>이메일:</strong> honggildong@example.com</li>
                        <li><strong>전화번호:</strong> +82-10-1234-5678</li>
                        <li><strong>위치:</strong> 서울, 대한민국</li>
                        <li><strong>LinkedIn:</strong> linkedin.com/in/honggildong</li>
                        <li><strong>GitHub:</strong> github.com/honggildong</li>
                    </ul>
                </motion.div>

                {/* 경력 사항 */}
                <motion.div variants={item} className="about-section">
                    <h2>경력 사항</h2>
                    <div className="experience-item">
                        <h3>ABC Corp, 시니어 개발자</h3>
                        <p>2018년 1월 - 현재</p>
                        <ul>
                            <li>확장성 높은 웹 애플리케이션 개발, React와 Node.js 사용</li>
                            <li>프로젝트 요구사항 정의 및 크로스 기능 팀과 협업</li>
                            <li>주니어 개발자 멘토링 및 코드 리뷰</li>
                        </ul>
                    </div>
                    <div className="experience-item">
                        <h3>XYZ Solutions, 풀스택 개발자</h3>
                        <p>2015년 3월 - 2017년 12월</p>
                        <ul>
                            <li>클라이언트 및 서버 측 애플리케이션 개발 및 유지보수</li>
                            <li>JavaScript, Python, AWS를 활용한 프로젝트 진행</li>
                            <li>최적화 기법을 통해 성능과 확장성을 30% 개선</li>
                        </ul>
                    </div>
                </motion.div>

                {/* 기술 스택 */}
                <motion.div variants={item} className="about-section">
                    <h2>기술 스택</h2>
                    <ul>
                        <li><strong>언어:</strong> JavaScript, Python, Java</li>
                        <li><strong>프레임워크:</strong> React, Node.js, Express, Django</li>
                        <li><strong>도구:</strong> Git, Docker, AWS, Jenkins</li>
                        <li><strong>데이터베이스:</strong> MongoDB, PostgreSQL, MySQL</li>
                    </ul>
                </motion.div>

                {/* 학력 사항 */}
                <motion.div variants={item} className="about-section">
                    <h2>학력 사항</h2>
                    <div className="education-item">
                        <h3>컴퓨터공학 학사</h3>
                        <p>ABC 대학교, 2011 - 2015</p>
                    </div>
                </motion.div>
            </motion.div>
        </div>
    );
}

export default AboutPage;
