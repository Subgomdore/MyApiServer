import React from 'react';
import SidebarLayout from './SidebarLayout';
import HeaderLayout from './HeaderLayout';
import MainContentsLayout from './MainContentsLayout';
import '../css/layouts/CommonLayout.css'; // CommonLayout 스타일 정의

function CommonLayout() {
    return (
        <div className="layout">
            {/* 사이드바 */}
            <SidebarLayout />

            <div className="main-wrapper">
                {/* 헤더 */}
                <HeaderLayout />

                {/* 메인 콘텐츠 */}
                <MainContentsLayout />
            </div>
        </div>
    );
}

export default CommonLayout;
