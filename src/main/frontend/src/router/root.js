import {lazy, Suspense} from "react";
import CommonLayout from "../layouts/CommonLayout"; // CommonLayout 컴포넌트 추가
const { createBrowserRouter } = require("react-router-dom");

const Loading = <div>Loading...</div>;

const Main = lazy(() => import("../pages/MainPage"));
const API_1 = lazy(() => import("../pages/upbit/UpbitMainPage"));
const API_2 = lazy(() => import("../pages/webtoon/WebToonPage"));
const AboutPage = lazy(() => import("../pages/AboutPage"));
const Contact = lazy(() => import("../pages/Contact"));
const FilterPopup = lazy(() => import("../components/FilterPopup")); // 팝업 컴포넌트 추가

const root = createBrowserRouter([
    {
        path: "/",
        element: <CommonLayout />,
        children: [
            {
                path: "",
                element: <Suspense fallback={Loading}><Main /></Suspense>
            },
            {
                path: "api/upbit/list",
                element: <Suspense fallback={Loading}><API_1 /></Suspense>
            },
            {
                path: "api/webtoon",
                element: <Suspense fallback={Loading}><API_2 /></Suspense>
            },
            {
                path: "/about", // 고정헤더 About
                element: <Suspense fallback={Loading}><AboutPage /></Suspense>
            },
            {
                path: "/Contact", // 고정헤더 Contract
                element: <Suspense fallback={Loading}><Contact /></Suspense>
            },
        ]
    }
    ,
    {
        path: "/filter-popup",  // 팝업은 CommonLayout에서 독립된 경로
        element: <Suspense fallback={Loading}><FilterPopup /></Suspense>
    }
]);

export default root;
