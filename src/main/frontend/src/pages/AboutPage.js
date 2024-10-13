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
                    <h1>황인성</h1>
                    <p>아무거또몰라요 개발자 | GPT로 날로만드는 개발자입니다. </p>
                    <p>개발중인 프로젝트이지만 너무 휑해서 적어놨어요. </p>
                </motion.div>

                {/* 연락처 정보 */}
                <motion.div variants={item} className="about-section">
                    <h2>연락처 정보</h2>
                    <ul>
                        <li><strong>이메일:</strong> joy2is90@gmail.com / joy2is@naver.com</li>
                        <li><strong>전화번호:</strong> +82-10-3974-4656</li>
                        <li><strong>위치:</strong> 서울, 대한민국</li>
                        <li><strong>GitHub:</strong> github.com/Subgomdore </li>
                    </ul>
                </motion.div>

                {/* 경력 사항 */}
                <motion.div variants={item} className="about-section">
                    <h2>경력 사항</h2>
                    <div className="experience-item">
                        <h3>세포아소프트, 시니어 개발자</h3>
                        <p>2023년 1월 - 2024년 9월 (1년8개월) </p>
                        <ul>
                            <li> DX사업부 전자인장팀 SaaS 운영서비스 담당 </li>
                            <li> On-Premise 지원 및 솔루션 공통기능 개발 </li>
                            <li> On-Premise 운영지원 및 트러블슈팅 </li>
                            <li> 전자인장팀 신규입사자 프레임워크 교육 및 지원</li>
                            <li> 참여프로젝트 : 동아제약 / 포스코플로우 / 이루다 / 이토추마루베니 / SBW생명과학 / 한국정보화진흥원(NIA)</li>
                            <li>  BASS / 코오롱LSI / 세포아소프트 / 현대회계법인 / 세종테크노파크 / COSMAX </li>


                        </ul>
                    </div>
                </motion.div>

                {/* 기술 스택 */}
                <motion.div variants={item} className="about-section">
                    <h2>기술 스택</h2>
                    <ul>
                        <li><strong>언어:</strong> JavaScript, Java </li>
                        <li><strong>프레임워크:</strong> React, SpringBoot</li>
                        <li><strong>도구:</strong> Git, Docker, AWS, Jenkins</li>
                        <li><strong>데이터베이스:</strong> Oracle, MS SQL, MariaDB</li>
                    </ul>
                </motion.div>

                {/* 학력 사항 */}
                <motion.div variants={item} className="about-section">
                    <h2>학력 사항</h2>
                    <div className="education-item">
                        <h3>사우고등학교 졸업</h3>
                        <p>2006 - 2008</p>
                    </div>
                </motion.div>
            </motion.div>
        </div>
    );
}

export default AboutPage;
