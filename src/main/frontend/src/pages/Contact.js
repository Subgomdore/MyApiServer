import React from 'react';
import { motion } from 'framer-motion';

function Contact() {
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
        <div style={{ padding: '20px', textAlign: 'center', width: '80%', margin: '0 auto' }}>
            <h1 style={{ fontSize: '36px', color: '#4CAF50', marginBottom: '20px' }}>Contact Us</h1>
            <p style={{ fontSize: '18px', color: '#333' }}>
                저희에게 궁금한 사항이 있거나, 제휴 및 협력에 대해 문의하고 싶으시면 언제든지 연락주세요.
            </p>

            <motion.div
                className="contact-boxes"
                variants={container}
                initial="hidden"
                animate="visible"
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '30px' }}
            >
                <motion.div
                    variants={item}
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px',
                        height: '250px',
                    }}
                >
                    <h3 style={{ color: '#2196F3' }}>Email Us</h3>
                    <p>support@insH-MyLab.com으로 언제든지 문의하세요.</p>
                </motion.div>

                <motion.div
                    variants={item}
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px',
                        height: '250px',
                    }}
                >
                    <h3 style={{ color: '#ff5722' }}>Call Us</h3>
                    <p>+82-123-456-789로 전화주시면 친절히 답변해드립니다.</p>
                </motion.div>

                <motion.div
                    variants={item}
                    style={{
                        background: '#f9f9f9',
                        padding: '40px',
                        borderRadius: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                        width: '300px',
                        height: '250px',
                    }}
                >
                    <h3 style={{ color: '#9C27B0' }}>Visit Us</h3>
                    <p>서울특별시 강남구 테헤란로 123, insH-MyLab 빌딩</p>
                </motion.div>
            </motion.div>
        </div>
    );
}

export default Contact;
